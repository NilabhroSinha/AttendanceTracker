package com.example.attendancetracker.Student.TimeTable;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Teacher.ApiCall.CalendarApiClient;
import com.example.attendancetracker.Teacher.ApiCall.CustomPair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class CalendarMonthFragment extends Fragment {

    private DateSelectionListener dateSelectionListener;
    private int currentYear;
    private int currentMonth;
    private RecyclerView recyclerView;
    private List<HolidayEvent> holidayEvents;

    ArrayList<CustomPair> list;

    public CalendarMonthFragment(ArrayList<CustomPair> hlist) {
        list = hlist;
    }

    public static CalendarMonthFragment newInstance(int year, int month, ArrayList<CustomPair> Hlist) {
        CalendarMonthFragment fragment = new CalendarMonthFragment(Hlist);
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            currentYear = getArguments().getInt("year");
            currentMonth = getArguments().getInt("month");
        } else {
            // Default to current month and year
            Calendar calendar = Calendar.getInstance();
            currentYear = calendar.get(Calendar.YEAR);
            currentMonth = calendar.get(Calendar.MONTH);
        }

        // Initialize the list of holiday events (customize as needed)

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar_month, container, false);

        GridLayout calendarGrid = rootView.findViewById(R.id.calendarGrid);
        recyclerView = requireActivity().findViewById(R.id.upcomingEventsList);

        try {
            holidayEvents = getHolidayEvents();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Set the calendar title
        SimpleDateFormat titleFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);
        TextView calendarTitle = rootView.findViewById(R.id.calendarTitle);
        calendarTitle.setText(titleFormat.format(getCalendarForMonth(currentYear, currentMonth).getTime()));

        // Set the calendar grid
        calendarGrid.removeAllViews(); // Clear existing views

        Calendar calendar = getCalendarForMonth(currentYear, currentMonth);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int maxDaysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Adjust for the starting day of the week (e.g., Sunday or Monday)
        calendarGrid.setColumnCount(7);
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            TextView dayOfWeekTextView = new TextView(getContext());
            dayOfWeekTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            dayOfWeekTextView.setPadding(50, 50, 50, 50);
            dayOfWeekTextView.setText(daysOfWeek[i]);
            dayOfWeekTextView.setGravity(Gravity.CENTER); // Customize the color
            dayOfWeekTextView.setTypeface(null, Typeface.BOLD); // Make it bold if desired
            calendarGrid.addView(dayOfWeekTextView);
        }

        for (int i = 1; i < firstDayOfWeek; i++) {
            TextView emptyTextView = new TextView(requireContext());
            emptyTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    5, 5
            ));
            calendarGrid.addView(emptyTextView);
        }

        for (int day = 1; day <= maxDaysInMonth; day++) {
            TextView dateTextView = new TextView(requireContext());
            dateTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            ));

            dateTextView.setText(String.valueOf(day));
            dateTextView.setPadding(50, 50, 50, 50);
            dateTextView.setGravity(Gravity.CENTER);
            dateTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // Adjust the text size as needed


            // Highlight event dates in the calendar
            for (HolidayEvent event : holidayEvents) {
                Calendar eventCalendar = Calendar.getInstance();
                eventCalendar.setTime(event.getDateTime());
                int eventYear = eventCalendar.get(Calendar.YEAR);
                int eventMonth = eventCalendar.get(Calendar.MONTH);
                int eventDay = eventCalendar.get(Calendar.DAY_OF_MONTH);

                if (currentYear == eventYear && currentMonth == eventMonth && day == eventDay) {
//                    dateTextView.setBackgroundResource(R.drawable.highlighted_background);
                    dateTextView.setTextColor(getResources().getColor(R.color.red));
                    dateTextView.setTypeface(null, Typeface.BOLD);
                    break;
                }
            }

            // Handle click events on date TextViews
            dateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dateSelectionListener != null) {
                        String dateText = dateTextView.getText().toString();
                        dateSelectionListener.onDateSelected(dateText);
                    }
                }
            });

            calendarGrid.addView(dateTextView);

            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return rootView;
    }

    public void setDateSelectionListener(DateSelectionListener listener) {
        dateSelectionListener = listener;
    }

    public Calendar getCalendarForMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the month
        return calendar;
    }

    public interface DateSelectionListener {
        void onDateSelected(String selectedDate);
    }

    public String getCurrentMonthTitle() {
        return new SimpleDateFormat("MMMM yyyy", Locale.US)
                .format(getCalendarForMonth(currentYear, currentMonth).getTime());
    }

    // Customize this method to return your list of holiday events
    private List<HolidayEvent> getHolidayEvents() throws InterruptedException {

        ArrayList<HolidayEvent> events = new ArrayList<>();

        for(CustomPair ev: list){
            events.add(new HolidayEvent(ev.getStringValue(), ev.getDateValue()));
        }

        return events;
    }

}
