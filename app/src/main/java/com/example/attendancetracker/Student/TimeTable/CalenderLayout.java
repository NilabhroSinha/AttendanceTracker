package com.example.attendancetracker.Student.TimeTable;



import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Teacher.ApiCall.CustomPair;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalenderLayout extends AppCompatActivity{

    private ViewPager2 viewPager;
    private EventAdapter eventAdapter;
    private RecyclerView recyclerView;
    private TabLayout tabLayout;
    ArrayList<CustomPair> hList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_layout);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.upcomingEventsList);

        hList = (ArrayList<CustomPair>) getIntent().getSerializableExtra("hList");


        CalendarPagerAdapter adapter = new CalendarPagerAdapter(this, hList);
        viewPager.setAdapter(adapter);

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
        viewPager.setCurrentItem(6, false);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Update the tab selection
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(getUpcomingEvents()); // Customize this method
        recyclerView.setAdapter(eventAdapter);
        recyclerView.scrollToPosition(adapter.getCurrEvent());
    }

    // Customize this method to return your list of upcoming events
    private List<HolidayEvent> getUpcomingEvents() {
        List<HolidayEvent> events = new ArrayList<>();

        for(CustomPair cm: hList){
            events.add(new HolidayEvent(cm.getStringValue(), cm.getDateValue()));
        }


        return events;
    }

    private Date getDateFromString(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            return sdf.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            return new Date();
        }
    }
}
