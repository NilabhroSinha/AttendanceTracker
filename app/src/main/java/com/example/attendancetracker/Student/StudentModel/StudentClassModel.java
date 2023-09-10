package com.example.attendancetracker.Student.StudentModel;

import com.example.attendancetracker.Teacher.TeacherModel.DateClass;

import java.util.ArrayList;

public class StudentClassModel {
    String classID, classTime, className, teacherName, teacherImage;

    public StudentClassModel() {
    }

    public StudentClassModel(String classID, String classTime, String className, String teacherName, String teacherImage) {
        this.classID = classID;
        this.classTime = classTime;
        this.className = className;
        this.teacherName = teacherName;
        this.teacherImage = teacherImage;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getClassTime() {
        return classTime;
    }

    public void setClassTime(String classTime) {
        this.classTime = classTime;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherImage() {
        return teacherImage;
    }

    public void setTeacherImage(String teacherImage) {
        this.teacherImage = teacherImage;
    }
}
