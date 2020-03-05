package com.thepost.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.google.android.material.card.MaterialCardView;
import com.thepost.app.R;
import com.thepost.app.models.MagazineModel;
import com.thepost.app.utils.TinyDB;

import org.ocpsoft.prettytime.PrettyTime;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;

public class MagazineActivity extends AppCompatActivity implements DownloadFile.Listener{

    private boolean showing;
    private boolean isStash;
    private int position;
    private int page;

    private Boolean isLoaded;

    private String url;
    private String date;
    private String title;

    private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine);

        showing = false;
        isLoaded = false;

        findViewById(R.id.pdfView).setVisibility(View.GONE);

        Intent intent = getIntent();

        isStash = intent.getBooleanExtra("isStash", false);
        position = intent.getIntExtra("position", 0);

        url = getList().get(position).getPdfLink();
        title = getList().get(position).getTitle();
        date = getList().get(position).getDate();
        page = getList().get(position).getPageNum();

        if(url == null)
            url = "";

        RemotePDFViewPager remotePDFViewPager =
                new RemotePDFViewPager(getApplicationContext(), url, this);

        findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exitMagazineView();
                finish();
                overridePendingTransition(R.anim.stay, R.anim.push_out_left);
            }
        });

    }

    //Necessary to show animation even when user presses system back button to quit activity
    @Override
    public void onBackPressed() {

        exitMagazineView();
        finish();
        overridePendingTransition(R.anim.stay, R.anim.push_out_left);
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        // That's the positive case. PDF Download went fine

        findViewById(R.id.loading).setVisibility(View.GONE);
        findViewById(R.id.pdfView).setVisibility(View.VISIBLE);

        pdfView = findViewById(R.id.pdfView);

        Log.e("Page Loading", page+"");

        pdfView.fromFile(new File(destinationPath))
                .swipeHorizontal(true)
                .pageFling(true)
                .onTap(new OnTapListener() {
                    @Override
                    public boolean onTap(MotionEvent e) {

                        if(!showing){
                            slideUp();
                        }
                        else{
                            slideDown();
                        }
                        return true;
                    }
                })
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {

                        isLoaded = true;
                        setUpSlider();
                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {

                        changePageNumber();
                    }
                })
                .defaultPage(page)
                .load();
    }

    @Override
    public void onFailure(Exception e) {
        // This will be called if download fails
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
        // You will get download progress here
        // Always on UI Thread so feel free to update your views here

        ((TextView) findViewById(R.id.progress)).setText((progress/total)*100 + "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void slideUp(){

        changePageNumber();

        showing = true;
        float pixels = getResources().getDimension(R.dimen.slider_slide);

        MaterialCardView view = findViewById(R.id.sliderView);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                -pixels);                // toYDelta
        animate.setDuration(250);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    private void slideDown(){

        showing = false;
        float pixels = getResources().getDimension(R.dimen.slider_slide);

        MaterialCardView view = findViewById(R.id.sliderView);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                -pixels,  // fromYDelta
                0);                // toYDelta
        animate.setDuration(250);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    private List<MagazineModel> getList(){

        if(isStash){
            return MagazineListActivity.magazinesStash;
        }
        else{
            return MagazineListActivity.magazines;
        }
    }

    private void setUpSlider(){

        Date dateParsed;

        try {
            dateParsed = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        }
        catch(ParseException e){
            dateParsed = new Date();
        }
        PrettyTime prettyTime = new PrettyTime();

        Log.e("Total Pages", pdfView.getPageCount() + "");

        ((TextView)findViewById(R.id.currentPage)).setText(pdfView.getCurrentPage() + 1 +"");
        ((TextView)findViewById(R.id.totalPages)).setText(pdfView.getPageCount()+"");
        ((TextView)findViewById(R.id.titleMag)).setText(title);
        ((TextView)findViewById(R.id.publishMag)).setText("published " + prettyTime.format(dateParsed));
    }

    private void changePageNumber(){

        ((TextView)findViewById(R.id.currentPage)).setText(pdfView.getCurrentPage()+"");
    }

    private void exitMagazineView(){

        if(isLoaded) {

            TinyDB tinyDB = new TinyDB(this);

            ArrayList<String> stash = tinyDB.getListString("stash");
            ArrayList<Integer> pages = tinyDB.getListInt("pages");

            if (isStash) {

                pages.remove(position);
                pages.add(position, pdfView.getCurrentPage());
            } else {
                pages.remove(pages.size() - 1);
                pages.add(pdfView.getCurrentPage());
            }

            tinyDB.putListString("stash", stash);
            tinyDB.putListInt("pages", pages);
        }
    }

}
