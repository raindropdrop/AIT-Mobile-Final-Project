package com.shirleyhe.aitfinalproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.shirleyhe.aitfinalproject.R;
import com.shirleyhe.aitfinalproject.WebBrowser;
import com.shirleyhe.aitfinalproject.activity.RecognizeConceptsActivity;

/**
 * Created by shirleyhe on 12/11/16.
 */
public class ItemDetailsFragment extends Fragment {

    WebView webView;
    String passKeyWord = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_details_fragment, null);

        webView = (WebView) rootView.findViewById(R.id.webView);
        //webView.setWebViewClient(new WebBrowser());

        //getting passkeyword from recognizeconceptsactivity
        RecognizeConceptsActivity recognizeConceptsActivity = (RecognizeConceptsActivity) getActivity();
        passKeyWord = recognizeConceptsActivity.getPassKeyWord();

        //GET FROM BUNDLE
        //String passKeyWordBundle = getArguments().getString("passKeyWord");



        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //String url = "http://www.ebay.com/sch/i.html?_from=R40&_trksid=p2050601.m570.l1313.TR0.TRC0.H0.X"+passKeyWord+".TRS0&_nkw="+passKeyWord+"&_sacat=0";
        String url = "https://www.google.com/search?q="+passKeyWord;
        //Log.d("passKeyWord", passKeyWord+"yes");

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.getSettings().setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);
    }


    public void navigateToStringUrl(String url) {
        webView.loadUrl(url);
    }
}
