package com.example.attendancetracker.Teacher.TeacherHomePage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.attendancetracker.R;
import com.example.attendancetracker.Teacher.ApiCall.CalendarApiClient;
import com.example.attendancetracker.Teacher.ApiCall.CustomPair;
import com.example.attendancetracker.Teacher.ClassesPage.AllAssignedClasses;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherHome extends AppCompatActivity {
    String teacherName;
    CardView first, second, third, fourth;
    CircleImageView personalDP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.my_purple));

        first = findViewById(R.id.first);
        second = findViewById(R.id.second);
        third = findViewById(R.id.third);
        fourth = findViewById(R.id.fourth);
        personalDP = findViewById(R.id.personalDP);

        FirebaseDatabase.getInstance().getReference().child("Teacher").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;

                String image = snapshot.child("imageID").getValue(String.class);
                teacherName = snapshot.child("name").getValue(String.class);

                Glide.with(getApplicationContext()).load(image).into(personalDP);

                first.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TeacherHome.this, AllAssignedClasses.class);
                        intent.putExtra("whichYear", "First");
                        intent.putExtra("teacherImage", image);
                        intent.putExtra("teacherName", teacherName);
                        TeacherHome.this.startActivity(intent);
                    }
                });

                second.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TeacherHome.this, AllAssignedClasses.class);
                        intent.putExtra("whichYear", "Second");
                        intent.putExtra("teacherImage", image);
                        intent.putExtra("teacherName", teacherName);
                        TeacherHome.this.startActivity(intent);
                    }
                });

                third.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TeacherHome.this, AllAssignedClasses.class);
                        intent.putExtra("whichYear", "Third");
                        intent.putExtra("teacherImage", image);
                        intent.putExtra("teacherName", teacherName);
                        TeacherHome.this.startActivity(intent);
                    }
                });

                fourth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TeacherHome.this, AllAssignedClasses.class);
                        intent.putExtra("whichYear", "Fourth");
                        intent.putExtra("teacherImage", image);
                        intent.putExtra("teacherName", teacherName);
                        TeacherHome.this.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
}