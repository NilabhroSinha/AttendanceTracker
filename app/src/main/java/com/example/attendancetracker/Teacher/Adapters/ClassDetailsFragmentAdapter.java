package com.example.attendancetracker.Teacher.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.attendancetracker.SignUp.Fragment.StudentFragment;
import com.example.attendancetracker.SignUp.Fragment.TeacherFragment;
import com.example.attendancetracker.Teacher.ClassDetails.Fragments.AllStudentsFragment;
import com.example.attendancetracker.Teacher.ClassDetails.Fragments.TimelineFragment;

public class ClassDetailsFragmentAdapter extends FragmentPagerAdapter {
    int tabCount;
    Context context;
    String whichYear, classID, className, time;

    public ClassDetailsFragmentAdapter(Context context, @NonNull FragmentManager fm, int totalTabs, String whichYear, String classID, String time, String className) {
        super(fm);
        this.context = context;
        this.tabCount = totalTabs;
        this.whichYear = whichYear;
        this.classID = classID;
        this.time = time;
        this.className = className;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new TimelineFragment(whichYear, classID, className, time);
            case 1:
                return new AllStudentsFragment(whichYear, classID);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
