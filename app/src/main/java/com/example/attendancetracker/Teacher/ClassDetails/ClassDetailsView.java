package com.example.attendancetracker.Teacher.ClassDetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.attendancetracker.R;
import com.example.attendancetracker.SignUp.Adapter.PageAdapter;
import com.example.attendancetracker.Teacher.Adapters.ClassDetailsFragmentAdapter;
import com.example.attendancetracker.Teacher.ClassDetails.Fragments.TimelineFragment;
import com.google.android.material.tabs.TabLayout;

public class ClassDetailsView extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    String department, classID, className, time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details_view);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager=(ViewPager)findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText("Timeline"));
        tabLayout.addTab(tabLayout.newTab().setText("Students"));

        department = getIntent().getStringExtra("department");
        classID = getIntent().getStringExtra("classID");
        time = getIntent().getStringExtra("time");
        className = getIntent().getStringExtra("className");

        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.my_purple));

        ViewPager viewPager = findViewById(R.id.viewPager);

        final ClassDetailsFragmentAdapter adapter = new ClassDetailsFragmentAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount(), department, classID, time, className);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}