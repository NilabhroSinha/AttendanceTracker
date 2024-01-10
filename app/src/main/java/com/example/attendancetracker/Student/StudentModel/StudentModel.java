package com.example.attendancetracker.Student.StudentModel;

import java.util.ArrayList;

public class StudentModel {
    String stuID, name, email, imageID, whichYear, rollnumber;
    ArrayList <String>classes;
    StudentClassModel studentClassModel;

    public StudentModel(String stuID, String name, String email, String imageID, String whichYear, String rollnumber, ArrayList<String> classes) {
        this.stuID = stuID;
        this.name = name;
        this.email = email;
        this.imageID = imageID;
        this.whichYear = whichYear;
        this.rollnumber = rollnumber;
        this.classes = classes;
    }

    public StudentModel() {
    }

    public ArrayList<String> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<String> classes) {
        this.classes = classes;
    }

    public String getStuID() {
        return stuID;
    }

    public void setStuID(String stuID) {
        this.stuID = stuID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getWhichYear() {
        return whichYear;
    }

    public void setWhichYear(String whichYear) {
        this.whichYear = whichYear;
    }

    public String getRollnumber() {
        return rollnumber;
    }

    public void setRollnumber(String rollnumber) {
        this.rollnumber = rollnumber;
    }
}
