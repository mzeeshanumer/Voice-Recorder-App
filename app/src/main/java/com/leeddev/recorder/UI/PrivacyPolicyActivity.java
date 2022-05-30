package com.leeddev.recorder.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.leeddev.recorder.R;

public class PrivacyPolicyActivity extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        webView = (WebView) findViewById(R.id.wv_privacy_policy);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://www.leeddev.io/privacy-policy.html");

    }
}