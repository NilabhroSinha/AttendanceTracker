package com.example.attendancetracker.Teacher.ApiCall;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CalendarService {
    @GET("events")

    Call<CalendarResponse> getHolidays(@Query("key") String apiKey);
}
