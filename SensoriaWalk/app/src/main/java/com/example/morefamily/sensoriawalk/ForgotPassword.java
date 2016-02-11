package com.example.morefamily.sensoriawalk;

/**
 * Created by MoreFamily on 12/14/2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class ForgotPassword extends Activity {

    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.webcontent);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://auth.sensoriafitness.com/authentication/forgotpassword");
    }

}