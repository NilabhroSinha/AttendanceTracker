package com.example.attendancetracker.Teacher.TeacherModel;

import java.util.ArrayList;
import java.util.Date;

public class DateClass {
    Date startDate, endDate;
    ArrayList<Integer> classDays;

    public DateClass() {
    }

    public DateClass(Date startDate, Date endDate, ArrayList<Integer> classDays) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.classDays = classDays;
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


}
