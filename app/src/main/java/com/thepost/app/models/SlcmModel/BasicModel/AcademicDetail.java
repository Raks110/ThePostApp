package com.thepost.app.models.SlcmModel.BasicModel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class AcademicDetail {

    @SerializedName("semester")
    @Expose
    private String semester;
    @SerializedName("subjects")
    @Expose
    private List<String> subjects = null;
    @SerializedName("marksStatus")
    @Expose
    private Boolean marksStatus;
    @SerializedName("internalMarks")
    @Expose
    private List<InternalMarks> internalMarks = null;
    @SerializedName("attendanceStatus")
    @Expose
    private Boolean attendanceStatus;
    @SerializedName("attendance")
    @Expose
    private List<Attendance> attendance = null;

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public Boolean getMarksStatus() {
        return marksStatus;
    }

    public void setMarksStatus(Boolean marksStatus) {
        this.marksStatus = marksStatus;
    }

    public List<InternalMarks> getInternalMarks() {
        return internalMarks;
    }

    public void setInternalMarks(List<InternalMarks> internalMarks) {
        this.internalMarks = internalMarks;
    }

    public Boolean getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(Boolean attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public List<Attendance> getAttendance() {
        return attendance;
    }

    public void setAttendance(List<Attendance> attendance) {
        this.attendance = attendance;
    }

}
