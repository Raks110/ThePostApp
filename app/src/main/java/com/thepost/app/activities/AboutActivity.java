package com.thepost.app.activities;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.thepost.app.R;

import java.util.Date;

@Keep
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        //Toolbar initialization (line 33-39)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebView view = findViewById(R.id.articleWebView);

        //Load Dark Web-Page or Light Web-Page based on Device Theme
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_NO:
                view.loadUrl("https://app.themitpost.com/about");
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                view.loadUrl("https://app.themitpost.com/about/dark");
                break;
        }


        //Handles zoom level on initial load
        view.getSettings().setUseWideViewPort(true);
        view.setInitialScale(1);

        //Show loading animation
        setLoadingValue(true);

        //Handle responses to the WebView
        view.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageCommitVisible (WebView view, String url){

                setLoadingValue(false);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

                super.onReceivedError(view, request, error);
                showError();

            }
        });
    }

    /**
     *
     * @param isLoading: decides if the Loading Screen/Animation is to be shown (true) or hidden (false).
     */

    private void setLoadingValue(boolean isLoading){

        if(isLoading){
            findViewById(R.id.articleWebView).setVisibility(View.GONE);
            findViewById(R.id.loadingScreenWebview).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.loadingScreenWebview).setVisibility(View.GONE);
            findViewById(R.id.articleWebView).setVisibility(View.VISIBLE);
        }

    }

    /**
     * If the web page does not load, then this function is called to display the error.
     */

    private void showError(){

        findViewById(R.id.articleWebView).setVisibility(View.GONE);
        findViewById(R.id.loadingScreenWebview).setVisibility(View.GONE);
        findViewById(R.id.webViewError).setVisibility(View.VISIBLE);
    }

    //Handle the back button in the action bar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.stay, R.anim.push_out_left);
            return true;
        }
        else{
            return super.onOptionsItemSelected(item);
        }
    }

    //Necessary to show animation even when user presses system back button to quit activity
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.push_out_left);
    }
}
