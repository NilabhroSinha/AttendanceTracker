package com.example.attendancetracker.Teacher.ClassDetails.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Teacher.Adapters.TimelineFragmentAdapter;
import com.example.attendancetracker.Teacher.ClassesPage.AllAssignedClasses;
import com.example.attendancetracker.Teacher.TeacherAdapters.AllAssignedClassesAdapter2;
import com.example.attendancetracker.Teacher.TeacherModel.AttendanceModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;

public class TimelineFragment extends Fragment {
    String department, classID, className, time;
    RecyclerView recyclerView;
    TimelineFragmentAdapter timelineFragmentAdapter;
    FirebaseAuth auth;
    ArrayList<AttendanceModel> attendanceList = new ArrayList<>();
    public TimelineFragment() {
    }

    public TimelineFragment(String department, String classID, String className, String time) {
        this.department = department;
        this.classID = classID;
        this.time = time;
        this.className = className;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_timeline_fragment, null);

        recyclerView = view.findViewById(R.id.rv);
        auth = FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance().getReference().child("Teacher").child(auth.getUid()).child(department).child(classID).child("timeTable").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;
                attendanceList.clear();

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    AttendanceModel am = dataSnapshot.getValue(AttendanceModel.class);
                    attendanceList.add(am);
                }

                int pos = getCurrentOrNextDate(attendanceList);
                recyclerView.scrollToPosition(pos);
                timelineFragmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        timelineFragmentAdapter = new TimelineFragmentAdapter(getContext(), attendanceList, className, time, classID, department);

        recyclerView.setAdapter(timelineFragmentAdapter);
        return view;
    }

    public static int getCurrentOrNextDate(ArrayList<AttendanceModel> arr){

        LocalDate cd = null;
        LocalDate ldate = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            cd = LocalDate.now();
        }

        for (int i=0 ; i< arr.size() ;i++){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ldate = LocalDate.of(Integer.parseInt(new SimpleDateFormat("yyyy").format(arr.get(i).getDate())), Integer.parseInt(new SimpleDateFormat("MM").format(arr.get(i).getDate())), Integer.parseInt(new SimpleDateFormat("dd").format(arr.get(i).getDate())));   //   YYYY/MM/DD

                if(cd.isEqual(ldate) || cd.isBefore(ldate)){
                    return i;
                }
            }

        }
        return arr.size()-1;
    }
}