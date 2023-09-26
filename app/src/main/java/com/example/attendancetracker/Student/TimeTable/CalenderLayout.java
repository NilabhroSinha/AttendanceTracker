package com.example.attendancetracker.Student.TimeTable;



import android.os.Bundle;
import android.util.Pair;
import android.view.View;

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

public class CalenderLayout extends AppCompatActivity implements CalendarMonthFragment.DateSelectionListener {

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
        int initialPage = calculatePositionForMonth(currentYear, currentMonth);
        viewPager.setCurrentItem(0, true);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Update the tab selection
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        recyclerView = findViewById(R.id.upcomingEventsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(getUpcomingEvents()); // Customize this method
        recyclerView.setAdapter(eventAdapter);
    }

    private int calculatePositionForMonth(int year, int month) {
        // Calculate the position for the given year and month
        // This depends on your ViewPager2 setup and how months are organized
        // For example, if each page in ViewPager2 represents a month, you can calculate it like this:

        return  month + (year - /* Start year of ViewPager2 */ 2022) * 12;
    }

    // Implement the DateSelectionListener interface
    @Override
    public void onDateSelected(String selectedDate) {
        // Handle date selection, e.g., scroll to the event in the RecyclerView

        // Find the position of the selected date in the event list
        int position = findEventPosition(selectedDate);

        // Scroll to the selected event position in the RecyclerView
        if (position != -1) {
            recyclerView.scrollToPosition(position);
        }
    }

    // Helper method to find the position of the selected date in the event list
    private int findEventPosition(String selectedDate) {
        for (int i = 0; i < eventAdapter.getItemCount(); i++) {
            HolidayEvent event = eventAdapter.getItem(i);
            // You need to modify this condition to match the date format used in your HolidayEvent class
            if (event.getDateTime().equals(selectedDate)) {
                return i;
            }
        }
        return -1; // Return -1 if the date is not found in the event list
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
