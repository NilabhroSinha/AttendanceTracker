package com.example.attendancetracker.Teacher.ApiCall;

public class Event {
    private String summary;
    private EventDate start;
    private EventDate end;

    public String getSummary() {
        return summary;
    }

    public EventDate getStart() {
        return start;
    }

    public EventDate getEnd() {
        return end;
    }

}