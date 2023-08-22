package com.example.attendancetracker.Teacher.TeacherModel;

import java.time.LocalDate;
import java.util.Date;

public class AttendanceModel {
    Date date;

    public AttendanceModel() {
    }

    public AttendanceModel(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
