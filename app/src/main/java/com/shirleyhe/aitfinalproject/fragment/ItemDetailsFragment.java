package com.shirleyhe.aitfinalproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.shirleyhe.aitfinalproject.R;
import com.shirleyhe.aitfinalproject.WebBrowser;

/**
 * Created by shirleyhe on 12/11/16.
 */
public class ItemDetailsFragment extends Fragment {

    WebView webView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_details_fragment, null);

        webView = (WebView) rootView.findViewById(R.id.webView);
        //webView.setWebViewClient(new WebBrowser());

        String url = "http://ebay.com";
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);

        return rootView;
    }
}
