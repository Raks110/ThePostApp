package com.thepost.app.models.SlcmModel.BasicModel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Attendance {

    @SerializedName("subjectName")
    @Expose
    private String subjectName;
    @SerializedName("totalClasses")
    @Expose
    private String totalClasses;
    @SerializedName("classesAttended")
    @Expose
    private String classesAttended;
    @SerializedName("classesAbsent")
    @Expose
    private String classesAbsent;
    @SerializedName("updatedAt")
    @Expose
    private Long updatedAt;

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(String totalClasses) {
        this.totalClasses = totalClasses;
    }

    public String getClassesAttended() {
        return classesAttended;
    }

    public void setClassesAttended(String classesAttended) {
        this.classesAttended = classesAttended;
    }

    public String getClassesAbsent() {
        return classesAbsent;
    }

    public void setClassesAbsent(String classesAbsent) {
        this.classesAbsent = classesAbsent;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
