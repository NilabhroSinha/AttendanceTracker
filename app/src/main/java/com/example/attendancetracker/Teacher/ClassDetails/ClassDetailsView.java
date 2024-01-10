package com.example.attendancetracker.Teacher.ClassDetails;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.attendancetracker.R;
import com.example.attendancetracker.SignUp.Adapter.PageAdapter;
import com.example.attendancetracker.Student.StudentModel.StudentModel;
import com.example.attendancetracker.Teacher.Adapters.ClassDetailsFragmentAdapter;
import com.example.attendancetracker.Teacher.ClassDetails.Fragments.TimelineFragment;
import com.example.attendancetracker.Teacher.ClassesPage.AllAssignedClasses;
import com.example.attendancetracker.Teacher.CreateNewSection.AddStudentsToClass;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ClassDetailsView extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    AlertDialog.Builder builder;
    String whichYear, classID, className, time, teacherName, teacherImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details_view);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager=(ViewPager)findViewById(R.id.viewPager);

        builder = new AlertDialog.Builder(this);

        Toolbar toolbar = findViewById(R.id.toolbar); // Replace with your Toolbar ID
        setSupportActionBar(toolbar);

        tabLayout.addTab(tabLayout.newTab().setText("Timeline"));
        tabLayout.addTab(tabLayout.newTab().setText("Students"));

        whichYear = getIntent().getStringExtra("whichYear");
        classID = getIntent().getStringExtra("classID");
        time = getIntent().getStringExtra("time");
        className = getIntent().getStringExtra("className");
        teacherName = getIntent().getStringExtra("teacherName");
        teacherImage = getIntent().getStringExtra("teacherImage");

        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.my_purple));

        ViewPager viewPager = findViewById(R.id.viewPager);

        final ClassDetailsFragmentAdapter adapter = new ClassDetailsFragmentAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount(), whichYear, classID, time, className);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addStudent){
            Intent intent = new Intent(ClassDetailsView.this, AddStudentsToClass.class);
            intent.putExtra("className", className);
            intent.putExtra("whichYear", whichYear);
            intent.putExtra("teacherName", teacherName);
            intent.putExtra("teacherImage", teacherImage);
            intent.putExtra("classTime", time);
            intent.putExtra("classID", classID);
            this.startActivity(intent);
        } else if (id == R.id.deleteClass) {
            builder.setMessage("Do you want to delete this class?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            DatabaseReference friendListRef =  FirebaseDatabase.getInstance().getReference().child("Teacher").child(FirebaseAuth.getInstance().getUid()).child(whichYear);
                            DatabaseReference otherRef =  FirebaseDatabase.getInstance().getReference().child("student").child(whichYear);

                            ArrayList<StudentModel> students = new ArrayList<>();

                            otherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot snapshot1: snapshot.getChildren()){
                                        StudentModel sm = snapshot1.getValue(StudentModel.class);
                                        students.add(sm);
                                    }

                                    friendListRef.child(classID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            for(StudentModel sm: students){
                                                otherRef.child(sm.getStuID()).child("allClasses").child(classID).removeValue();
                                                finish();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.setTitle("Delete Class ");
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }
}