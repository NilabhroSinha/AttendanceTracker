package com.example.attendancetracker.Student.ClassAttendanceDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Application;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.TimeTable.CalendarPagerAdapter;
import com.example.attendancetracker.Student.TimeTable.EventAdapter;
import com.example.attendancetracker.Teacher.ApiCall.CustomPair;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ClassDetails extends AppCompatActivity {
    int classesTaken = 0, classesRemaining = 0, presentDays = 0;
    String classID, teacherID, department;
    TextView yourAttendance, totalClassesTaken, TotalClassesRemaining, TotalClassesAttended;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    ArrayList<Integer> classDays = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        yourAttendance = findViewById(R.id.attendancePercentage);
        TotalClassesRemaining = findViewById(R.id.RemainingClasses);
        totalClassesTaken = findViewById(R.id.TotalClassesTaken);
        TotalClassesAttended = findViewById(R.id.TotalClassesAttended);

        classID = getIntent().getStringExtra("classID");
        teacherID = getIntent().getStringExtra("teacherID");
        department = getIntent().getStringExtra("department");

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        ClassPagerAdapter adapter = new ClassPagerAdapter(this, classID, teacherID, department);
        viewPager.setAdapter(adapter);

        FirebaseDatabase.getInstance().getReference().child("Teacher").child(teacherID).child(department).child(classID).child("dateClass").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;
                classDays.clear();

                for (DataSnapshot snapshot1 : snapshot.child("classDays").getChildren()) {
                    Integer item = snapshot1.getValue(Integer.class);
                    classDays.add(item);
                }

                Date startDate = null, endDate = null;

                Long startDateLong = snapshot.child("startDate").child("time").getValue(Long.class);
                Long endDateLong = snapshot.child("endDate").child("time").getValue(Long.class);

                startDate = new Date(startDateLong);
                endDate = new Date(endDateLong);

                Date today = Calendar.getInstance().getTime();

                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTime(startDate);

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(endDate);

                while (startCalendar.before(endCalendar)) {
                    Date currentDate = startCalendar.getTime();

                    int dayOfWeek = startCalendar.get(Calendar.DAY_OF_WEEK);

                    if(classDays.contains(dayOfWeek-2)){
                        if(currentDate.before(today)){
                            classesTaken++;
                        } else if (currentDate.after(today)) {
                            classesRemaining++;
                        }else{
                            classesTaken++;
                        }
                    }

                    startCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                totalClassesTaken.setText(String.valueOf(classesTaken));
                TotalClassesRemaining.setText(String.valueOf(classesRemaining));

                FirebaseDatabase.getInstance().getReference().child("student").child(department).child(FirebaseAuth.getInstance().getUid()).child("allClasses").child(classID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("presentDays").exists()){
                            for (DataSnapshot snapshot1 : snapshot.child("presentDays").getChildren()) {
                                presentDays++;
                            }
                        }

                        TotalClassesAttended.setText(String.valueOf(presentDays));
                        int attendance = classesTaken > 0 ? (presentDays/classesTaken)*100 : 0;
                        yourAttendance.setText(attendance+"%");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        tabLayout.setVisibility(View.GONE);

        // Create CalendarMonthFragment instances for different months and tabs
        for (int i = -6; i <= 6; i++) { // Display 25 months (12 before, 12 after the current month)
            int year = Calendar.getInstance().get(Calendar.YEAR); // Customize the year as needed
            int month = Calendar.getInstance().get(Calendar.MONTH) + i; // Customize the starting month as needed
            adapter.addMonth(year, month);
        }

        // Attach the TabLayout to the ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(adapter.getMonthTitle(position))
        ).attach();

        Calendar currentDate = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH);
        viewPager.setCurrentItem(6, true);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Update the tab selection
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }
}