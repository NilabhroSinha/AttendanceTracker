package com.example.attendancetracker.Student.StudentHomePage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentAdapters.StudentHomeAdapter;
import com.example.attendancetracker.Student.StudentModel.StudentClassModel;
import com.example.attendancetracker.Student.StudentModel.StudentModel;
import com.example.attendancetracker.Teacher.ClassesPage.AllAssignedClasses;
import com.example.attendancetracker.Teacher.TeacherAdapters.AllAssignedClassesAdapter2;
import com.example.attendancetracker.Teacher.TeacherModel.TeacherClassModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentHome extends AppCompatActivity {
    String studentImage;
    CircleImageView profilePicture;
    ImageView qrlogo;
    RecyclerView studentHomeRecylclerView;
    StudentHomeAdapter recyclerViewAdapter;
    ArrayList<StudentClassModel> classesArrayList = new ArrayList<>();
    FloatingActionMenu addButton;
    FloatingActionButton createClass;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.my_purple));

        profilePicture = findViewById(R.id.personalDP);

//        addButton = findViewById(R.id.addUser);
//        addButton.setMenuButtonColorPressed(R.color.my_dark_purple);
//        addButton.setMenuButtonColorNormalResId(R.color.my_dark_purple);
//
//        createClass = findViewById(R.id.createClass);

        studentHomeRecylclerView = findViewById(R.id.recycler);
        qrlogo = findViewById(R.id.qrlogo);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        qrlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(StudentHome.this);
                dialog.setContentView(R.layout.dialogue_qrcode_popout_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                ImageView qr = dialog.findViewById(R.id.qrcode);

                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(FirebaseAuth.getInstance().getUid(), BarcodeFormat.QR_CODE, 250, 250);

                    Bitmap bitmap = new BarcodeEncoder().createBitmap(bitMatrix);

                    qr.setImageBitmap(bitmap);

                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }

                dialog.show();
            }
        });

        getClassData();

    }

    void getClassData(){

        String allStreams[] = {"CSE", "IT", "ECE", "EE", "AEIE"};

        for(String dept: allStreams){
            getRef(dept);
        }

    }

    void getRef(String dept){

        FirebaseDatabase.getInstance().getReference().child("student").child(dept).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        String uid = dataSnapshot.getValue(StudentModel.class).getStuID();

                        if(uid.equals(auth.getUid())){
                            FirebaseDatabase.getInstance().getReference().child("student").child(dept).child(auth.getUid()).child("imageID").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        studentImage = snapshot.getValue(String.class);
                                        Glide.with(StudentHome.this).load(studentImage).into(profilePicture);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            FirebaseDatabase.getInstance().getReference().child("student").child(dept).child(auth.getUid()).child("allClasses").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    classesArrayList.clear();
                                    StudentClassModel classModel;

                                    if (snapshot.exists()) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            classModel = dataSnapshot.getValue(StudentClassModel.class);
                                            classesArrayList.add(classModel);
                                        }

                                        studentHomeRecylclerView.setLayoutManager(new LinearLayoutManager(StudentHome.this));

                                        recyclerViewAdapter = new StudentHomeAdapter(StudentHome.this, classesArrayList);
                                        studentHomeRecylclerView.setAdapter(recyclerViewAdapter);


                                    } else {
                                        Toast.makeText(StudentHome.this, "No Data Found", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}