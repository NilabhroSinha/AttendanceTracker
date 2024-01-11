package com.example.attendancetracker.Teacher.ApiCall;

import android.util.Log;
import android.util.Pair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CalendarApiClient {
    private static final String API_KEY = "AIzaSyDzzuChicpSwZQIyodiv-XVSxkb5z2CWAk";
    private static final String BASE_URL = "https://www.googleapis.com/calendar/v3/calendars/en.indian%23holiday%40group.v.calendar.google.com/";

    static ArrayList<CustomPair> arrayList = new ArrayList<>();


    public static ArrayList<CustomPair> getList() {
        CalendarApiClient client = new CalendarApiClient();
        Call<CalendarResponse> call = client.getHolidays();

        call.enqueue(new Callback<CalendarResponse>() {
            @Override
            public void onResponse(Call<CalendarResponse> call, Response<CalendarResponse> response) {
                if (response.isSuccessful()) {
                    CalendarResponse calendarResponse = response.body();
                    if (calendarResponse != null) {
                        arrayList.clear();
                        List<Event> events = calendarResponse.getItems();
                        for (Event event : events) {
                            String summary = event.getSummary();
                            EventDate startDate = event.getStart();

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                            Date date = null;
                            try {
                                date = dateFormat.parse(startDate.getDate());
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            Date currentDate = new Date();

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(currentDate);

                            calendar.add(Calendar.MONTH, -6);

                            Date sixMonthsAgo = calendar.getTime();

                            calendar.add(Calendar.MONTH, 12);

                            Date sixMonthsAfter = calendar.getTime();

                            if(date.after(sixMonthsAgo) && date.before(sixMonthsAfter))
                                arrayList.add(new CustomPair(summary, date));

                        }
                    }
                } else {
                    Log.d("input", "Failed to fetch holidays: ");
                }
            }

            @Override
            public void onFailure(Call<CalendarResponse> call, Throwable t) {
                Log.d("input", t.getMessage());
            }
        });

        return arrayList;
    }

    private CalendarService calendarService;

    public CalendarApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        calendarService = retrofit.create(CalendarService.class);
    }

    public Call<CalendarResponse> getHolidays() {
        return calendarService.getHolidays(API_KEY);
    }
}