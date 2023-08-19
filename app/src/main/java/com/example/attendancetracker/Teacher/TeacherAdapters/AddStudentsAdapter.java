package com.example.attendancetracker.Teacher.TeacherAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentModel.StudentAttendance;
import com.example.attendancetracker.Student.StudentModel.StudentModel;
import com.example.attendancetracker.Student.StudentModel.StudentClassModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddStudentsAdapter extends RecyclerView.Adapter<AddStudentsAdapter.ViewHolder>{

    private final Context context;
    private List<StudentModel> allStudents = new ArrayList<>();
    String department, classID, className, teacherName, teacherImage;
    HashSet<String> prevAddedStudents;
    DatabaseReference studentRef, teacherRef;

    public AddStudentsAdapter(Context context, HashSet<String> prevAddedStudents, String department, String classID, String className, String teacherName, String teacherImage) {
        this.context = context;
        this.prevAddedStudents = prevAddedStudents;
        this.department = department;
        this.classID = classID;
        this.className = className;
        this.teacherName = teacherName;
        this.teacherImage = teacherImage;
    }

    public void filterList(ArrayList<StudentModel> filterlist) {
        // below line is to add our filtered
        // list in our course array list.
        allStudents = filterlist;
        // below line is to notify our adapter
        // as change in recycler view data.
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AddStudentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_all_students_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddStudentsAdapter.ViewHolder holder, int position) {
        String userName = position <= getItemCount() ? allStudents.get(position).getName() : "";
        String profilePic = position <= getItemCount() ? allStudents.get(position).getImageID() : "";
        String studentID = position <= getItemCount() ? allStudents.get(position).getStuID() : "";

        holder.name.setText(userName);
        Glide.with(context).load(profilePic).into(holder.profile_image);

        studentRef = FirebaseDatabase.getInstance().getReference().child("student").child(department).child(studentID);
        teacherRef = FirebaseDatabase.getInstance().getReference().child("Teacher").child(FirebaseAuth.getInstance().getUid()).child(department).child(classID);

        if(prevAddedStudents.contains(studentID)){
            holder.add.setVisibility(View.GONE);
            holder.check.setVisibility(View.VISIBLE);
        }
        else{
            holder.check.setVisibility(View.GONE);
            holder.add.setVisibility(View.VISIBLE);
            holder.add.setText("Add");
        }

        holder.check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, userName + " is already in your class", Toast.LENGTH_SHORT).show();
            }
        });

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> map = new HashMap<>();

                map.put(studentID, studentID);

                teacherRef.child("allStudents").updateChildren(map);

                Toast.makeText(context, userName + " has successfully entered class!", Toast.LENGTH_SHORT).show();

                HashMap<String, Object> map1 = new HashMap<>();

                int totalClassesTillNow = getTotalClassesTillNow();

                StudentClassModel studentClassModel = new StudentClassModel(className, teacherName, teacherImage, 0, getTotalClassesTillNow(), new ArrayList<StudentAttendance>());

                map1.put(classID, studentClassModel);
                FirebaseDatabase.getInstance().getReference().child("student").child(department).child(studentID).child("allClasses").updateChildren(map1);

            }
        });
    }

    private int getTotalClassesTillNow() {
        final int[] total = {0};
        teacherRef.child("totalClassesTillNow").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    total[0] = snapshot.getValue(Integer.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return total[0];
    }

    @Override
    public int getItemCount() {
        if(allStudents != null)
            return allStudents.size();
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profile_image;
        ImageView check;
        TextView name, add;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            check = itemView.findViewById(R.id.check);
            name = itemView.findViewById(R.id.name);
            profile_image = itemView.findViewById(R.id.profile_image);
            add = itemView.findViewById(R.id.add);

        }
    }
}
