package com.thepost.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.thepost.app.MainActivity;
import com.thepost.app.R;
import com.thepost.app.adapters.SLCMAdapter;
import com.thepost.app.models.SlcmModel.BasicModel.SlcmBasicModel;
import com.thepost.app.remotes.ApiUtils;
import com.thepost.app.remotes.TokenAPIService;
import com.thepost.app.utils.ContextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.thepost.app.utils.ContextUtils.isNetworkAvailable;

public class LoggedInSlcmFragment extends Fragment {


    static Boolean loginStatus;
    static String regNo;
    static String password;
    static SlcmBasicModel slcmBasicModel;
    private SLCMAdapter slcmAdapter;
    static String semester;
    private List<String> photo_urls;
    private Activity activity;
    private SharedPreferences preferences;
    private View view;

    private Boolean showKey;

    public LoggedInSlcmFragment() {
        // Required empty public constructor
    }

    public static LoggedInSlcmFragment newInstance() {

        return new LoggedInSlcmFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_logged_in_slcm, container, false);

        activity = getActivity();

        view.findViewById(R.id.loadingScreenSlcmBasic).setVisibility(View.VISIBLE);

        preferences = getActivity().getSharedPreferences("thepostapp", Context.MODE_PRIVATE);

        assert getContext() != null;

        //If the user is not connected to internet, then show the user that SLCM data can not be retrieved.

        if (!isNetworkAvailable(getContext())) {

            view.findViewById(R.id.academicsRecycler).setVisibility(View.GONE);
            view.findViewById(R.id.SLCMEmptyAnimation).setVisibility(View.VISIBLE);

            view.findViewById(R.id.logout).setVisibility(View.GONE);

            Log.e("Logged In", "Network Unavailable");
            return view;
        }

        try {

            enableLogout();

            final RecyclerView recyclerView = view.findViewById(R.id.academicsRecycler);

            List<Integer> list_nums;

            initImages();
            list_nums = initNums();

            recyclerView.setVisibility(View.VISIBLE);

            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);

            slcmAdapter = new SLCMAdapter(slcmBasicModel.getAcademicDetails().get(0).getAttendance(), slcmBasicModel.getAcademicDetails().get(0).getInternalMarks(), getContext(), photo_urls, list_nums);
            recyclerView.setAdapter(slcmAdapter);

            view.findViewById(R.id.loadingScreenSlcmBasic).setVisibility(View.GONE);

            SlcmFragment.isLoaded = true;

