package smf.local;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import smf.local.CustomTypes.SmsMessageEx;
import smf.local.CustomTypes.UserPrefs;
import smf.local.Helpers.DbHelper;
import smf.local.Helpers.ErrorHelper;
import smf.local.Helpers.SystemAccess;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly Chupaev
 * Date: 02.02.13
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
public class SmsService extends Service {
    private static final String TAG = "SmsService";

    private ServiceHandler mServiceHandler;
    private Looper mServiceLooper;

    private int mResultCode;

    @Override
    public void onCreate() {
        try {
            HandlerThread thread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();

            mServiceLooper = thread.getLooper();
            mServiceHandler = new ServiceHandler(mServiceLooper);
        } catch (Exception ex) {
            ErrorHelper.ShowError(ex);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            Message msg = mServiceHandler.obtainMessage();

            msg.arg1 = startId;
            msg.obj = intent;
            mServiceHandler.sendMessage(msg);

        } catch (Exception ex) {
            ErrorHelper.ShowError(ex);
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        /**
         * Handle incoming transaction requests.
         * The incoming requests are initiated by the MMS Server or by the MMS Client itself.
         */
        @Override
        public void handleMessage(Message msg) {
            try {
                int serviceId = msg.arg1;
                Intent intent = (Intent) msg.obj;
                if (intent != null) {
                    SmsMessageEx currentMessage = null;
                    SmsReceiver.lastMessageIsForSpamBox = false;
                    SmsMessageEx sms = null;

                    try {
                        SmsReceiver.userPrefs = new UserPrefs(SmsReceiver.receivingContext);

                        SystemAccess.Initialize(SmsReceiver.receivingContext);
                        ErrorHelper.Initialize(SmsReceiver.receivingContext);
                        DbHelper.Initialize(SmsReceiver.receivingContext);

                        // Get SMS map from Intent
                        Bundle extras = intent.getExtras();

                        if (extras != null) {
                            ErrorHelper.Log("Received SMS " + DbHelper.getTimeStamp());

                            sms = SmsMessageEx.getSystemSms(extras, SmsReceiver.receivingContext);
                            String address = sms.originalSms.getOriginatingAddress();

                            // If BlackList is not used we should not pass here!
                            if (sms.isInBlackList()) {
                                ErrorHelper.Log("found in blacklist");
                                keepAndNotify(sms, true);
                            } else if (SmsReceiver.userPrefs.blockUnknown() && !sms.isKnown()) {
                                ErrorHelper.Log("treated as unknown");
                                keepAndNotify(sms, true);
                            } else {
                                keepAndNotify(sms, false);
                            }
                        }
                    } catch (Exception ex) {
                        if (sms != null && !sms.isSaved) sms.saveSpamMessage();
                        ErrorHelper.ShowError(ex);
                    } finally {
                        ErrorHelper.Log("End of receive");
                    }
                }
                // NOTE: We MUST not call stopSelf() directly, since we need to
                // make sure the wake lock acquired by AlertReceiver is released.
                SmsReceiver.finishingService(SmsService.this, serviceId);
            } catch (Exception ex) {
                ErrorHelper.ShowError(ex);
            }
        }

        private void keepAndNotify(SmsMessageEx sms, boolean isSpam) {
            if (isSpam && SmsReceiver.userPrefs.keepSpam()) {
                sms.saveSpamMessage();
            }

            if (!sms.isKnown() && SmsReceiver.userPrefs.notifyOfBlockedMessages()) {
                SmsReceiver.lastMessageIsForSpamBox = true;
                sms.notifyOfBlockedSms(R.drawable.question);
            }

            if (!isSpam)// && SmsReceiver.userPrefs.showCustomIcons())
            {
                ErrorHelper.Log("abortBroadcast for custom notify");
                sms.notify(sms.isInFavorities() ? R.drawable.rose : R.drawable.paper_plane_24);
            }
        }
    }
}
