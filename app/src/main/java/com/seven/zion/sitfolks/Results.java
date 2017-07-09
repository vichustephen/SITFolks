package com.seven.zion.sitfolks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Stephen on 07-Mar-17.
 */

public class Results extends AppCompatActivity {
    private WebView results;
     ProgressBar Pbar;

    String firstUrl = "http://coe.sethu.ac.in/results.php";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_results);
        results = (WebView)findViewById(R.id.results_view);
        Pbar = (ProgressBar) findViewById(R.id.pB1);
        results.setWebViewClient(new Mybrowser(Pbar));
        results.loadUrl("http://coe.sethu.ac.in/results.php");
        results.setWebChromeClient(new WebChromeClient());
        results.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        results.getSettings().setLoadsImagesAutomatically(true);
        results.getSettings().setJavaScriptEnabled(true);
        results.getSettings().setBuiltInZoomControls(true);
        results.getSettings().setDisplayZoomControls(false);

    }
    private class Mybrowser extends WebViewClient{
        private ProgressBar progressBar;
        public Mybrowser(ProgressBar progressBar)
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
        if (results.canGoBack())
            results.goBack();
        else
            super.onBackPressed();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)&&results.canGoBack())
        {
            results.goBack();
        }
        return super.onKeyDown(keyCode, event);
    }
}

