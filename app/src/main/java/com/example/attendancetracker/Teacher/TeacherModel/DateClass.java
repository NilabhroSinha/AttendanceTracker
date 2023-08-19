package com.example.attendancetracker.Teacher.TeacherModel;

import java.util.ArrayList;
import java.util.Date;

public class DateClass {
    Date startDate, endDate;
    ArrayList<Integer> classDays;
    ArrayList<String> presentStudents;

    public DateClass() {
    }

    public DateClass(Date startDate, Date endDate, ArrayList<Integer> classDays, ArrayList<String> presentStudents) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.classDays = classDays;
        this.presentStudents = presentStudents;
    }

    public ArrayList<Integer> getClassDays() {
        return classDays;
    }

    public void setClassDays(ArrayList<Integer> classDays) {
        this.classDays = classDays;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public ArrayList<String> getPresentStudents() {
        return presentStudents;
    }

    public void setPresentStudents(ArrayList<String> presentStudents) {
        this.presentStudents = presentStudents;
    }
}
