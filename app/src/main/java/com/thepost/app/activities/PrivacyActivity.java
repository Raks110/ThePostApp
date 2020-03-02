package com.thepost.app.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.MailTo;
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
public class PrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_privacy);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable drawable = toolbar.getNavigationIcon();
        assert drawable != null;

        WebView view = findViewById(R.id.articleWebView);

        //Load Dark Web-Page or Light Web-Page based on Device Theme
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_NO:
                view.loadUrl("https://app.themitpost.com/policy");
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                view.loadUrl("https://app.themitpost.com/policy/dark");
                break;
        }

        //Handle initial zoom state of the web page
        view.getSettings().setUseWideViewPort(true);
        view.getSettings().setJavaScriptEnabled(true);
        view.setInitialScale(1);

        setLoadingValue(true);

        //handle loading of the web page
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

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Handle clicking on Email ID displayed on the web page
                if (url.startsWith("mailto:")) {
                    final Activity activity = PrivacyActivity.this;
                    MailTo mt = MailTo.parse(url);
                    Intent i = newEmailIntent(mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                    activity.startActivity(i);
                    view.reload();
                    return true;
                }
                else {
                    view.loadUrl(url);
                }
                return true;
            }
        });
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

    //Necessary to show animation even when user presses system back button to quit activity
    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.stay, R.anim.push_out_left);
    }

    //Start an intent that requests for an Email Client from the OS
    private Intent newEmailIntent(String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }
}
