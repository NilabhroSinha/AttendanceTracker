package com.example.attendancetracker.SignUp.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.attendancetracker.SignUp.Fragment.StudentFragment;
import com.example.attendancetracker.SignUp.Fragment.TeacherFragment;

public class PageAdapter extends FragmentPagerAdapter {

    int tabCount;
    Context context;

    public PageAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        this.context = context;
        this.tabCount = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                StudentFragment sf = new StudentFragment();
                return sf;
            case 1:
                TeacherFragment tf = new TeacherFragment();
                return tf;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
