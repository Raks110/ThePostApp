package com.thepost.app.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.thepost.app.R;
import com.thepost.app.adapters.MagazineAdapter;
import com.thepost.app.models.MagazineModel.MagazineModel;
import com.thepost.app.models.MagazineModel.MagazineWrapperModel;
import com.thepost.app.remotes.ApiUtils;
import com.thepost.app.remotes.MagazinesAPIService;
import com.thepost.app.utils.ClickListener;
import com.thepost.app.utils.RecyclerTouchListener;
import com.thepost.app.utils.TinyDB;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class MagazineListActivity extends AppCompatActivity {

    static List<MagazineModel> magazinesQuarterly;
    static List<MagazineModel> magazinesAnnual;
    static List<MagazineModel> magazines;
    static List<MagazineModel> magazinesStash;
    private RecyclerView allRecyclerView;
    private RecyclerView stashRecyclerView;
    private RecyclerView annualRecyclerView;
    private MagazineAdapter stashAdapter;
    private MagazineAdapter allAdapter;
    private MagazineAdapter annualAdapter;

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
        magazinesAnnual = new ArrayList<>();
        magazinesQuarterly = new ArrayList<>();

        if(stashSet != null || stashPages != null) {
            for (int i = 0; i < stashSet.size(); i++) {

                for (int j = 0; j < magazines.size(); j++) {

                    MagazineModel model = magazines.get(j);

                    if (model.getId().equals(stashSet.get(i))) {

                        //TODO: make the page number live so that user can resume reading from where he/she stopped (pages list has helpful data)

                        model.setPageNum(0);
                        stash.add(model);

                    } else {

                        model.setPageNum(0);
                    }
                }
            }
        }

        for(int j=0;j<magazines.size();j++){

            MagazineModel model = magazines.get(j);

            if(model.getContent().toLowerCase().trim().equals("annual")){
                magazinesAnnual.add(model);
            }
            else{
                magazinesQuarterly.add(model);
            }
        }

        return stash;
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

        mAPIService.getMagazines().enqueue(new Callback<MagazineWrapperModel>() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<MagazineWrapperModel> call, Response<MagazineWrapperModel> response) {

                if (response.isSuccessful()) {

                    MagazineWrapperModel model = response.body();

                    magazines = model.getData();

                    if(magazines == null){

                        showError("Check your internet connection and try again.");
                        return;
                    }

                    ArrayList<String> stash = tinyDB.getListString("stash");
                    ArrayList<Integer> pages = tinyDB.getListInt("pages");

                    magazinesStash = getStash(stash, pages);

                    if(stash == null || stash.size() == 0 || pages == null || pages.size() == 0){

                        findViewById(R.id.yourStash).setVisibility(View.GONE);
                    }
                    else {

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
                                intent.putExtra("isStash", 0);
                                intent.putExtra("position", position);

                                startActivity(intent);
                                overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }
                        }));

                    }

                    allRecyclerView = findViewById(R.id.quarterlyMagazinesRecycler);
                    allAdapter = new MagazineAdapter(magazinesQuarterly);

                    allRecyclerView.setLayoutManager(new GridLayoutManager(MagazineListActivity.this, 2));
                    allRecyclerView.setAdapter(allAdapter);

                    allRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(MagazineListActivity.this, allRecyclerView, new ClickListener() {
                        @Override
                        public void onClick(View view, int position) {

                            ArrayList<String> stash = tinyDB.getListString("stash");
                            ArrayList<Integer> pages = tinyDB.getListInt("pages");

                            if(!isFoundInStash(magazinesQuarterly.get(position), stash)) {

                                if (stash == null) {

                                    stash = new ArrayList<>();
                                }
                                if (pages == null) {

                                    pages = new ArrayList<>();
                                }

                                stash.add(magazinesQuarterly.get(position).getId());
                                pages.add(magazinesQuarterly.get(position).getPageNum());

                                tinyDB.putListString("stash", stash);
                                tinyDB.putListInt("pages", pages);

                            }

                            Intent intent = new Intent(MagazineListActivity.this, MagazineActivity.class);

                            intent.putExtra("id", magazinesQuarterly.get(position).getId());
                            intent.putExtra("isStash", 1);
                            intent.putExtra("position", position);

                            startActivity(intent);
                            overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
                        }

                        @Override
                        public void onLongClick(View view, int position) {

                        }
                    }));

                    annualRecyclerView = findViewById(R.id.annualMagazinesRecycler);
                    annualAdapter = new MagazineAdapter(magazinesAnnual);

                    LinearLayoutManager layoutManager = new LinearLayoutManager(MagazineListActivity.this, LinearLayoutManager.HORIZONTAL, false);

                    annualRecyclerView.setLayoutManager(layoutManager);
                    annualRecyclerView.setAdapter(annualAdapter);

                    annualRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(MagazineListActivity.this, annualRecyclerView, new ClickListener() {
                        @Override
                        public void onClick(View view, int position) {

                            ArrayList<String> stash = tinyDB.getListString("stash");
                            ArrayList<Integer> pages = tinyDB.getListInt("pages");


                            if(!isFoundInStash(magazinesAnnual.get(position), stash)) {

                                if (stash == null) {

                                    stash = new ArrayList<>();
                                }
                                if (pages == null) {

                                    pages = new ArrayList<>();
                                }

                                stash.add(magazinesAnnual.get(position).getId());
                                pages.add(magazinesAnnual.get(position).getPageNum());

                                tinyDB.putListString("stash", stash);
                                tinyDB.putListInt("pages", pages);
                            }

                            Intent intent = new Intent(MagazineListActivity.this, MagazineActivity.class);

                            intent.putExtra("id", magazinesAnnual.get(position).getId());
                            intent.putExtra("isStash", 2);
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
            public void onFailure(Call<MagazineWrapperModel> call, Throwable t) {

                showError("Check your internet connection and try again.");
                Log.e("Error in Magazines", t.getMessage());
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
        findViewById(R.id.quarterlyMagazines).setVisibility(View.GONE);
        findViewById(R.id.annualMagazines).setVisibility(View.GONE);
    }

    /**
     * Show the articles (called after the list of articles is ready)
     */

    private void showContent() {

        findViewById(R.id.loading).setVisibility(View.GONE);
        findViewById(R.id.empty).setVisibility(View.GONE);
        findViewById(R.id.quarterlyMagazines).setVisibility(View.VISIBLE);
        findViewById(R.id.annualMagazines).setVisibility(View.VISIBLE);
    }

    /**
     * @param message This will be printed as the Error Message for the user to read.
     */

    private void showError(String message) {

        findViewById(R.id.loading).setVisibility(View.GONE);
        findViewById(R.id.yourStash).setVisibility(View.GONE);
        findViewById(R.id.annualMagazines).setVisibility(View.GONE);
        findViewById(R.id.quarterlyMagazines).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.emptyMsg)).setText(message);
        findViewById(R.id.empty).setVisibility(View.VISIBLE);
    }

    private boolean isFoundInStash(MagazineModel magazine, ArrayList<String> stash) {

        for(int i=0;i<stash.size();i++){

            if(stash.get(i).equals(magazine.getId())){

                return true;
            }
        }

        return false;
    }
}
