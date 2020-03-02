package com.thepost.app.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;

import com.thepost.app.R;
import com.thepost.app.fragments.HomeFragment;
import com.thepost.app.utils.ContextUtils;

@Keep
public class ArticleView extends AppCompatActivity {

    private Intent shareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        //Parameters needed to render the web page
        final int articleID = intent.getIntExtra("_id",0);
        String authorName = intent.getStringExtra("author");
        String title = intent.getStringExtra("title");
        String link = intent.getStringExtra("link");

        setContentView(R.layout.activity_article_view);

        if(!ContextUtils.isNetworkAvailable(this)){
            showError();
            return;
        }

        WebView view = findViewById(R.id.articleWebView);

        //Load Dark Web-Page or Light Web-Page based on Device Theme
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_NO:
                view.loadUrl("https://app.themitpost.com/posts/render/" + articleID);
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                view.loadUrl("https://app.themitpost.com/posts/render/" + articleID + "/dark");
                break;
        }

        //Handles zoom level on initial load
        view.getSettings().setUseWideViewPort(true);
        view.setInitialScale(1);

        view.getSettings().setJavaScriptEnabled(true);
        view.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

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

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                //If the link belongs to The MIT Post, then search if the article is available to be viewed in the app itself.
                //LIST is the list of articles that holds all available articles.

                if(request.getUrl().toString().equalsIgnoreCase("https://app.themitpost.com/posts/render/" + articleID + "/share")){
                    startActivity(Intent.createChooser(shareIntent,"Choose how to share this awesome article"));
                }
                else if(request.getUrl().toString().contains("themitpost.com")){

                    for(int i = 0; i< HomeFragment.LIST.size(); i++){

                        if(request.getUrl().toString().toLowerCase().contains(HomeFragment.LIST.get(i).getLink().toLowerCase()) || HomeFragment.LIST.get(i).getLink().toLowerCase().contains(request.getUrl().toString().toLowerCase())){

                            Intent intentOwn = new Intent(ArticleView.this, ArticleView.class);
                            intentOwn.putExtra("author",HomeFragment.LIST.get(i).getAuthor().getName());
                            intentOwn.putExtra("title", HomeFragment.LIST.get(i).getTitle());
                            intentOwn.putExtra("link",HomeFragment.LIST.get(i).getLink());
                            intentOwn.putExtra("_id",HomeFragment.LIST.get(i).getId());
                            startActivity(intentOwn);
                            return true;
                        }
                    }

                    //If article isn't available, then open the Web Browser
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

        //Handle click on share button in the web page
        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String share = Html.fromHtml(title) + "\n\n" + link + "\n\n" + "Written by " + authorName + "\n" + "~Shared from The MIT Post app (for Android).";
        shareIntent.putExtra(Intent.EXTRA_TEXT, share);
    }

    //Necessary to show animation even when user presses system back button to quit activity
    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.stay, R.anim.push_out_left);
    }

    /**
     *
     * @param isLoading: decides if the Loading Screen/Animation is to be shown (true) or hidden (false).
     */

    private void setLoadingValue(boolean isLoading){

        if(isLoading){
            findViewById(R.id.empty).setVisibility(View.GONE);
            findViewById(R.id.articleWebView).setVisibility(View.GONE);
            findViewById(R.id.loading).setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.articleWebView).setVisibility(View.VISIBLE);
            findViewById(R.id.empty).setVisibility(View.GONE);
            findViewById(R.id.loading).setVisibility(View.GONE);
        }

    }

    /**
     * If the web page does not load, then this function is called to display the error.
     */

    private void showError(){

        findViewById(R.id.articleWebView).setVisibility(View.GONE);
        findViewById(R.id.empty).setVisibility(View.VISIBLE);
    }
}
