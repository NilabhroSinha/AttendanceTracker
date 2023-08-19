package com.example.attendancetracker.Teacher.TeacherModel;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Date;

public class TeacherClassModel {
    private String className, class_Timing;
    private DateClass dateClass;

    public TeacherClassModel() {
    }

    public TeacherClassModel(String className, String class_Timing, DateClass dateClass) {
        this.className = className;
        this.class_Timing = class_Timing;
        this.dateClass = dateClass;
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
}
