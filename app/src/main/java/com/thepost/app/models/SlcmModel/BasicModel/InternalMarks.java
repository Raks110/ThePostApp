package com.thepost.app.models.SlcmModel.BasicModel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class InternalMarks {

    @SerializedName("subject_name")
    @Expose
    private String subjectName;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("is_lab")
    @Expose
    private Boolean isLab;
    @SerializedName("sessional")
    @Expose
    private Sessional sessional;
    @SerializedName("assignment")
    @Expose
    private Assignment assignment;
    @SerializedName("lab")
    @Expose
    private Lab lab;
    @SerializedName("updatedAt")
    @Expose
    private Long updatedAt;

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getIsLab() {
        return isLab;
    }

    public void setIsLab(Boolean isLab) {
        this.isLab = isLab;
    }

    public Sessional getSessional() {
        return sessional;
    }

    public void setSessional(Sessional sessional) {
        this.sessional = sessional;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Lab getLab() {
        return lab;
    }

    public void setLab(Lab lab) {
        this.lab = lab;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }
}