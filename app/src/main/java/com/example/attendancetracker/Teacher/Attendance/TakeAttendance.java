package com.example.attendancetracker.Teacher.Attendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentModel.StudentModel;
import com.example.attendancetracker.Teacher.Adapters.TakeAttendanceAdapter;
import com.example.attendancetracker.Teacher.ExcelSheetCreator.CSVExporter;
import com.example.attendancetracker.Teacher.TeacherModel.AttendanceModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class TakeAttendance extends AppCompatActivity {
    Button qr;
    CircleImageView editAttendance;
    TextView present, strength;
    ImageView qrImage, download;
    String whichYear, classID;
    Date date;
    RecyclerView recycler;
    FirebaseAuth auth;
    TakeAttendanceAdapter takeAttendanceAdapter;
    ArrayList<AttendanceModel> attendanceList = new ArrayList<>();
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<StudentModel> presentS = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);

        whichYear = getIntent().getStringExtra("whichYear");
        classID = getIntent().getStringExtra("classID");
        date = (Date) this.getIntent().getExtras().get("date");

        qrImage = findViewById(R.id.qrImage);
        present = findViewById(R.id.present);
        strength = findViewById(R.id.strength);
        qr = findViewById(R.id.qr);
        editAttendance = findViewById(R.id.editAtt);
        recycler = findViewById(R.id.recycler);
        auth =FirebaseAuth.getInstance();
        download = findViewById(R.id.download);

        setAttendancePercentage();

        Date currentDate = new Date();

        if(currentDate.getDate() != date.getDate()){
            qr.setVisibility(View.GONE);
        }


        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(TakeAttendance.this);
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("Volume up to turn on flash");
                intentIntegrator.setCaptureActivity(captActivity.class);
                intentIntegrator.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
                intentIntegrator.initiateScan();
            }

        });

        FirebaseDatabase.getInstance().getReference().child("Teacher").child(auth.getUid()).child(whichYear).child(classID).child("timeTable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;
                attendanceList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    AttendanceModel am = dataSnapshot.getValue(AttendanceModel.class);
                    attendanceList.add(am);
                }

                int pos = getCurrentOrNextDate(attendanceList, date);

                FirebaseDatabase.getInstance().getReference().child("Teacher").child(auth.getUid()).child(whichYear).child(classID).child("timeTable").child(String.valueOf(pos)).child("presentStudents").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()) return;
                        arrayList.clear();
                        presentS.clear();
                        int presentStudents = 0;

                        for(DataSnapshot snapshot1: snapshot.getChildren()){
                            String str = snapshot1.getValue(String.class);
                            presentStudents++;
                            arrayList.add(str);
                        }

                        for(String students: arrayList){
                            FirebaseDatabase.getInstance().getReference().child("student").child(whichYear).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.exists()) return;

                                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                                        if(snapshot1.child("stuID").getValue(String.class).equals(students)){
                                            StudentModel sm = snapshot1.getValue(StudentModel.class);

                                            presentS.add(sm);
                                        }
                                    }

                                    download.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            CSVExporter.exportToCSV(TakeAttendance.this, presentS);
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }


                        if(presentStudents > 0){
                            qrImage.setVisibility(View.GONE);
                        }
                        present.setText(String.valueOf(presentStudents));

                        takeAttendanceAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                editAttendance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TakeAttendance.this, EditAttendance.class);
                        intent.putExtra("whichYear", whichYear);
                        intent.putExtra("classID", classID);
                        intent.putExtra("pos", pos);
//                intent.putExtra("teacherName", teacherName);
//                intent.putExtra("teacherImage", teacherImage);
//                intent.putExtra("classTime", classTime.getText().toString());
                        startActivity(intent);
                    }
                });


                takeAttendanceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        recycler.setLayoutManager(new LinearLayoutManager(this));

        takeAttendanceAdapter = new TakeAttendanceAdapter(getApplicationContext(), arrayList, whichYear);
        recycler.setAdapter(takeAttendanceAdapter);
    }

    private void setAttendancePercentage() {
        DatabaseReference classDatabase = FirebaseDatabase.getInstance().getReference().child("Teacher").child(FirebaseAuth.getInstance().getUid()).child(whichYear).child(classID);

        classDatabase.child("allStudents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;
                int totalStudents = 0;

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    totalStudents++;
                }

                strength.setText(String.valueOf(totalStudents));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(intentResult != null){
            String decodedStudentID = intentResult.getContents();

            if(decodedStudentID != null){

                Dialog dialog = new Dialog(this);

                FirebaseDatabase.getInstance().getReference().child("student").child(whichYear).addValueEventListener(new ValueEventListener() {
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

                        if(snapshot.child(decodedStudentID).exists()){

                            StudentModel studentModel = snapshot.child(decodedStudentID).getValue(StudentModel.class);

                            Glide.with(getApplicationContext()).load(studentModel.getImageID()).into(dp);
                            name.setText(studentModel.getName());
                            rollnumber.setText("Roll Number: "+ studentModel.getRollnumber());
                            dept.setText("Year: "+ studentModel.getWhichYear());


                            accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    FirebaseDatabase.getInstance().getReference().child("Teacher").child(FirebaseAuth.getInstance().getUid()).child(whichYear).child(classID).child("timeTable").addValueEventListener(new ValueEventListener() {
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
                                                        FirebaseDatabase.getInstance().getReference().child("Teacher").child(FirebaseAuth.getInstance().getUid()).child(whichYear).child(classID).child("timeTable").child(index+"").child("presentStudents").updateChildren(map);

                                                        FirebaseDatabase.getInstance().getReference().child("student").child(whichYear).child(decodedStudentID).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                HashMap<String, Object> map1 = new HashMap<>();

                                                                LocalDate localDate = LocalDate.now();
                                                                map1.put(String.valueOf(LocalDate.now().getDayOfYear()), Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

                                                                FirebaseDatabase.getInstance().getReference().child("student").child(whichYear).child(decodedStudentID).child("allClasses").child(classID).child("presentDays").updateChildren(map1);

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
                                    dialog.dismiss();
                                }
                            });

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                FirebaseDatabase.getInstance().getReference().child("student").child(whichYear).child(decodedStudentID).child("allClasses").child(classID).child("presentDays").child(String.valueOf(LocalDate.now().getDayOfYear())).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(!snapshot.exists()){
                                            dialog.show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        else{
                            accept.setVisibility(View.GONE);
                            name.setText("Not from this Department");
                            rollnumber.setText("");
                            dept.setText("");
                            dialog.show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                dialog.dismiss();
            }
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }

    public static int getCurrentOrNextDate(ArrayList<AttendanceModel> arr, Date date){

        LocalDate cd = null;   //   YYYY/MM/DD
        LocalDate ldate = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            cd = LocalDate.of(Integer.parseInt(new SimpleDateFormat("yyyy").format(date)), Integer.parseInt(new SimpleDateFormat("MM").format(date)), Integer.parseInt(new SimpleDateFormat("dd").format(date)));
        }

        for (int i=0 ; i< arr.size() ;i++){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ldate = LocalDate.of(Integer.parseInt(new SimpleDateFormat("yyyy").format(arr.get(i).getDate())), Integer.parseInt(new SimpleDateFormat("MM").format(arr.get(i).getDate())), Integer.parseInt(new SimpleDateFormat("dd").format(arr.get(i).getDate())));   //   YYYY/MM/DD

                if(cd.isEqual(ldate)){
                    return i;
                }
            }

        }
        return arr.size()-1;
    }

}
