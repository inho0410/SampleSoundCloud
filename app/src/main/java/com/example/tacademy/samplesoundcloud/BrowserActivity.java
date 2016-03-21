package com.example.tacademy.samplesoundcloud;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BrowserActivity extends AppCompatActivity {

    WebView webView;
    public static final String CALLBACK_URL = "http://blog.naver.com/dlsgh0410";
    public static final String PARAM_CODE = "code";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        webView = (WebView)findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(CALLBACK_URL)) {
                    Uri uri = Uri.parse(url);
                    String code = uri.getQueryParameter("code");
                    Intent data = new Intent();
                    data.putExtra(PARAM_CODE, code);
                    setResult(RESULT_OK, data);
                    finish();
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        webView.setWebChromeClient(new WebChromeClient());

        Intent intent = getIntent();
        Uri data = intent.getData();
        String url = data.toString();
        webView.loadUrl(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.resumeTimers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.pauseTimers();
    }
}
