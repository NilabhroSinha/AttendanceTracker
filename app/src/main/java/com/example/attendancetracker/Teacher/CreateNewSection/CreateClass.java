package com.example.attendancetracker.Teacher.CreateNewSection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StdSignUp.StudentSignup_2;
import com.example.attendancetracker.Teacher.ClassesPage.AllAssignedClasses;
import com.example.attendancetracker.Teacher.TeacherHomePage.TeacherHome;
import com.example.attendancetracker.Teacher.TeacherModel.AttendanceModel;
import com.example.attendancetracker.Teacher.TeacherModel.DateClass;
import com.example.attendancetracker.Teacher.TeacherModel.TeacherClassModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateClass extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    EditText className;
    String whichYear, teacherName, teacherImage, class_Timing;
    Spinner dropDown;
    TextView duration, startDate, classTime;
    ImageView datePicker, timePicker, close;
    CheckBox mon, tue, wed, thu, fri;
    Button createButton;
    Date sDate, endDate;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        className = findViewById(R.id.className);
        duration = findViewById(R.id.duration);
        startDate = findViewById(R.id.startdate);
        datePicker = findViewById(R.id.datePicker);
        timePicker = findViewById(R.id.timePicker);
        dropDown = findViewById(R.id.dropdown_menu);
        classTime = findViewById(R.id.classTime);
        mon = findViewById(R.id.monday);
        tue = findViewById(R.id.tuesday);
        wed = findViewById(R.id.wednesday);
        thu = findViewById(R.id.thursday);
        fri = findViewById(R.id.friday);
        createButton = findViewById(R.id.create);
        close = findViewById(R.id.close);

        whichYear = getIntent().getStringExtra("whichYear");
        teacherName = getIntent().getStringExtra("teacherName");
        teacherImage = getIntent().getStringExtra("teacherImage");

        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.white));

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateClass.this, TeacherHome.class));
                finish();
            }
        });

        CreateClass.CustomAdapter<String> adapter = new CreateClass.CustomAdapter<String>(CreateClass.this,
                android.R.layout.simple_spinner_dropdown_item, new String[] {"1 month", "2 months", "3 months", "4 months", "5 months", "6 months"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        dropDown.setOnItemSelectedListener(this);

        dropDown.setAdapter(adapter);

        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on below line we are getting our hour, minute.
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // on below line we are initializing our Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateClass.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                // on below line we are setting selected time
                                // in our text view.

                                class_Timing = updateTime(hourOfDay, minute);
                                classTime.setText(class_Timing);
                            }
                        }, hour, minute, false);
                // at last we are calling show to
                // display our time picker dialog.
                timePickerDialog.show();

            }
        });

        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // on below line we are getting
                // our day, month and year.
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        CreateClass.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                String birthday = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                startDate.setText(birthday);

                                calendar.set(year, monthOfYear, dayOfMonth, 0, 0);

                                sDate = calendar.getTime();

                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String duration = dropDown.getSelectedItem().toString();
                int totalDuration = duration.charAt(0)-'0';

                calendar.add(Calendar.MONTH, totalDuration);
                endDate = calendar.getTime();                                //end Date

                ArrayList<Integer> weeklyClasses = getAllClassesInAWeek();

                LocalDate firstClass = null;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    firstClass = LocalDate.of(Integer.parseInt(new SimpleDateFormat("yyyy").format(sDate)), Integer.parseInt(new SimpleDateFormat("MM").format(sDate)), Integer.parseInt(new SimpleDateFormat("dd").format(sDate)));   //   YYYY/MM/DD
                }

                LocalDate lastClass = null;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    lastClass = LocalDate.of(Integer.parseInt(new SimpleDateFormat("yyyy").format(endDate)), Integer.parseInt(new SimpleDateFormat("MM").format(endDate)), Integer.parseInt(new SimpleDateFormat("dd").format(endDate)));   //   YYYY/MM/DD
                }

                DateClass dateClass = new DateClass(sDate, endDate, weeklyClasses);
                ArrayList<AttendanceModel> timetable = getClasses(weeklyClasses, firstClass, lastClass);

                String classID = FirebaseDatabase.getInstance().getReference().push().getKey();

                TeacherClassModel teacherClassModel = new TeacherClassModel(className.getText().toString(), classTime.getText().toString(), classID, dateClass, timetable);
                FirebaseDatabase.getInstance().getReference().child("Teacher").child(auth.getUid()).child(whichYear).child(classID).setValue(teacherClassModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(CreateClass.this, "Class created successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(CreateClass.this, AddStudentsToClass.class);
                            intent.putExtra("whichYear", whichYear);
                            intent.putExtra("classID", classID);
                            intent.putExtra("className", className.getText().toString());
                            intent.putExtra("teacherName", teacherName);
                            intent.putExtra("teacherImage", teacherImage);
                            intent.putExtra("classTime", classTime.getText().toString());
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });

    }

    public static ArrayList<AttendanceModel> getClasses(ArrayList<Integer> arr, LocalDate f, LocalDate l){

        ArrayList<AttendanceModel> dates = new ArrayList<>();
        DayOfWeek day=null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (LocalDate date = f; date.isBefore(l); date = date.plusDays(1))
            {
                day = date.getDayOfWeek();
                if(day.name().equals("MONDAY")){
                    if(arr.contains(0)){

                        Date date1 = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        dates.add(new AttendanceModel(date1));
                    }
                }
                else if(day.name().equals("TUESDAY")){

                    if(arr.contains(1)){
                        Date date1 = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        dates.add(new AttendanceModel(date1));
                    }
                }
                else if(day.name().equals("WEDNESDAY")){

                    if(arr.contains(2)){
                        Date date1 = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        dates.add(new AttendanceModel(date1));
                    }
                }
                else if(day.name().equals("THURSDAY")){

                    if(arr.contains(3)){
                        Date date1 = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        dates.add(new AttendanceModel(date1));
                    }
                }
                else if(day.name().equals("FRIDAY")){
                    if(arr.contains(4)){
                        Date date1 = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        dates.add(new AttendanceModel(date1));
                    }
                }

            }
        }
        return dates;
    }

    ArrayList<Integer> getAllClassesInAWeek(){
        ArrayList<Integer> list = new ArrayList<>();

        if(mon.isChecked()){
            list.add(0);  //monday
        }
        if(tue.isChecked()){
            list.add(1);
        }
        if(wed.isChecked()){
            list.add(2);
        }
        if(thu.isChecked()){
            list.add(3);
        }
        if(fri.isChecked()){
            list.add(4);
        }

        return list;
    }

    private String updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";

        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return aTime;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        duration.setText(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private static class CustomAdapter<T> extends ArrayAdapter<String> {
        public CustomAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText("");
            return view;
        }
    }
}