package smf.local;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import smf.local.CustomTypes.References;
import smf.local.CustomTypes.UserPrefs;
import smf.local.Helpers.DbHelper;
import smf.local.Helpers.ErrorHelper;
import smf.local.Messages.BlockedMessagesList;
import smf.local.SimpleActivities.Statistics;

import java.util.List;

public class SmsReceiver extends BroadcastReceiver {
    public static UserPrefs userPrefs;
    public static Boolean lastMessageIsForSpamBox = false;
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public static Context receivingContext;

    static final Object mStartingServiceSync = new Object();
    static PowerManager.WakeLock mStartingService;
    private static SmsReceiver sInstance;

    private static Handler saveHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            refreshActivities();
        }
    };

    public static SmsReceiver getInstance() {
        if (sInstance == null) {
            sInstance = new SmsReceiver();
        }
        return sInstance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (receivingContext == null) receivingContext = context;
        SmsReceiver.userPrefs = new UserPrefs(SmsReceiver.receivingContext);

        DbHelper.Initialize(context);
        ErrorHelper.Initialize(context);

        if (!intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            DbHelper.addTrace("ABORTED! Action: " + intent.getAction() + ", Flags: " + intent.getFlags());
            abortBroadcast();
            return;
        } else {
            // do nothing
            if (!userPrefs.useBlackList()) {
                ErrorHelper.Log("Filter is off, skip processing " + DbHelper.getTimeStamp() + ".");
                return;
            }

            // or do everything
            abortBroadcast();

            intent.setClass(context, SmsService.class);
            intent.putExtra("result", getResultCode());
            startingService(context, intent);
        }
    }

    /**
     * Start the service to process the current event notifications, acquiring
     * the wake lock before returning to ensure that the service will run.
     */
    public static void startingService(Context context, Intent intent) {
        try {
            synchronized (mStartingServiceSync) {
                if (mStartingService == null) {
                    PowerManager pm =
                            (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                    mStartingService = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                            "StartingAlertService");
                    mStartingService.setReferenceCounted(false);
                }
                mStartingService.acquire();
                context.startService(intent);
            }
        } catch (Exception ex) {
            ErrorHelper.ShowError(ex);
        }
    }

    /**
     * Called back by the service when it has finished processing notifications,
     * releasing the wake lock if the service is now stopping.
     */
    public static void finishingService(Service service, int startId) {
        synchronized (mStartingServiceSync) {
            if (mStartingService != null) {
                if (service.stopSelfResult(startId)) {
                    mStartingService.release();
                    saveHandler.sendEmptyMessage(0);
                }
            }
        }
    }

    private static void refreshActivities() {
        try {
            ActivityManager activityManager = (ActivityManager) SmsReceiver.receivingContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> runningTaskInfoList = activityManager.getRunningTasks(1);
            ComponentName componentName = runningTaskInfoList.get(0).topActivity;

            if (componentName.getClassName().equals(Statistics.class.getName()) && References.StatisticsActivity != null)
                References.StatisticsActivity.onRestart();
            else if (componentName.getClassName().equals(BlockedMessagesList.class.getName()) && References.BlockedMessagesActivity != null)
                References.BlockedMessagesActivity.loadData();
        } catch (Exception ex) {
            ErrorHelper.Log("RefreshActivities Exception: " + ex.getMessage());
        }
    }
}