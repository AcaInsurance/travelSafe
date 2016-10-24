package com.aca.travelsafe.Widget;

import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Created by Marsel on 10/8/2016.
 */
public class MyWebView {

    public static WebView create(WebView webView, WebChromeClient webChromeClient) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true); // allow pinch to zooom
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebChromeClient(webChromeClient);
        webView.requestFocus();

        return webView;
    }
}
