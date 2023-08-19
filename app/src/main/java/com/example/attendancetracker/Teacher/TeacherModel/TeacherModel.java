package com.example.attendancetracker.Teacher.TeacherModel;

import java.util.ArrayList;

public class TeacherModel {
    String teachID, name, email, imageID;
    ArrayList<String> classes;

    public TeacherModel() {
    }

    public TeacherModel(String teachID, String name, String email, String imageID, ArrayList<String> classes) {
        this.teachID = teachID;
        this.name = name;
        this.email = email;
        this.imageID = imageID;
        this.classes = classes;
    }

    public String getTeachID() {
        return teachID;
    }

    public void setTeachID(String stuID) {
        this.teachID = stuID;
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

    public ArrayList<String> getClasses() {
        return classes;
    }

    public void setClasses(ArrayList<String> classes) {
        this.classes = classes;
    }
}
