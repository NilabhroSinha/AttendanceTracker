package com.example.attendancetracker.Teacher.ClassesPage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentEditProfile.StudentEditProfile;
import com.example.attendancetracker.Student.StudentHomePage.StudentHome;
import com.example.attendancetracker.Teacher.Adapters.AllAssignedClassesAdapter;
import com.example.attendancetracker.Teacher.CreateNewSection.CreateClass;
import com.example.attendancetracker.Teacher.TeacherAdapters.AllAssignedClassesAdapter2;
import com.example.attendancetracker.Teacher.TeacherEditProfile.TeacherEditProfile;
import com.example.attendancetracker.Teacher.TeacherModel.TeacherClassModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllAssignedClasses extends AppCompatActivity {
    String whichYear, teacherName, teacherImage;
    CircleImageView profilePicture;
    RecyclerView AllAssignedClassesRecyclerView;
    AllAssignedClassesAdapter2 recyclerViewAdapter;
    ArrayList<TeacherClassModel> classesArrayList = new ArrayList<>();
    FloatingActionMenu addButton;
    FloatingActionButton createClass;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_assigned_classes);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.my_purple));

        profilePicture = findViewById(R.id.personalDP);

        addButton = findViewById(R.id.addUser);
        addButton.setMenuButtonColorPressed(R.color.my_dark_purple);
        addButton.setMenuButtonColorNormalResId(R.color.my_dark_purple);

        createClass = findViewById(R.id.createClass);

        whichYear = getIntent().getStringExtra("whichYear");
        teacherImage = getIntent().getStringExtra("teacherImage");
        teacherName = getIntent().getStringExtra("teacherName");

        AllAssignedClassesRecyclerView = findViewById(R.id.recycler);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        Glide.with(AllAssignedClasses.this).load(teacherImage).into(profilePicture);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllAssignedClasses.this, TeacherEditProfile.class);
                intent.putExtra("imageID", teacherImage);
                startActivity(intent);
            }
        });

        createClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addButton.toggle(true);

                Intent intent = new Intent(AllAssignedClasses.this, CreateClass.class);
                intent.putExtra("whichYear", getIntent().getStringExtra("whichYear"));
                intent.putExtra("teacherName", teacherName);
                intent.putExtra("teacherImage", teacherImage);
                AllAssignedClasses.this.startActivity(intent);
            }
        });



        database.getReference().child("Teacher").child(auth.getUid()).child(whichYear).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                classesArrayList.clear();
                TeacherClassModel classModel;
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        classModel = dataSnapshot.getValue(TeacherClassModel.class);
                        classesArrayList.add(classModel);
                    }

                    AllAssignedClassesRecyclerView.setLayoutManager(new LinearLayoutManager(AllAssignedClasses.this));

                    recyclerViewAdapter = new AllAssignedClassesAdapter2(AllAssignedClasses.this, classesArrayList, teacherName, teacherImage, whichYear);
                    AllAssignedClassesRecyclerView.setAdapter(recyclerViewAdapter);

                }
                else{
                    Toast.makeText(AllAssignedClasses.this, "No Data Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
}