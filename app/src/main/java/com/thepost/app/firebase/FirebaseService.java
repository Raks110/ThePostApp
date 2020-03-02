package com.thepost.app.firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.thepost.app.remotes.ApiUtils;
import com.thepost.app.remotes.TokenAPIService;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.annotations.EverythingIsNonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Keep
public class FirebaseService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onNewToken(@NonNull String token) {
        Log.e(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {

        getSharedPreferences("thepostapp", Context.MODE_PRIVATE).edit().putString("fcm_token", token).apply();

        TokenAPIService mAPIService = ApiUtils.getTokenAPIService();

        String regNo = getSharedPreferences("thepostapp", Context.MODE_PRIVATE).getString("regNo", "");
        String pass = getSharedPreferences("thepostapp", Context.MODE_PRIVATE).getString("pass", "");

        if (regNo.trim().equals("") || pass.trim().equals("")) {
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("regNumber", regNo);
        params.put("pass", pass);
        params.put("fcm_token", token);
        params.put("action", "update");
        String strRequestBody = new Gson().toJson(params);

        final RequestBody requestBody = RequestBody.create(MediaType.
                parse("application/json"), strRequestBody);

        mAPIService.sendToken(requestBody).enqueue(new Callback<Object>() {
            @Override
            @EverythingIsNonNull
            public void onResponse(Call<Object> call, Response<Object> response) {

                if (response.isSuccessful()) {

                    Log.d(TAG, "Successfully updated token");
                }

                if (!response.isSuccessful()) {

                    Log.e(TAG, "Failed to update token.");
                }

            }

            @Override
            @EverythingIsNonNull
            public void onFailure(Call<Object> call, Throwable t) {

                Log.e(TAG, "Failed to update token.");
            }
        });
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        //Handle notifications that were received when the app is actively running in the foreground
    }
}