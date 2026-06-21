package com.example.autosms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                for (Object pdu : pdus) {
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                    String sender = sms.getOriginatingAddress();
                    String message = sms.getMessageBody();
                    Log.d("SMS","WORK");
                    if (message != null && message.trim().equalsIgnoreCase("/stat")) {

                        String js = "javascript:getPredictionForNextToken("+sender+")";
                        MainActivity.webView.post(() -> MainActivity.webView.loadUrl(js));


                    }
                }
            }
        }
    }
}
