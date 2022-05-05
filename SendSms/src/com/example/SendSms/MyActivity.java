package com.example.SendSms;

import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MyActivity extends Activity {

    public static final String ACTION_SMS_SENT = "com.example.android.apis.os.SMS_SENT_ACTION";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final Context ctx = this;
        final EditText recipientTextEdit = (EditText) findViewById(R.id.sms_recipient);
        final EditText contentTextEdit = (EditText) findViewById(R.id.sms_content);

        // Watch for send button clicks and send text messages.
        Button sendButton = (Button) findViewById(R.id.sms_send_message);
        sendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SmsManager sms = SmsManager.getDefault();

                List<String> messages = sms.divideMessage(contentTextEdit.getText().toString());

                String recipient = recipientTextEdit.getText().toString();
                for (String message : messages)
                    sms.sendTextMessage(recipient, "vova", message, PendingIntent.getBroadcast(
                            ctx, 0, new Intent(ACTION_SMS_SENT), 0), null);
            }
        });
    }
}
