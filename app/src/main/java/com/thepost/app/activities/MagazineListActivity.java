package com.thepost.app.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.thepost.app.R;
import com.thepost.app.adapters.MagazineAdapter;
import com.thepost.app.models.MagazineModel;
import com.thepost.app.remotes.ApiUtils;
import com.thepost.app.remotes.MagazinesAPIService;
import com.thepost.app.utils.ClickListener;
import com.thepost.app.utils.RecyclerTouchListener;
import com.thepost.app.utils.TinyDB;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class MagazineListActivity extends AppCompatActivity {

    static List<MagazineModel> magazines;
    static List<MagazineModel> magazinesStash;
    private RecyclerView allRecyclerView;
    private RecyclerView stashRecyclerView;
    private MagazineAdapter stashAdapter;
    private MagazineAdapter allAdapter;

    private MagazinesAPIService mAPIService;

    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazine_list);

        tinyDB = new TinyDB(this);
        mAPIService = ApiUtils.getMagazinesAPIService();

        //TODO: Replace below with function call to get all magazines from endpoint
        showLoading();
        getAllMagazines();
    }

    //Necessary to show animation even when user presses system back button to quit activity
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.push_out_left);
    }

    /**
     *
     * @param stashSet the String based objects that are in the user's cache memory that represent the ID of the magazines read by the user
     * @param stashPages the Page Model in the user's cache memory (//TODO: Future feature to allow user to continue reading from where he/she left off)
     * @return A new list that contains the MagazineModel objects corresponding to the IDs found in stashSet
     */

    private List<MagazineModel> getStash(ArrayList<String> stashSet, ArrayList<Integer> stashPages){

        List<MagazineModel> stash = new ArrayList<>();

        for(int i=0;i<stashSet.size();i++){

            for(int j=0;j<magazines.size();j++){

                MagazineModel model = magazines.get(j);

                if(model.getId().equals(stashSet.get(i))){

                    model.setPageNum(stashPages.get(i));
                    stash.add(model);
                }
                else{

                    model.setPageNum(0);
                }
            }
        }

        return stash;
    }

    private void initList(){

        magazines = new ArrayList<>();

        MagazineModel model = new MagazineModel();
        model.setTitle("Of Marks & Memories");
        model.setPdfLink("https://firebasestorage.googleapis.com/v0/b/mit-post-244d7.appspot.com/o/Of%20Marks%20%26%20Memories%E2%80%94A%20Freshers%E2%80%99%20Guide%20to%20Manipal_compressed.pdf?alt=media&token=e2430ea0-7574-485f-9017-f45c43df8d58");
        model.setDate("2019-7-30");
        model.setId("01");
        model.setImageURL("https://i.ibb.co/zXdHt0V/Document-page-001.jpg");
        model.setContent("Issue 4");

        magazines.add(model);

        MagazineModel model2 = new MagazineModel();
        model2.setTitle("Year 60, and Counting");
        model2.setPdfLink("https://firebasestorage.googleapis.com/v0/b/mit-post-244d7.appspot.com/o/Mag3_compressed.pdf?alt=media&token=f0840a67-27a5-42f9-b1bd-d076fc07e6db");
        model2.setDate("2019-2-30");
        model2.setId("02");
        model2.setImageURL("https://i.ibb.co/VJRpQdq/Mag3-compressed-page-001.jpg");
        model2.setContent("Issue 3");

        magazines.add(model2);

        MagazineModel model4 = new MagazineModel();
        model4.setTitle("Year 60, and Counting");
        model4.setPdfLink("https://firebasestorage.googleapis.com/v0/b/mit-post-244d7.appspot.com/o/Mag3_compressed.pdf?alt=media&token=f0840a67-27a5-42f9-b1bd-d076fc07e6db");
        model4.setDate("2019-2-30");
        model4.setId("03");
        model4.setImageURL("https://i.ibb.co/VJRpQdq/Mag3-compressed-page-001.jpg");
        model4.setContent("Issue 3");

        magazines.add(model4);

        MagazineModel model3 = new MagazineModel();
        model3.setTitle("Of Marks & Memories");
        model3.setPdfLink("https://firebasestorage.googleapis.com/v0/b/mit-post-244d7.appspot.com/o/Of%20Marks%20%26%20Memories%E2%80%94A%20Freshers%E2%80%99%20Guide%20to%20Manipal_compressed.pdf?alt=media&token=e2430ea0-7574-485f-9017-f45c43df8d58");
        model3.setDate("2019-7-30");
        model3.setId("04");
        model3.setImageURL("https://i.ibb.co/zXdHt0V/Document-page-001.jpg");
        model3.setContent("Issue 4");

        magazines.add(model3);
    }

    @Override
    public void onResume(){

        super.onResume();
        if(stashAdapter != null)
            stashAdapter.notifyDataSetChanged();
    }

    /**
     * Gets all magazines as a List of MagazineModel using Retrofit.
     */

    private void getAllMagazines() {

        mAPIService.getMagazines().enqueue(new Callback<List<MagazineModel>>() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<List<MagazineModel>> call, Response<List<MagazineModel>> response) {

                if (response.isSuccessful()) {

                    magazines = response.body();

                    if(magazines == null){

                        showError("Check your internet connection and try again.");
                        return;
                    }

                    ArrayList<String> stash = tinyDB.getListString("stash");
                    ArrayList<Integer> pages = tinyDB.getListInt("pages");

                    if(stash == null || stash.size() == 0 || pages == null || pages.size() == 0){

                        findViewById(R.id.yourStash).setVisibility(View.GONE);
                    }
                    else {

                        magazinesStash = getStash(stash, pages);

                        findViewById(R.id.yourStash).setVisibility(View.VISIBLE);

                        stashRecyclerView = findViewById(R.id.yourStashRecycler);
                        stashAdapter = new MagazineAdapter(magazinesStash);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(MagazineListActivity.this, LinearLayoutManager.HORIZONTAL, false);

                        stashRecyclerView.setLayoutManager(layoutManager);
                        stashRecyclerView.setAdapter(stashAdapter);

                        stashRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(MagazineListActivity.this, stashRecyclerView, new ClickListener() {
                            @Override
                            public void onClick(View view, int position) {

                                ArrayList<String> stash = tinyDB.getListString("stash");
                                ArrayList<Integer> pages = tinyDB.getListInt("pages");

                                String temp = stash.get(stash.size()-1);
                                stash.remove(stash.size()-1);
                                stash.add(0, temp);

                                int tempInt = pages.get(pages.size()-1);
                                pages.remove(pages.size()-1);
                                pages.add(0, tempInt);

                                tinyDB.putListString("stash", stash);
                                tinyDB.putListInt("pages", pages);

                                Intent intent = new Intent(MagazineListActivity.this, MagazineActivity.class);

                                intent.putExtra("id", magazinesStash.get(position).getId());
                                intent.putExtra("isStash", true);
                                intent.putExtra("position", position);

                                startActivity(intent);
                                overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }
                        }));

                    }

                    allRecyclerView = findViewById(R.id.olderMagazinesRecycler);
                    allAdapter = new MagazineAdapter(magazines);

                    allRecyclerView.setLayoutManager(new GridLayoutManager(MagazineListActivity.this, 2));
                    allRecyclerView.setAdapter(allAdapter);

                    allRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(MagazineListActivity.this, allRecyclerView, new ClickListener() {
                        @Override
                        public void onClick(View view, int position) {


                            ArrayList<String> stash = tinyDB.getListString("stash");
                            ArrayList<Integer> pages = tinyDB.getListInt("pages");

                            if(stash == null){

                                stash = new ArrayList<>();
                            }
                            if(pages == null){

                                pages = new ArrayList<>();
                            }

                            stash.add(magazines.get(position).getId());
                            pages.add(magazines.get(position).getPageNum());

                            tinyDB.putListString("stash", stash);
                            tinyDB.putListInt("pages", pages);

                            Intent intent = new Intent(MagazineListActivity.this, MagazineActivity.class);

                            intent.putExtra("id", magazines.get(position).getId());
                            intent.putExtra("isStash", false);
                            intent.putExtra("position", position);

                            startActivity(intent);
                            overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));


                    showContent();

                } else {

                    showError("Check your internet connection and try again.");
                }
            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<List<MagazineModel>> call, Throwable t) {

                showError("Check your internet connection and try again.");
            }
        });
    }

    /**
     * Show loading screen and hide everything else.
     */

    private void showLoading() {

        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        findViewById(R.id.empty).setVisibility(View.GONE);
        findViewById(R.id.yourStash).setVisibility(View.GONE);
        findViewById(R.id.olderMagazines).setVisibility(View.GONE);
    }

    /**
     * Show the articles (called after the list of articles is ready)
     */

    private void showContent() {

        findViewById(R.id.loading).setVisibility(View.GONE);
        findViewById(R.id.empty).setVisibility(View.GONE);
        findViewById(R.id.olderMagazines).setVisibility(View.VISIBLE);
    }

    /**
     * @param message This will be printed as the Error Message for the user to read.
     */

    private void showError(String message) {

        findViewById(R.id.loading).setVisibility(View.GONE);
        findViewById(R.id.yourStash).setVisibility(View.GONE);
        findViewById(R.id.olderMagazines).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.emptyMsg)).setText(message);
        findViewById(R.id.empty).setVisibility(View.VISIBLE);
    }
}
