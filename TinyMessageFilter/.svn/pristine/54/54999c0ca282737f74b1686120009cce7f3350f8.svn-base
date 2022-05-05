package smf.local;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.ITelephony;
import smf.local.Helpers.CacheHelper;
import smf.local.Helpers.DbHelper;
import smf.local.Helpers.ErrorHelper;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

public class PhoneCallReceiver extends BroadcastReceiver {
    Context context = null;
    public static final String PHONE_STATE = "android.intent.action.PHONE_STATE";
    private ITelephony telephonyService;
    private String lastCaller;
    private Date lastCall;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        DbHelper.Initialize(context);
        ErrorHelper.Initialize(context);
        try
        {
            if (!intent.getAction().equals(PHONE_STATE)) return;

            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            long diff = lastCall.getTime() - new Date().getTime();
            //if (lastCaller == number && ( == 1000))

            Boolean reject = isToBeRejected(number);
            if (!reject) return;

            TelephonyManager telephony = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
                Class c = Class.forName(telephony.getClass().getName());
                Method m = c.getDeclaredMethod("getITelephony");
                m.setAccessible(true);
                telephonyService = (ITelephony) m.invoke(telephony);
                //telephonyService.silenceRinger();
                telephonyService.endCall();

            DbHelper.insertRejectedCall(number);
        }
        catch (Exception e)
        {
            ErrorHelper.ShowError(e);
        }
    }

    public boolean isToBeRejected(String number)
    {
        List<String> ignoredPoorPeople = CacheHelper.getIgnoredCallersList();
        if (ignoredPoorPeople == null || ignoredPoorPeople.size() == 0) return false;

        if (CacheHelper.containsRegExp(ignoredPoorPeople, number))
            return true;

        return false;
    }
}