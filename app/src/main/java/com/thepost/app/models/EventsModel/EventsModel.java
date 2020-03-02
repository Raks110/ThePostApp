package com.thepost.app.models.EventsModel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class EventsModel {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("data")
    @Expose
    private List<DataModel> data;

    public List<DataModel> getData() {
        return data;
    }

    public String getStatus() {
        return status;
    }

    public void setData(List<DataModel> data) {
        this.data = data;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
