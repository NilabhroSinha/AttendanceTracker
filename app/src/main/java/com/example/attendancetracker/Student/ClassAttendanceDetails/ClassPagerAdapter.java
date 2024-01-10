package com.example.attendancetracker.Student.ClassAttendanceDetails;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.attendancetracker.Student.TimeTable.CalendarMonthFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ClassPagerAdapter extends FragmentStateAdapter {
    String classID, teacherID, whichYear;
    private final int NUM_MONTHS = 13;
    private final List<ClassMonthFragment> fragments = new ArrayList<>();
    private final List<String> monthTitles = new ArrayList<>();

    public ClassPagerAdapter(@NonNull FragmentActivity fragmentActivity, String classID, String teacherID, String whichYear) {
        super(fragmentActivity);

        this.classID = classID;
        this.teacherID = teacherID;
        this.whichYear = whichYear;
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

        ClassMonthFragment fragment = ClassMonthFragment.newInstance(year, month, classID, teacherID, whichYear);

        fragments.add(fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return NUM_MONTHS;
    }

    public void addMonth(int year, int month) {
        String title = new SimpleDateFormat("MMMM yyyy", Locale.US).format(getCalendarForMonth(year, month).getTime());
        monthTitles.add(title);
    }

    public String getMonthTitle(int position) {
        return monthTitles.get(position);
    }

    public Calendar getCalendarForMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the month
        return calendar;
    }
}
