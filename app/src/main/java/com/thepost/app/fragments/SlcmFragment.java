package com.thepost.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.thepost.app.MainActivity;
import com.thepost.app.R;
import com.thepost.app.activities.PrivacyActivity;
import com.thepost.app.models.SlcmModel.BasicModel.SlcmBasicModel;
import com.thepost.app.remotes.ApiUtils;
import com.thepost.app.remotes.SlcmAPIService;
import com.thepost.app.utils.ContextUtils;

import java.util.HashMap;
import java.util.concurrent.Executor;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class SlcmFragment extends Fragment {

    private View view;

    private Activity mActivity;

    private boolean showStuff;

    private boolean hardLogin;

    private SlcmAPIService mAPIService;

    public static boolean isLoaded;

    public SlcmFragment() {
        // Required empty public constructor
    }

    public static SlcmFragment newInstance() {
        return new SlcmFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        isLoaded = false;

        view = inflater.inflate(R.layout.fragment_slcm, container, false);
        assert mActivity!= null;

        hardLogin = false;

        view.findViewById(R.id.loadingScreenSlcm).setVisibility(View.VISIBLE);

        mAPIService = ApiUtils.getSlcmAPIService();

        SharedPreferences sharedPref = mActivity.getSharedPreferences("thepostapp", Context.MODE_PRIVATE);

        if(sharedPref.getBoolean("loggedIn",false)){

            view.findViewById(R.id.slcmLoginCard).setVisibility(View.GONE);
            view.findViewById(R.id.loadingScreenSlcm).setVisibility(View.VISIBLE);
            view.findViewById(R.id.loadingScreenSlcm).setVisibility(View.VISIBLE);

            isLoaded = false;

            sendPost(sharedPref.getString("regNo",""), sharedPref.getString("pass",""));
        }
        else {

            view.findViewById(R.id.slcmLoginCard).setVisibility(View.VISIBLE);
            view.findViewById(R.id.loadingScreenSlcm).setVisibility(View.GONE);

            isLoaded = true;

            view.findViewById(R.id.mainLoginButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    assert getContext()!= null;

                    hardLogin = true;

                    String regno = ((TextInputEditText) view.findViewById(R.id.email)).getText().toString().trim();

                    if (regno.length() != 9) {
                        view.findViewById(R.id.email).requestFocus();
                        ((TextInputEditText) view.findViewById(R.id.email)).setError("Please enter a valid registration number.", getResources().getDrawable(R.drawable.ic_error, getContext().getTheme()));
                    }
                    else {

                        String password = ((TextInputEditText) view.findViewById(R.id.password)).getText().toString().trim();
                        if (!TextUtils.isEmpty(regno) && !TextUtils.isEmpty(password)) {

                            if(!((CheckBox)view.findViewById(R.id.slcmAccept)).isChecked()){

                                Toast.makeText(getContext(),"Please read and accept our privacy policy before you continue.", Toast.LENGTH_LONG).show();
                                return;
                            }

                            view.findViewById(R.id.slcmLoginCard).setVisibility(View.GONE);
                            view.findViewById(R.id.loadingScreenSlcm).setVisibility(View.VISIBLE);

                            sendPost(regno, password);
                        } else {
                            view.findViewById(R.id.password).requestFocus();
                            ((TextInputEditText) view.findViewById(R.id.password)).setError("Please enter your password.", getResources().getDrawable(R.drawable.ic_error, getContext().getTheme()));
                        }
                    }
                }
            });
        }

        view.findViewById(R.id.slcmCondition).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getActivity() != null) {
                    Intent intent = new Intent(mActivity, PrivacyActivity.class);
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.pull_in_left, R.anim.stay);
                }
            }
        });

        return view;
    }

    private void sendPost(final String regNo, final String pass) {

        HashMap<String, String> params = new HashMap<>();
        params.put("regNumber", regNo);
        params.put("pass", pass);
        String strRequestBody = new Gson().toJson(params);

        final RequestBody requestBody = RequestBody.create(MediaType.
                parse("application/json"),strRequestBody);

        mAPIService.savePost(requestBody).enqueue(new Callback<SlcmBasicModel>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<SlcmBasicModel> call, Response<SlcmBasicModel> response) {

                assert mActivity != null;

                try {

                    if (response.isSuccessful()) {

                        LoggedInSlcmFragment.slcmBasicModel = response.body();
                        LoggedInSlcmFragment.loginStatus = true;
                        LoggedInSlcmFragment.regNo = regNo;
                        LoggedInSlcmFragment.password = pass;

                        assert response.body() != null;

                        if (response.body().getStatus()) {
                            SharedPreferences sharedPref = mActivity.getSharedPreferences("thepostapp", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("regNo", regNo);
                            editor.putString("pass", pass);
                            editor.putBoolean("loggedIn", true);
                            editor.apply();

                            //SlcmBasicFragment.semester = response.body().getSemester();

                            view.findViewById(R.id.loadingScreenSlcm).setVisibility(View.GONE);

                            MainActivity.logged_static = true;

                            if(((BottomNavigationView)getActivity().findViewById(R.id.nav_view)).getSelectedItemId() == R.id.navigation_slcm){

                                ((BottomNavigationView)getActivity().findViewById(R.id.nav_view)).setSelectedItemId(R.id.navigation_slcm);
                            }

                            view.findViewById(R.id.slcmLoginCard).setVisibility(View.VISIBLE);
                            view.findViewById(R.id.loadingScreenSlcm).setVisibility(View.GONE);

                        } else {
                            SharedPreferences sharedPref = mActivity.getSharedPreferences("thepostapp", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("loggedIn", false);
                            editor.apply();
                            view.findViewById(R.id.loadingScreenSlcm).setVisibility(View.GONE);

                            Toast.makeText(getContext(), "Ensure your credentials are right or try again later.", Toast.LENGTH_LONG).show();

                            view.findViewById(R.id.slcmLoginCard).setVisibility(View.VISIBLE);
                        }
                    } else {
                        SharedPreferences sharedPref = mActivity.getSharedPreferences("thepostapp", Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("loggedIn", false);
                        editor.apply();
                        view.findViewById(R.id.loadingScreenSlcm).setVisibility(View.GONE);

                        try {
                            Toast.makeText(getContext(), "The server seems to be down.", Toast.LENGTH_LONG).show();
                        }
                        catch(Exception ex){
                            //empty catch
                        }

                        view.findViewById(R.id.slcmLoginCard).setVisibility(View.VISIBLE);
                    }
                }
                catch(Exception e){

                    SharedPreferences sharedPref = mActivity.getSharedPreferences("thepostapp", Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("loggedIn", false);
                    editor.apply();
                    view.findViewById(R.id.loadingScreenSlcm).setVisibility(View.GONE);

                    try {
                        Toast.makeText(getContext(), "Some error seems to have occurred. Please try again.", Toast.LENGTH_LONG).show();
                    }
                    catch(Exception ex){
                        //empty catch
                    }

                    view.findViewById(R.id.slcmLoginCard).setVisibility(View.VISIBLE);
                }

            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<SlcmBasicModel> call, Throwable t) {

                assert mActivity!= null;

                SharedPreferences sharedPref = mActivity.getSharedPreferences("thepostapp",Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("regNo", regNo);
                editor.putString("pass", pass);
                editor.putBoolean("loggedIn", false);
                editor.apply();

                Log.e("onFailure", "Initial Fail");
                view.findViewById(R.id.loadingScreenSlcm).setVisibility(View.GONE);

                view.findViewById(R.id.slcmLoginCard).setVisibility(View.VISIBLE);

                try {
                    Toast.makeText(getContext(), "We seem to have hit a hiccup. Please try logging in again.", Toast.LENGTH_LONG).show();
                }
                catch(Exception ex){
                    //empty catch
                }
                Log.e("error", t.toString());
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity =(Activity) context;
    }
}
