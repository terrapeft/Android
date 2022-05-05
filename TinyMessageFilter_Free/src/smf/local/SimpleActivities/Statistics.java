package smf.local.SimpleActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import smf.local.CustomTypes.References;
import smf.local.CustomTypes.SmsMessageEx;
import smf.local.CustomTypes.UserPrefs;
import smf.local.Files.FileList;
import smf.local.Helpers.CacheHelper;
import smf.local.Helpers.DbHelper;
import smf.local.Helpers.ErrorHelper;
import smf.local.Helpers.SystemAccess;
import smf.local.Messages.BlockedMessagesList;
import smf.local.R;
import smf.local.SmsReceiver;
import smf.local.Traces.TracesList;

/**
 * Created with IntelliJ IDEA.
 * User: vitaly.chupaev
 * Date: 12/13/12
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class Statistics extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.statistics);
        References.StatisticsActivity = this;

        SmsReceiver.userPrefs = new UserPrefs(this);

        UpdateStatistics();
    }

    public void onRestart() {
        super.onRestart();
        UpdateStatistics();
    }

    public void onClick(View v) {
        try {
            // Handle item selection
            switch (v.getId()) {
                case R.id.prefsButton:
                    Intent pIntent = new Intent(this, PreferencesList.class);
                    startActivity(pIntent);
                    break;
                case R.id.traceButton:
                    Intent fIntent = new Intent(this, TracesList.class);
                    startActivity(fIntent);
                    break;
                case R.id.contactsButton:
                    Intent cIntent = new Intent(this, FileList.class);
                    startActivity(cIntent);
                    break;
                case R.id.spamButton:
                    SystemAccess.removeNotification(SmsMessageEx.SPAM_NOTIFICATION_ID);
                    Intent mIntent = new Intent(this, BlockedMessagesList.class);
                    startActivity(mIntent);
                    break;
                case R.id.aboutButton:
                    showInformationDialog();
                    break;
                case R.id.inboxButton:
                    SystemAccess.removeNotification(SmsMessageEx.NOTIFICATION_ID);
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intent.setClassName("com.android.mms", "com.android.mms.ui.ConversationList");
                    startActivity(intent);
                    break;
            }
        } catch (Exception ex) {
            ErrorHelper.ShowError(ex);
        }
    }

    private void UpdateStatistics() {
        DbHelper.Initialize(this);
        SystemAccess.Initialize(this);

        final TextView spamLbl = (TextView) findViewById(R.id.spamCount);
        int msgs = DbHelper.getSpamCount();
        int newMsgs = DbHelper.getNewSpamCount();
        spamLbl.setText(String.valueOf(newMsgs) + " (" + String.valueOf(msgs) + ")");

        final TextView inboxLbl = (TextView) findViewById(R.id.inboxCount);
        int newSms = SystemAccess.getNewMessagesCount();
        inboxLbl.setText(String.valueOf(newSms));

        final TextView blackLbl = (TextView) findViewById(R.id.blackCount);
        int blockCount = CacheHelper.getBlackList() == null ? 0 : CacheHelper.getBlackList().size();
        blackLbl.setText(String.valueOf(blockCount));

        final TextView favLbl = (TextView) findViewById(R.id.favCount);
        int loveCount = CacheHelper.getFavorities() == null ? 0 : CacheHelper.getFavorities().size();
        favLbl.setText(String.valueOf(loveCount));

        final TextView muteLbl = (TextView) findViewById(R.id.muteCount);
        int muteCount = CacheHelper.getSilentList() == null ? 0 : CacheHelper.getSilentList().size();
        muteLbl.setText(String.valueOf(muteCount));
    }

    private void showInformationDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("About...");
        alertDialog.setMessage(
                "I use Momenticons icons from http://momentumdesignlab.com " +
                        "and Liam McKay's icons from http://wefunction.com/contact/\n\n" +
                        "(C) Vitaly Chupaev, 2013" + "\nsubscriber113@yandex.ru");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });
        alertDialog.setIcon(R.drawable.about);
        alertDialog.show();
    }

}