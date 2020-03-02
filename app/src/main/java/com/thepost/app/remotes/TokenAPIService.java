package com.thepost.app.remotes;

import androidx.annotation.Keep;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

@Keep
public interface TokenAPIService {

    @POST("/credential")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<Object> sendToken(@Body RequestBody body);
}
