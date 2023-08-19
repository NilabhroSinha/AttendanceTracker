package com.example.attendancetracker.Teacher.CreateNewSection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentModel.StudentModel;
import com.example.attendancetracker.Teacher.TeacherAdapters.AddStudentsAdapter;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class AddStudentsToClass extends AppCompatActivity {
    SearchView searchView;
    String department, classID, className, teacherName, teacherImage;
    SwitchMaterial showAll;
    ImageView emptyListID, backbutton;
    RecyclerView recyclerView;
    AddStudentsAdapter searchViewAdapter;
    ArrayList<StudentModel> allStudents;
    FirebaseDatabase database;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_students_to_class);

        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.my_purple));

        showAll = findViewById(R.id.switch1);
        emptyListID = findViewById(R.id.emptyListID);
        searchView = findViewById(R.id.search_bar);
        backbutton = findViewById(R.id.backbutton);
        allStudents = new ArrayList<>();

        HashSet<String> prevAddedStudents = new HashSet<>();

        department = getIntent().getStringExtra("department");
        classID = getIntent().getStringExtra("classID");
        className = getIntent().getStringExtra("className");
        teacherName = getIntent().getStringExtra("teacherName");
        teacherImage = getIntent().getStringExtra("teacherImage");

        auth = FirebaseAuth.getInstance();

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        database = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference =  database.getReference().child("student");

        FirebaseDatabase.getInstance().getReference().child("student").child(department).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                allStudents.clear();
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        StudentModel sm = dataSnapshot.getValue(StudentModel.class);
                        allStudents.add(sm);
                    }

                    allStudents.sort(Comparator.comparing(StudentModel::getName));

                    filter(searchView.getQuery().toString());
                    emptyListID.setVisibility(View.GONE);

                    searchViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference prevRegisteredStudents =  FirebaseDatabase.getInstance().getReference().child("Teacher").child(auth.getUid()).child(department).child(classID).child("allStudent");

        prevRegisteredStudents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds: snapshot.getChildren()) {

                        String str = ds.getValue(String.class);
                        prevAddedStudents.add(str);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        showAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    filter(searchView.getQuery().toString());
                    searchViewAdapter.notifyDataSetChanged();
                    emptyListID.setVisibility(View.GONE);
                }
                else {
                    if(searchView.getQuery().toString().length()==0)
                        emptyListID.setVisibility(View.VISIBLE);
                    else{
                        emptyListID.setVisibility(View.GONE);
                    }

                    filter(searchView.getQuery().toString());
                    searchViewAdapter.notifyDataSetChanged();

                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                searchViewAdapter.notifyDataSetChanged();
                return false;
            }
        });

        recyclerView = findViewById(R.id.recyclerV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchViewAdapter = new AddStudentsAdapter(AddStudentsToClass.this, prevAddedStudents, department, classID, className, teacherName, teacherImage);
        recyclerView.setAdapter(searchViewAdapter);


    }

    private void filter(String text) {
        // creating a new array list to filter our data.

        ArrayList<StudentModel> filteredList = new ArrayList<>();

        if(text.length() == 0 && !showAll.isChecked()) {
            searchViewAdapter.filterList(filteredList);
            return;
        }

        // running a for loop to compare elements.
        // checking if the entered string matched with any item of our recycler view.
        for (StudentModel row : allStudents) {
            String target = row.getName().toLowerCase().trim();
            String str = text.toLowerCase().trim();

            if(str.equals(target.substring(0, str.length())))
                filteredList.add(row);

        }

        if (filteredList.isEmpty()) {
            // if no item is added in filtered list we are
            // displaying a toast message as no data found.
            Toast.makeText(this, "No Data Found..", Toast.LENGTH_SHORT).show();
        }

        searchViewAdapter.filterList(filteredList);

    }
}