            if (preferences.getBoolean("first_login", true)) {

                Toast.makeText(getContext(), "Tap on the cards or the times shown to view more info.", Toast.LENGTH_LONG).show();
                preferences.edit().putBoolean("first_login", false).apply();

                TokenAPIService apiService = ApiUtils.getTokenAPIService();

                String regNo = preferences.getString("regNo", "");
                String pass = preferences.getString("pass", "");

                if (!(regNo.trim().equals("") || pass.trim().equals(""))) {


                    HashMap<String, String> params = new HashMap<>();
                    params.put("regNumber", regNo);
                    params.put("pass", pass);
                    params.put("fcm_token", preferences.getString("fcm_token", ""));
                    params.put("action", "insert");
                    String strRequestBody = new Gson().toJson(params);

                    final RequestBody requestBody = RequestBody.create(MediaType.
                            parse("application/json"), strRequestBody);

                    apiService.sendToken(requestBody).enqueue(new Callback<Object>() {
                        @Override
                        @okhttp3.internal.annotations.EverythingIsNonNull
                        public void onResponse(Call<Object> call, Response<Object> response) {

                            if (response.isSuccessful()) {

                                Log.d("SlcmFragment", "Successfully inserted token");
                            }

                            if (!response.isSuccessful()) {

                                Log.e("SlcmFragment", "Failed to insert token.");
                            }

                        }

                        @Override
                        @okhttp3.internal.annotations.EverythingIsNonNull
                        public void onFailure(Call<Object> call, Throwable t) {

                            Log.e("SlcmFragment", "Failed to insert token.");
                        }
                    });
                }
            }
        } catch (Exception e) {

            Log.e("SlcmBasicFragment", "Error: " + e.getMessage());

            MainActivity.logged_static = false;

            if(((BottomNavigationView)getActivity().findViewById(R.id.nav_view)).getSelectedItemId() == R.id.navigation_slcm){

                ((BottomNavigationView)getActivity().findViewById(R.id.nav_view)).setSelectedItemId(R.id.navigation_slcm);
            }

        }

        return view;
    }

    private void initImages() {

        photo_urls = new ArrayList<>();

        photo_urls.add("https://i.ibb.co/Y7d2pyS/IMG-20190903-134223.jpg");
        photo_urls.add("https://i.ibb.co/WGqYYbg/IMG-20190903-134711-01.jpg");
        photo_urls.add("https://i.ibb.co/WznCJbX/IMG-20190903-134739.jpg");
        photo_urls.add("https://i.ibb.co/TP2rZ1C/IMG-20190903-134917-01-20190903143135832.jpg");
        photo_urls.add("https://i.ibb.co/h8QkY8c/IMG-20190903-135025.jpg");
        photo_urls.add("https://i.ibb.co/Xxwr8yF/IMG-20190903-135034.jpg");
        photo_urls.add("https://i.ibb.co/hf2Qk4n/IMG-20190903-135046.jpg");
        photo_urls.add("https://i.ibb.co/M9ZVYB2/IMG-20190903-135138.jpg");
        photo_urls.add("https://i.ibb.co/m4wdXdV/IMG-20190903-135319.jpg");
        photo_urls.add("https://i.ibb.co/dMjDfQB/IMG-20190903-135729.jpg");
        photo_urls.add("https://i.ibb.co/wp0XdJk/IMG-20190903-140003.jpg");
        photo_urls.add("https://i.ibb.co/yfzRHjv/IMG-20190903-140012.jpg");
        photo_urls.add("https://i.ibb.co/mzLDMNZ/IMG-20190903-140040.jpg");
        photo_urls.add("https://i.ibb.co/VmgSJ17/IMG-20190903-140413.jpg");
        photo_urls.add("https://i.ibb.co/Mhj1Q9H/IMG-20190903-140636.jpg");
        photo_urls.add("https://i.ibb.co/LtQFYBp/IMG-20190903-140653.jpg");
        photo_urls.add("https://i.ibb.co/GVt831t/IMG-20190903-140659.jpg");
        photo_urls.add("https://i.ibb.co/M8BrDXy/IMG-20190903-140707.jpg");
        photo_urls.add("https://i.ibb.co/LgQFBdM/IMG-20190903-140724.jpg");
    }

    /**
     * Gets the number of cards and assigns a background to each card.
     * This image is shown in the detailed view of each card.
     *
     * @return list of integers such that the URL at index i is the image loaded into CardView i.
     */

    private List<Integer> initNums() {

        List<Integer> hello = new ArrayList<>();

        for (int i = 0; i < slcmBasicModel.getAcademicDetails().get(0).getAttendance().size(); i++) {

            hello.add(i);
        }

        Collections.shuffle(hello);
        return hello;
    }

    /**
     * Makes the logout button visible, and handles its click event.
     * Remove the token from the database so no more notifications are sent to the device regarding SLCM.
     */

    private void enableLogout(){

        view.findViewById(R.id.logout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert getContext() != null;

                DialogPlus dialogPlus1;

                dialogPlus1 = DialogPlus.newDialog(getContext())
                        .setContentHolder(new ViewHolder(R.layout.dialog_logout_slcm))
                        .setCancelable(true)
                        .setExpanded(false)
                        .create();

                final DialogPlus dialogPlus = dialogPlus1;

                dialogPlus.show();

                View viewDialog = dialogPlus.getHolderView();
                viewDialog.findViewById(R.id.slcm_logout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialogPlus.dismiss();

                        view.findViewById(R.id.loadingScreenSlcmBasic).setVisibility(View.VISIBLE);

                        view.findViewById(R.id.logout).setVisibility(View.GONE);

                        TokenAPIService mAPIService = ApiUtils.getTokenAPIService();

                        String regNo = preferences.getString("regNo", "");
                        String pass = preferences.getString("pass", "");

                        HashMap<String, String> params = new HashMap<>();
                        params.put("regNumber", regNo);
                        params.put("pass", pass);
                        params.put("fcm_token", preferences.getString("fcm_token", ""));
                        params.put("action", "delete");
                        String strRequestBody = new Gson().toJson(params);

                        final RequestBody requestBody = RequestBody.create(MediaType.
                                parse("application/json"), strRequestBody);

                        mAPIService.sendToken(requestBody).enqueue(new Callback<Object>() {
                            @Override
                            @EverythingIsNonNull
                            public void onResponse(Call<Object> call, Response<Object> response) {

                                if (response.isSuccessful()) {

                                    Log.d("SlcmFragment", "Successfully deleted token");

                                    Toast.makeText(getContext(), "You have been logged out of SLCM.", Toast.LENGTH_LONG).show();

                                    SharedPreferences sharedPreferences = activity.getSharedPreferences("thepostapp", Context.MODE_PRIVATE);
                                    sharedPreferences.edit().clear().apply();

                                    view.findViewById(R.id.loadingScreenSlcmBasic).setVisibility(View.GONE);
                                    MainActivity.logged_static = false;

                                    if(((BottomNavigationView)getActivity().findViewById(R.id.nav_view)).getSelectedItemId() == R.id.navigation_slcm){
                                        ((BottomNavigationView)getActivity().findViewById(R.id.nav_view)).setSelectedItemId(R.id.navigation_slcm);
                                    }
                                }

                                if (!response.isSuccessful()) {

                                    Log.e("SlcmFragment", "Failed to delete token.");

                                    Toast.makeText(getContext(), "Please check your connection and try to logout again.", Toast.LENGTH_LONG).show();
                                    view.findViewById(R.id.logout).setVisibility(View.VISIBLE);

                                }

                            }

                            @Override
                            @EverythingIsNonNull
                            public void onFailure(Call<Object> call, Throwable t) {

                                Log.e("SlcmFragment", "Failed to delete token.");
                                Toast.makeText(getContext(), "Please check your connection and try to logout again.", Toast.LENGTH_LONG).show();
                                view.findViewById(R.id.logout).setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });

                viewDialog.findViewById(R.id.slcm_logout_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialogPlus.dismiss();
                    }
                });
            }
        });
    }
}
