package com.example.attendancetracker.Student.StudentModel;

import com.example.attendancetracker.Teacher.TeacherModel.DateClass;

import java.util.ArrayList;

public class StudentClassModel {
    String className, teacherName, teacherImage;
    int totalDaysPresent, totalClassesTillNow;

    ArrayList<StudentAttendance> allAttendance;

    public StudentClassModel() {
    }

    public StudentClassModel(String className, String teacherName, String teacherImage, int totalDaysPresent, int totalClassesTillNow, ArrayList<StudentAttendance> allAttendance) {
        this.className = className;
        this.teacherName = teacherName;
        this.teacherImage = teacherImage;
        this.totalDaysPresent = totalDaysPresent;
        this.totalClassesTillNow = totalClassesTillNow;
        this.allAttendance = allAttendance;
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
