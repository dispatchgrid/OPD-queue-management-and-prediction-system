package com.example.autosms;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.role.RoleManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.ConsoleMessage;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SMS_ROLE = 101;

    static WebView webView;
    static SmsManager smsManager;
    private static final int SMS_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // or your layout file
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }


        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS
        }, 1);




        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            getWindow().setStatusBarColor(android.graphics.Color.BLACK);


        }
        webView = findViewById(R.id.webView);

        // Enable JavaScript
        webView.getSettings().setJavaScriptEnabled(true);

        // Optional: stay inside the WebView
        webView.setWebViewClient(new WebViewClient());


        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {


                String msg = consoleMessage.message();


                String[] parts = msg.split(":");

                if (parts.length >= 3 && parts[0].equals("WEBVIEWCOM")) {
                    String phone = parts[1].trim();


                    StringBuilder messageBuilder = new StringBuilder();
                    for (int i = 2; i < parts.length; i++) {
                        messageBuilder.append(parts[i]);
                        if (i < parts.length - 1) {
                            messageBuilder.append(":");
                        }
                    }
                    String message = messageBuilder.toString().trim();
                    Log.d("SMS", phone);
                    Log.d("SMS", message);


                    try {
                        Log.d("SMS", "onConsoleMessage: Triggered from here");
                        runOnUiThread(() -> sendSMS(phone, message));

                    }catch (Exception e){
                        Log.e("ERR", "onConsoleMessage: ", e);
                    }
                }


                return super.onConsoleMessage(consoleMessage);
            }

        });

        new Handler().postDelayed(() -> {
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.clearCache(true);
            webView.clearHistory();
            webView.loadUrl("https://dispatchgrid.github.io/OQMPS/counter-mobile.html");
        }, 2000);



        // Load your URL

    }
    private void sendSMS(String phone, String message) {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, message, null, null);
        Log.d("SMS", "SMS sent to " + phone);

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("PERM", "onRequestPermissionsResult: WORKED");
        } else {
            Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
