package com.example.attendancetracker.Teacher.Attendance;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentModel.StudentModel;
import com.example.attendancetracker.Teacher.ClassesPage.AllAssignedClasses;
import com.example.attendancetracker.Teacher.TeacherModel.AttendanceModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;

public class TakeAttendance extends AppCompatActivity {
    Button qr;
    String department, classID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        department = getIntent().getStringExtra("department");
        classID = getIntent().getStringExtra("classID");

        qr = findViewById(R.id.qr);

        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScanOptions scanOptions = new ScanOptions();
                scanOptions.setOrientationLocked(true);
                scanOptions.setBeepEnabled(true);
                scanOptions.setPrompt("Volume up to turn on flash");
                scanOptions.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                scanOptions.setCaptureActivity(captActivity.class);
                barLauncher.launch(scanOptions);
            }
        });
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null){
            Dialog dialog = new Dialog(this);

            String decodedStudentID = String.valueOf(result.getContents());

            FirebaseDatabase.getInstance().getReference().child("student").child(department).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(!snapshot.exists()) return;

                    dialog.setContentView(R.layout.diaglue_approve_decline_student);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView dp = dialog.findViewById(R.id.dp);
                    TextView name = dialog.findViewById(R.id.name);
                    TextView rollnumber = dialog.findViewById(R.id.rollnumber);
                    TextView dept = dialog.findViewById(R.id.department);

                    Button accept = dialog.findViewById(R.id.accept);
                    Button decline = dialog.findViewById(R.id.decline);

                    decline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        StudentModel studentModel = dataSnapshot.getValue(StudentModel.class);
                        if(decodedStudentID.equals(studentModel.getStuID())){

                            Glide.with(TakeAttendance.this).load(studentModel.getImageID()).into(dp);
                            name.setText(studentModel.getName());
                            rollnumber.setText("Roll Number: "+ studentModel.getRollnumber());
                            dept.setText("Department: "+ studentModel.getDepartment());


                            accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    FirebaseDatabase.getInstance().getReference().child("Teacher").child(FirebaseAuth.getInstance().getUid()).child(department).child(classID).child("timeTable").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            int index = 0;
                                            for(DataSnapshot snapshot1: snapshot.getChildren()){
                                                AttendanceModel attendanceModel = snapshot1.getValue(AttendanceModel.class);

                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                    LocalDate ldate = LocalDate.of(Integer.parseInt(new SimpleDateFormat("yyyy").format(attendanceModel.getDate())), Integer.parseInt(new SimpleDateFormat("MM").format(attendanceModel.getDate())), Integer.parseInt(new SimpleDateFormat("dd").format(attendanceModel.getDate())));   //   YYYY/MM/DD

                                                    if(LocalDate.now().isEqual(ldate)){

                                                        HashMap<String, Object> map = new HashMap<>();
                                                        map.put(decodedStudentID, decodedStudentID);
                                                        FirebaseDatabase.getInstance().getReference().child("Teacher").child(FirebaseAuth.getInstance().getUid()).child(department).child(classID).child("timeTable").child(index+"").child("presentStudents").updateChildren(map);

                                                        FirebaseDatabase.getInstance().getReference().child("student").child(department).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                for(DataSnapshot dataSnapshot1: snapshot.getChildren()){
                                                                    StudentModel sm = dataSnapshot1.getValue(StudentModel.class);

                                                                    if(decodedStudentID.equals(sm.getStuID())){

                                                                        HashMap<String, Object> map1 = new HashMap<>();

                                                                        LocalDate localDate = LocalDate.now();
                                                                        map1.put(String.valueOf(LocalDate.now().getDayOfYear()), Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

                                                                        FirebaseDatabase.getInstance().getReference().child("student").child(department).child(decodedStudentID).child("allClasses").child(classID).child("presentDays").updateChildren(map1);
                                                                        break;
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });
                                                        break;
                                                    }
                                                }
                                                index++;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            });

                        }
                        dialog.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    });
}
