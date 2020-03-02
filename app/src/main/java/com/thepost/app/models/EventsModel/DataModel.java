package com.thepost.app.models.EventsModel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class DataModel {

    @SerializedName("_id")
    @Expose
    private String _id;

    @SerializedName("clubName")
    @Expose
    private String clubName;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("imageURL")
    @Expose
    private String imageURL;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("formLink")
    @Expose
    private String formLink;

    public String get_id() {
        return _id;
    }

    public String getClubName() {
        return clubName;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFormLink() {
        return formLink;
    }

    public void setFormLink(String formLink) {
        this.formLink = formLink;
    }
}
