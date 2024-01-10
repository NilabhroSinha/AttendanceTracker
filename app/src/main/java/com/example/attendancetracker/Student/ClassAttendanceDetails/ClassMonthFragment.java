package com.example.attendancetracker.Student.ClassAttendanceDetails;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentAdapters.StudentHomeAdapter;
import com.example.attendancetracker.Student.StudentHomePage.StudentHome;
import com.example.attendancetracker.Student.StudentModel.StudentClassModel;
import com.example.attendancetracker.Student.TimeTable.CalendarMonthFragment;
import com.example.attendancetracker.Student.TimeTable.HolidayEvent;
import com.example.attendancetracker.Teacher.ApiCall.CustomPair;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ClassMonthFragment extends Fragment {
    Context context;
    private int currentYear;
    private int currentMonth;
    String classID, teacherID, whichYear;
    ArrayList<Date> presentDaysList = new ArrayList<>();
    ArrayList<Integer> classDays = new ArrayList<>();
    public ClassMonthFragment() {

    }

    public static ClassMonthFragment newInstance(int year, int month, String classID, String teacherID, String whichYear) {
        ClassMonthFragment fragment = new ClassMonthFragment();
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putString("classID", classID);
        args.putString("teacherID", teacherID);
        args.putString("whichYear", whichYear);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();

        if (getArguments() != null) {
            currentYear = getArguments().getInt("year");
            currentMonth = getArguments().getInt("month");
            classID = getArguments().getString("classID");
            teacherID = getArguments().getString("teacherID");
            whichYear = getArguments().getString("whichYear");
        } else {
            Calendar calendar = Calendar.getInstance();
            currentYear = calendar.get(Calendar.YEAR);
            currentMonth = calendar.get(Calendar.MONTH);
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar_month, container, false);

        GridLayout calendarGrid = rootView.findViewById(R.id.calendarGrid);

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
            TextView dayOfWeekTextView = new TextView(context);
            dayOfWeekTextView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            dayOfWeekTextView.setPadding(40, 50, 40, 50);
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

        FirebaseDatabase.getInstance().getReference().child("student").child(whichYear).child(FirebaseAuth.getInstance().getUid()).child("allClasses").child(classID).child("presentDays").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                presentDaysList.clear();

                Date classDay;

                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        classDay = dataSnapshot.getValue(Date.class);
                        presentDaysList.add(classDay);
                    }
                }

                for (int day = 1; day <= maxDaysInMonth; day++) {
                    TextView dateTextView = new TextView(context);
                    dateTextView.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    ));

                    dateTextView.setText(String.valueOf(day));
                    dateTextView.setPadding(40, 30, 40, 30);
                    dateTextView.setGravity(Gravity.CENTER);
                    dateTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // Adjust the text size as needed


                    int finalDay = day;

                    int finalDay1 = day;
                    FirebaseDatabase.getInstance().getReference().child("Teacher").child(teacherID).child(whichYear).child(classID).child("dateClass").addListenerForSingleValueEvent(new ValueEventListener() {
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

                            Calendar calendar = Calendar.getInstance();

                            calendar.set(Calendar.YEAR, currentYear);
                            calendar.set(Calendar.MONTH, currentMonth);
                            calendar.set(Calendar.DAY_OF_MONTH, finalDay);

                            Date date = calendar.getTime();
                            Date today = Calendar.getInstance().getTime();

                            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                            if(classDays.contains(dayOfWeek-2)){
                                if(date.before(startDate) || date.after(endDate)){
                                    dateTextView.setTextColor(context.getColor(R.color.that_grey));
                                }

                                else if (date.after(startDate) && date.before(today)) {
                                    dateTextView.setTextColor(context.getColor(R.color.that_red));
                                    dateTextView.setTypeface(null, Typeface.BOLD);
                                }

                                else if (date.after(today) && date.before(endDate)) {
                                    dateTextView.setTextColor(context.getColor(R.color.that_yellow));
                                    dateTextView.setTypeface(null, Typeface.BOLD);
                                }else{
                                    dateTextView.setTextColor(context.getColor(R.color.that_blue));
                                    dateTextView.setTypeface(null, Typeface.BOLD);
                                }
                            }else if(date.equals(today)){
                                dateTextView.setTextColor(context.getColor(R.color.that_blue));
                                dateTextView.setTypeface(null, Typeface.BOLD);
                            }
                            else{
                                dateTextView.setTextColor(context.getColor(R.color.that_grey));
                            }

                            for (Date prsntDate : presentDaysList) {
                                Calendar eventCalendar = Calendar.getInstance();
                                eventCalendar.setTime(prsntDate);

                                int eventYear = eventCalendar.get(Calendar.YEAR);
                                int eventMonth = eventCalendar.get(Calendar.MONTH);
                                int eventDay = eventCalendar.get(Calendar.DAY_OF_MONTH);

                                if (currentYear == eventYear && currentMonth == eventMonth && finalDay1 == eventDay) {
                                    dateTextView.setTextColor(context.getColor(R.color.that_green));
                                    dateTextView.setTypeface(null, Typeface.BOLD);
                                    break;
                                }
                            }



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




                    // Handle click events on date TextView
                    calendarGrid.addView(dateTextView);

                    // Move to the next day
                    calendar.add(Calendar.DAY_OF_MONTH, 1);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rootView;
    }

    public Calendar getCalendarForMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Set to the first day of the month
        return calendar;
    }
}
