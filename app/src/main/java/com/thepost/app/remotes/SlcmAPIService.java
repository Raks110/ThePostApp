package com.thepost.app.remotes;

import androidx.annotation.Keep;

import com.thepost.app.models.SlcmModel.BasicModel.SlcmBasicModel;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

@Keep
public interface SlcmAPIService {

    @POST("/values")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<SlcmBasicModel> savePost(@Body RequestBody body);

}
