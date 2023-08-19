package com.example.attendancetracker.Student.StudentModel;

import java.util.Date;

public class StudentAttendance {
    Date date;
    boolean attendance;

    public StudentAttendance(Date date, boolean attendance) {
        this.date = date;
        this.attendance = attendance;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isAttendance() {
        return attendance;
    }

    public void setAttendance(boolean attendance) {
        this.attendance = attendance;
    }
}
