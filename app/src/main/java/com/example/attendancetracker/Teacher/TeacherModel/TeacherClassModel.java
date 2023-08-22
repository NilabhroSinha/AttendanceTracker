package com.example.attendancetracker.Teacher.TeacherModel;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Date;

public class TeacherClassModel {
    private String className, class_Timing, classID;
    private DateClass dateClass;
    ArrayList<AttendanceModel> timeTable;

    public TeacherClassModel() {
    }

    public TeacherClassModel(String className, String class_Timing, String classID, DateClass dateClass, ArrayList<AttendanceModel> timeTable) {
        this.className = className;
        this.class_Timing = class_Timing;
        this.classID = classID;
        this.dateClass = dateClass;
        this.timeTable = timeTable;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClass_Timing() {
        return class_Timing;
    }

    public void setClass_Timing(String class_Timing) {
        this.class_Timing = class_Timing;
    }

    public DateClass getDateClass() {
        return dateClass;
    }

    public void setDateClass(DateClass dateClass) {
        this.dateClass = dateClass;
    }

    public ArrayList<AttendanceModel> getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(ArrayList<AttendanceModel> timeTable) {
        this.timeTable = timeTable;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }
}
