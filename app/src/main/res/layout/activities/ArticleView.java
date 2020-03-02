package com.thepost.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;

import com.thepost.app.MainActivity;
import com.thepost.app.R;
import com.thepost.app.fragments.ArticlesFragment;

@Keep
public class ArticleView extends AppCompatActivity {

    public static boolean loadedTwice;

    private Intent shareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        switch(MainActivity.theme)
        {
            case 0:
                setTheme(R.style.AppThemeDark);
                break;

            case 1:
                setTheme(R.style.AppTheme);
                break;

            case 2:
                setTheme(R.style.AppTheme);
                break;
            case 3:
                setTheme(R.style.AppThemeVibrant);
                break;

            case 4:
                setTheme(R.style.AppThemeTransDark);
                break;
        }

        super.onCreate(savedInstanceState);

        if(loadedTwice)
            finish();

        Intent intent = getIntent();

        final int articleID = intent.getIntExtra("_id",0);
        String authorName = intent.getStringExtra("author");
        String title = intent.getStringExtra("title");
        String link = intent.getStringExtra("link");

        setContentView(R.layout.activity_article_view);
        WebView view = findViewById(R.id.articleWebView);

        if(MainActivity.theme == 1 || MainActivity.theme == 2 || MainActivity.theme == 4)
            view.loadUrl("https://app.themitpost.com/posts/render/" + articleID);
        else
            view.loadUrl("https://app.themitpost.com/posts/render/" + articleID + "/dark");

        view.getSettings().setUseWideViewPort(true);
        view.getSettings().setJavaScriptEnabled(true);
        view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
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

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                if(request.getUrl().toString().equalsIgnoreCase("https://app.themitpost.com/posts/render/" + articleID + "/share")){
                    startActivity(Intent.createChooser(shareIntent,"Choose how to share this awesome article"));
                }
                else if(request.getUrl().toString().contains("themitpost.com")){

                    for(int i=0;i< ArticlesFragment.LIST.size();i++){

                        if(request.getUrl().toString().toLowerCase().contains(ArticlesFragment.LIST.get(i).getLink().toLowerCase()) || ArticlesFragment.LIST.get(i).getLink().toLowerCase().contains(request.getUrl().toString().toLowerCase())){

                            Intent intentOwn = new Intent(ArticleView.this, ArticleView.class);
                            intentOwn.putExtra("author",ArticlesFragment.LIST.get(i).getAuthor().getName());
                            intentOwn.putExtra("title", ArticlesFragment.LIST.get(i).getTitle());
                            intentOwn.putExtra("link",ArticlesFragment.LIST.get(i).getLink());
                            intentOwn.putExtra("_id",ArticlesFragment.LIST.get(i).getId());
                            startActivity(intentOwn);
                            return true;
                        }
                    }

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(request.getUrl());
                    startActivity(i);
                }
                else{

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(request.getUrl());
                    startActivity(i);
                }
                return true;
            }

            @Override
            public void onLoadResource(WebView  view, String  url){
                //nothing here
            }
        });

        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String share = Html.fromHtml(title) + "\n\n" + link + "\n\n" + "Written by " + authorName + "\n" + "~The MIT Post";
        shareIntent.putExtra(Intent.EXTRA_TEXT, share);

        //findViewById(R.id.shareButton).setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed(){
        loadedTwice = true;
        finish();
        overridePendingTransition(R.anim.stay, R.anim.push_out_left);
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
}
