package com.shirleyhe.aitfinalproject;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by shirleyhe on 12/11/16.
 */
public class WebBrowser extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}