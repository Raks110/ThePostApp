package com.thepost.app.models.NoticeModel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class NoticeModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<NoticeDataModel> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<NoticeDataModel> getData() {
        return data;
    }

    public void setData(List<NoticeDataModel> data) {
        this.data = data;
    }
}
