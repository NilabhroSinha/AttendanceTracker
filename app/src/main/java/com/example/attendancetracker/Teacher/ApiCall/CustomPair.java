package com.example.attendancetracker.Teacher.ApiCall;

import java.io.Serializable;
import java.util.Date;

public class CustomPair implements Serializable {
    private String stringValue;
    private Date dateValue;

    public CustomPair() {
    }

    public CustomPair(String stringValue, Date dateValue) {
        this.stringValue = stringValue;
        this.dateValue = dateValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }
}
