/**
 * 
 */
package smf.local.CustomTypes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import smf.local.Rules.RuleModel;
import smf.local.Helpers.CacheHelper;
import smf.local.Helpers.DbHelper;
import smf.local.Helpers.SystemAccess;
import smf.local.Messages.BlockedMessagesList;
import smf.local.Messages.MessageModel;
import smf.local.R;
import smf.local.SmsReceiver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vitaly.Chupaev
 *
 */
public class SmsMessageEx
{
    private final Integer notificationMessageLength = 100;
    private Context context;
    private static NotificationManager notificationManager;

    public static Integer NOTIFICATION_ID = 7701;
    public static Integer SPAM_NOTIFICATION_ID = 7702;
    public SmsMessage originalSms;
	public String body;
	public String sender;
    public String rawSender;
	public long timeStamp;
    public boolean isSaved = false;
    public RuleModel file = null;
	
	public SmsMessageEx(SmsMessage sms, String mergedBody, Context context)
	{
		originalSms = sms;
		body = mergedBody;
		sender = sms.getDisplayOriginatingAddress();
        rawSender = sms.getOriginatingAddress();
		timeStamp = sms.getTimestampMillis();
        this.context = context;

        DbHelper.Initialize(context);
        file = CacheHelper.getFile(rawSender);
    }
	
	public String getTitle(int unreadMessagesCount)
	{
		return unreadMessagesCount > 1
                ? ""
                : SystemAccess.getContactName(originalSms.getDisplayOriginatingAddress());
	}
	
	public String getDisplayMessage(int unreadMessagesCount)
	{
        return unreadMessagesCount > 1
                ? unreadMessagesCount + " unread messages"
                : body.length() > notificationMessageLength
                    ? body.substring(0, notificationMessageLength) + "..."
                    : body.toString();
	}

    public boolean isKnown()
    {
        if (CacheHelper.contains(SystemAccess.getPhoneNumbers(), rawSender))
            return true;

        if (CacheHelper.contains(SystemAccess.getSenderNumbers(), rawSender))
            return true;

        return false;
    }

    public boolean isInBlackList()
    {
        List<String> black = CacheHelper.getBlackList();
        if (black == null || black.size() == 0) return false;

        if (CacheHelper.containsRegExp(black, rawSender))
            return true;

        return false;
    }

    public boolean hasBadContent(StringBuilder usedRule)
    {
        List<String> bps = CacheHelper.getBlockBodyPatterns(rawSender);
        if (bps.size() == 0) return false;

        return CacheHelper.containsRegExp(bps, body, usedRule);
    }

    public boolean isInMuteList()
    {
        List<String> mute = CacheHelper.getSilentList();
        if (mute == null || mute.size() == 0) return false;

        if (CacheHelper.containsRegExp(mute, rawSender))
            return true;

        return false;
    }

/*    public boolean isInFavorities()
    {
        List<String> favs = CacheHelper.getFavorities();
        if (favs == null || favs.size() == 0) return false;

        if (CacheHelper.containsRegExp(favs, rawSender))
            return true;

        return false;
    }*/

    public boolean isDuplicate(SmsMessageEx sms)
    {
        if (sms == null) return false;
        return body.equals(sms.body) && sender.equals(sms.sender);
    }

    public boolean notify(int iconId)
    {
        boolean canAbort = false;
        DbHelper.addTrace("Sms sent time: " + new Date(timeStamp) + ", system time: " + DbHelper.getTimeStamp());

        if (SmsReceiver.userPrefs.isShowSentTime())
        {
            body = "[" +  DbHelper.getTimeStamp("yyyy-MM-dd H H:mm", timeStamp)  + "] " + body;
        }

        SystemAccess.putSmsToInbox(
                context.getContentResolver(),
                new MessageModel(sender, body, DbHelper.getTimeStampMillis()));

        canAbort = true;

        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("content://mms-sms/conversations/" + getSmsThreadId()));

        showNotification(iconId, smsIntent);
        return canAbort;
    }

    public int getSmsThreadId()
    {
        int req_thread_id = 0;

        Uri inboxQueryUri = Uri.parse("content://sms");
        Cursor cursor = context.getContentResolver().query(
                inboxQueryUri,
                new String[] { "_id", "thread_id", "address", "person", "date", "body", "type" },
                null, null, null);

        while (cursor.moveToNext())
        {
            int thread_id = cursor.getInt(1);
            String address = cursor.getString(cursor.getColumnIndex("address"));

            if(sender.equals(address))
                {
                req_thread_id = thread_id;
            }
        }

        return req_thread_id;
    }

    public void notifyOfBlockedSms(Integer icon)
    {
        Intent myIntent = new Intent(context, BlockedMessagesList.class);
        showNotification(icon, myIntent);
    }

    public void showNotification(Integer icon, Intent myIntent)
    {
        int unreadMessagesCount = getMessagesCount(icon);

        String senderName = getTitle(0);

        Notification notification = new Notification(icon, senderName, System.currentTimeMillis());

        if (notificationManager == null)
            notificationManager  =  (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String notificationTitle = getTitle(unreadMessagesCount);
        String notificationText = getDisplayMessage(unreadMessagesCount);

        DbHelper.addTrace("Create notification intent.");

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context.getApplicationContext(),
                0, myIntent, 0);

        if (isKnown() && !isInMuteList())
        {
            notification.defaults = Notification.DEFAULT_ALL;
        }

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notification.setLatestEventInfo(context,
                notificationTitle,
                notificationText,
                pendingIntent);

        Integer notificationId = (icon == R.drawable.plane_spam) ? SPAM_NOTIFICATION_ID : NOTIFICATION_ID;
        notificationManager.notify(notificationId, notification);
        DbHelper.addTrace("Notified.");
    }

    public void saveSpamMessage()
    {
        ContentValues v = new ContentValues();
        v.put(DbHelper.SENDER_COL, sender);
        v.put(DbHelper.BODY_COL, body);
        v.put(DbHelper.DATE_COL, timeStamp);

        List<ContentValues> list = new ArrayList<ContentValues>();
        list.add(v);
        DbHelper.insertIntoTable(DbHelper.SPAM_TABLE, list);
        isSaved = true;
    }

    public static SmsMessageEx getSystemSms(Bundle extras, Context context)
    {
        // Get received SMS array
        Object[] smsExtra = (Object[]) extras.get( SystemAccess.SMS_EXTRA_NAME );
        StringBuilder body = new StringBuilder();
        SmsMessage origSms = null;

        // Merge long sms
        for ( int i = 0; i < smsExtra.length; ++i )
        {
            origSms = SmsMessage.createFromPdu((byte[])smsExtra[i]);
            body.append(origSms.getMessageBody());
        }

        SmsMessageEx sms = new SmsMessageEx(origSms, body.toString(), context);

        return sms;
    }

    private int getMessagesCount(int iconId)
    {
        return (iconId == R.drawable.plane_spam)
                ? DbHelper.getNewSpamCount()
                : SystemAccess.getNewMessagesCount();
    }

    public static boolean containsRegExp(String text, String searchFor)
    {
            Pattern pattern = Pattern.compile(searchFor);
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) return true;

        return false;
    }

}
