package com.seven.zion.sitfolks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class portal extends AppCompatActivity {

    private WebView portalV;
    ProgressBar Pbar2;

    String firstUrl = "http://coe.sethu.ac.in/results.php";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal);
        portalV = (WebView)findViewById(R.id.portal_view);
        Pbar2 = (ProgressBar)findViewById(R.id.pB2);
        portalV.setWebViewClient(new Mybrowser2(Pbar2));
        portalV.loadUrl("http://portal.sethu.ac.in");
        portalV.setWebChromeClient(new WebChromeClient());
        portalV.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        portalV.getSettings().setLoadsImagesAutomatically(true);
        portalV.getSettings().setBuiltInZoomControls(true);
        portalV.getSettings().setDisplayZoomControls(false);
        portalV.getSettings().setJavaScriptEnabled(true);

    }
    private class Mybrowser2 extends WebViewClient {
        private ProgressBar progressBar;
        public Mybrowser2(ProgressBar progressBar)
        {
            this.progressBar = progressBar;
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view,String url){
            view.loadUrl(url);
            view.getSettings().setJavaScriptEnabled(true);
            view.getSettings().setLoadsImagesAutomatically(true);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }
    @Override
    public void onBackPressed() {
        if (portalV.canGoBack())
            portalV.goBack();
        else
            super.onBackPressed();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)&& portalV.canGoBack())
        {
            portalV.goBack();
        }
        return super.onKeyDown(keyCode, event);
    }
}

