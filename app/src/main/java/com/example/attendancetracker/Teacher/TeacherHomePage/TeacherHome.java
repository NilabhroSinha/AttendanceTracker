package com.example.attendancetracker.Teacher.TeacherHomePage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.attendancetracker.R;
import com.example.attendancetracker.Teacher.ClassesPage.AllAssignedClasses;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherHome extends AppCompatActivity {
    CardView cse, it, ece, ee, aeie;
    CircleImageView personalDP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.my_purple));

        cse = findViewById(R.id.CSE);
        it = findViewById(R.id.IT);
        ece = findViewById(R.id.ECE);
        ee = findViewById(R.id.EE);
        aeie = findViewById(R.id.AEIE);
        personalDP = findViewById(R.id.personalDP);

        FirebaseDatabase.getInstance().getReference().child("Teacher").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;

                String image = snapshot.child("imageID").getValue(String.class);

                Glide.with(getApplicationContext()).load(image).into(personalDP);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        cse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherHome.this, AllAssignedClasses.class);
                intent.putExtra("department", "CSE");
                TeacherHome.this.startActivity(intent);
            }
        });

        it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherHome.this, AllAssignedClasses.class);
                intent.putExtra("department", "IT");
                TeacherHome.this.startActivity(intent);
            }
        });

        ece.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherHome.this, AllAssignedClasses.class);
                intent.putExtra("department", "ECE");
                TeacherHome.this.startActivity(intent);
            }
        });

        ee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherHome.this, AllAssignedClasses.class);
                intent.putExtra("department", "EE");
                TeacherHome.this.startActivity(intent);
            }
        });

        aeie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TeacherHome.this, AllAssignedClasses.class);
                intent.putExtra("department", "AEIE");
                TeacherHome.this.startActivity(intent);
            }
        });



    }
}