package com.thepost.app.models.SlcmModel.BasicModel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class SlcmBasicModel {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("updatedAt")
    @Expose
    private Long updatedAt;
    @SerializedName("cgpa")
    @Expose
    private String cgpa;
    @SerializedName("academicDetails")
    @Expose
    private List<AcademicDetail> academicDetails = null;
    @SerializedName("teacherGuardianStatus")
    @Expose
    private String teacherGuardianStatus;
    @SerializedName("semester")
    @Expose
    private String semester;
    @SerializedName("section")
    @Expose
    private String section;
    @SerializedName("rollno")
    @Expose
    private String rollno;
    @SerializedName("admittedYear")
    @Expose
    private String admittedYear;
    @SerializedName("teacherGuardianDetails")
    @Expose
    private TeacherGuardianDetails teacherGuardianDetails;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCgpa() {
        return cgpa;
    }

    public void setCgpa(String cgpa) {
        this.cgpa = cgpa;
    }

    public List<AcademicDetail> getAcademicDetails() {
        return academicDetails;
    }

    public void setAcademicDetails(List<AcademicDetail> academicDetails) {
        this.academicDetails = academicDetails;
    }

    public String getTeacherGuardianStatus() {
        return teacherGuardianStatus;
    }

    public void setTeacherGuardianStatus(String teacherGuardianStatus) {
        this.teacherGuardianStatus = teacherGuardianStatus;
    }

    public String getAdmittedYear() {
        return admittedYear;
    }

    public void setAdmittedYear(String admittedYear) {
        this.admittedYear = admittedYear;
    }

    public String getRollno() {
        return rollno;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public TeacherGuardianDetails getTeacherGuardianDetails() {
        return teacherGuardianDetails;
    }

    public void setTeacherGuardianDetails(TeacherGuardianDetails teacherGuardianDetails) {
        this.teacherGuardianDetails = teacherGuardianDetails;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

}
