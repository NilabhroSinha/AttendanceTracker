package com.example.attendancetracker.Student.TimeTable;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.attendancetracker.Teacher.ApiCall.CustomPair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class CalendarPagerAdapter extends FragmentStateAdapter {
    private final int NUM_MONTHS = 13; // Display 25 months (12 before, 12 after the current month)
    private final List<CalendarMonthFragment> fragments = new ArrayList<>();
    private final List<String> monthTitles = new ArrayList<>();
    ArrayList<CustomPair> hlist;

    public CalendarPagerAdapter(FragmentActivity fragmentActivity, ArrayList<CustomPair> hlist) {
        super(fragmentActivity);
        this.hlist = hlist;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

        int year = currentYear;
        int month = currentMonth + position - NUM_MONTHS / 2;

        if (month < 0) {
            year -= 1;
            month += 12;
        } else if (month > 11) {
            year += 1;
            month -= 12;
        }

        CalendarMonthFragment fragment = CalendarMonthFragment.newInstance(year, month, hlist);
        fragments.add(fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return NUM_MONTHS;
    }

    public CalendarMonthFragment getFragment(int position) {
        return fragments.get(position);
    }

    public void addMonth(int year, int month) {
        String title = new SimpleDateFormat("MMMM yyyy", Locale.US).format(getCalendarForMonth(year, month).getTime());
        monthTitles.add(title);
    }

    public String getMonthTitle(int position) {
        return monthTitles.get(position);
    }

    public int getCurrentYear() {
        Calendar currentCalendar = Calendar.getInstance();
        return currentCalendar.get(Calendar.YEAR);
    }

    public int getCurrentMonth() {
        Calendar currentCalendar = Calendar.getInstance();
        return currentCalendar.get(Calendar.MONTH);
    }

    public int getNumMonths() {
        return NUM_MONTHS;
    }

    public Calendar getCalendarForMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the month
        return calendar;
    }
}



