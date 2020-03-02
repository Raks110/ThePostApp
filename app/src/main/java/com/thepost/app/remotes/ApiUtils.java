package com.thepost.app.remotes;

import androidx.annotation.Keep;

@Keep
public class ApiUtils {

    private ApiUtils() {}

    private static final String BASE_URL = "https://app.themitpost.com/";

    public static ArticleAPIService getArticleAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(ArticleAPIService.class);
    }

    public static NoticesAPIService getNoticesAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(NoticesAPIService.class);
    }

    public static MagazinesAPIService getMagazinesAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(MagazinesAPIService.class);
    }

    public static EventsAPIService getEventsAPIService(){

        return RetrofitClient.getClient(BASE_URL).create(EventsAPIService.class);
    }

    public static SlcmAPIService getSlcmAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(SlcmAPIService.class);
    }

    public static TokenAPIService getTokenAPIService(){

        return RetrofitClient.getClient(BASE_URL).create(TokenAPIService.class);
    }
}
