package com.example.attendancetracker.Teacher.Attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentModel.StudentModel;
import com.example.attendancetracker.Teacher.Adapters.TimelineFragmentAdapter;
import com.example.attendancetracker.Teacher.Adapters.UpdateAttendanceAdapter;
import com.example.attendancetracker.Teacher.TeacherModel.AttendanceModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class EditAttendance extends AppCompatActivity {
    String whichYear, classID;
    int pos;
    RecyclerView recyclerView;
    UpdateAttendanceAdapter updateAttendanceAdapter;
    FirebaseAuth auth;
    ArrayList<StudentModel> studentList = new ArrayList<>();
    Set<String> presentStudentList = new HashSet<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_attendance);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        auth = FirebaseAuth.getInstance();
        whichYear = getIntent().getStringExtra("whichYear");
        classID = getIntent().getStringExtra("classID");
        pos = getIntent().getIntExtra("pos", -1);
        recyclerView = findViewById(R.id.recycler);

        FirebaseDatabase.getInstance().getReference().child("Teacher").child(auth.getUid()).child(whichYear).child(classID).child("allStudents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;

                studentList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String studentID = dataSnapshot.getValue(String.class);

                    FirebaseDatabase.getInstance().getReference().child("student").child(whichYear).addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()) return;

                            StudentModel sm = snapshot.child(studentID).getValue(StudentModel.class);

                            studentList.add(sm);
                            updateAttendanceAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                updateAttendanceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Teacher").child(FirebaseAuth.getInstance().getUid()).child(whichYear).child(classID).child("timeTable").child(String.valueOf(pos)).child("presentStudents").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;

                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    presentStudentList.add(snapshot1.getValue(String.class));
                }
                updateAttendanceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateAttendanceAdapter = new UpdateAttendanceAdapter(this, studentList, presentStudentList, whichYear, classID, pos);

        recyclerView.setAdapter(updateAttendanceAdapter);
    }
}