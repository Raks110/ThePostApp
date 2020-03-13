package com.thepost.app.models.MagazineModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MagazineModel {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("pdfLink")
    @Expose
    private String pdfLink;
    @SerializedName("imageLink")
    @Expose
    private String imageURL;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("time")
    @Expose
    private Object time;

    private int pageNum = 0;

    public String getImageURL() {
        return imageURL;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getId() {
        return id;
    }

    public String getPdfLink() {
        return pdfLink;
    }

    public String getDate() {
        return date;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPdfLink(String pdfLink) {
        this.pdfLink = pdfLink;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}
