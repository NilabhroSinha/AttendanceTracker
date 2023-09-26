package com.example.attendancetracker.Student.TimeTable;

import java.util.Date;

public class HolidayEvent {
    private String title;
    private Date dateTime;

    public HolidayEvent() {
    }

    public HolidayEvent(String title, Date dateTime) {
        this.title = title;
        this.dateTime = dateTime;
    }

    public String getTitle() {
        return title;
    }

    public Date getDateTime() {
        return dateTime;
    }
}
