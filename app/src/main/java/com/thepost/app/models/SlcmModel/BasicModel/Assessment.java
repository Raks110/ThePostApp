package com.thepost.app.models.SlcmModel.BasicModel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Assessment {

    @SerializedName("assessment_desc")
    @Expose
    private String assessmentDesc;
    @SerializedName("marks")
    @Expose
    private String marks;

    public String getAssessmentDesc() {
        return assessmentDesc;
    }

    public void setAssessmentDesc(String assessmentDesc) {
        this.assessmentDesc = assessmentDesc;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

}