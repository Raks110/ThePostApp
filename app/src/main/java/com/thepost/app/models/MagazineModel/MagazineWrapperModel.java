package com.thepost.app.models.MagazineModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.thepost.app.models.NoticeModel.NoticeDataModel;

import java.util.List;

public class MagazineWrapperModel {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<MagazineModel> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<MagazineModel> getData() {
        return data;
    }

    public void setData(List<MagazineModel> data) {
        this.data = data;
    }
}
