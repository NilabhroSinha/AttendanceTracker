package com.example.attendancetracker.Teacher.ClassDetails.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentModel.StudentModel;
import com.example.attendancetracker.Teacher.Adapters.AllStudentsFragmentAdapter;
import com.example.attendancetracker.Teacher.ClassesPage.AllAssignedClasses;
import com.example.attendancetracker.Teacher.TeacherAdapters.AllAssignedClassesAdapter2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class AllStudentsFragment extends Fragment {
    String department, classID;
    RecyclerView recyclerView;
    AllStudentsFragmentAdapter recyclerViewAdapter;
    FirebaseAuth auth;
    ArrayList<StudentModel> arrayList = new ArrayList<>();

    public AllStudentsFragment() {
    }

    public AllStudentsFragment(String department, String classID) {
        this.department = department;
        this.classID = classID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_timeline_fragment, null);

        recyclerView = view.findViewById(R.id.rv);
        auth = FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance().getReference().child("Teacher").child(auth.getUid()).child(department).child(classID).child("allStudents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    String studentID = dataSnapshot.getValue(String.class);

                    FirebaseDatabase.getInstance().getReference().child("student").child(department).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(!snapshot.exists()) return;
//                            arrayList.clear();
                            for(DataSnapshot dataSnapshot1: snapshot.getChildren()){
                                StudentModel sm = dataSnapshot1.getValue(StudentModel.class);

                                if(Objects.equals(studentID, sm.getStuID())){
                                    arrayList.add(sm);
                                }
                            }

                            recyclerViewAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewAdapter = new AllStudentsFragmentAdapter(getContext(), arrayList);
        recyclerView.setAdapter(recyclerViewAdapter);

        return view;
    }
}