package com.thepost.app.activities;

import android.graphics.PorterDuff;
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

import com.thepost.app.MainActivity;
import com.thepost.app.R;

import java.util.Date;

import static com.thepost.app.utils.ContextUtils.getColorForTheme;

@Keep
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch(MainActivity.theme){

            case 0:
                setTheme(R.style.AppThemeDark);
                break;
            case 1:
                setTheme(R.style.AppTheme);
                break;
            case 2:
                setTheme(R.style.AppThemeOrange);
                break;
            case 3:
                setTheme(R.style.AppThemeVibrant);
                break;
            case 4:
                setTheme(R.style.AppThemeTransDark);
                break;
        }

        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable drawable = toolbar.getNavigationIcon();
        assert drawable != null;

        drawable.setColorFilter(getColorForTheme(MainActivity.theme), PorterDuff.Mode.SRC_ATOP);


        WebView view = findViewById(R.id.articleWebView);

        if(MainActivity.theme == 1 || MainActivity.theme == 2 || MainActivity.theme == 4)
            view.loadUrl("https://app.themitpost.com/about");
        else
            view.loadUrl("https://app.themitpost.com/about/dark");


        view.getSettings().setUseWideViewPort(true);
        view.setInitialScale(1);

        setLoadingValue(true);

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

    private void showError(){

        findViewById(R.id.articleWebView).setVisibility(View.GONE);
        findViewById(R.id.loadingScreenWebview).setVisibility(View.GONE);
        findViewById(R.id.webViewError).setVisibility(View.VISIBLE);
    }

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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.push_out_left);
    }

    boolean shouldFinish;

    @Override
    public void onPause() {

        shouldFinish = true;

        super.onPause();
        final Date date  = new Date();

        new Handler().post(new Runnable() {
            @Override
            public void run() {

                Date d = new Date();
                if(d.getTime() - date.getTime() >= 15*60*1000){

                    if(shouldFinish)
                        finish();
                }

            }
        });
    }

    @Override
    public void onResume(){

        super.onResume();
        shouldFinish = false;
    }

}
