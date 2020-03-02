package com.thepost.app.remotes;

import androidx.annotation.Keep;

import com.thepost.app.models.ArticleModel.ArticleModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

@Keep
public interface ArticleAPIService {

    @GET("/posts")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<List<ArticleModel>> getPosts();
}
