package com.example.attendancetracker.Teacher.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentModel.StudentModel;
import com.example.attendancetracker.Teacher.Attendance.TakeAttendance;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TakeAttendanceAdapter extends RecyclerView.Adapter<TakeAttendanceAdapter.ViewHolder>{
    Context context;
    ArrayList<String> arrayList;
    String department;

    public TakeAttendanceAdapter() {
    }

    public TakeAttendanceAdapter(Context context, ArrayList<String> arrayList, String department) {
        this.context = context;
        this.arrayList = arrayList;
        this.department = department;
    }

    @NonNull
    @Override
    public TakeAttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_all_student_fragment_layout, parent, false);
        return new TakeAttendanceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TakeAttendanceAdapter.ViewHolder holder, int position) {
        String stuID = arrayList.get(position);

        FirebaseDatabase.getInstance().getReference().child("student").child(department).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;

                StudentModel sm = snapshot.child(stuID).getValue(StudentModel.class);

                String name = sm.getName();
                String imageID = sm.getImageID();

                holder.add.setTextSize(18);
                holder.add.setTextColor(Color.parseColor("#228B22"));

                holder.name.setText(name);
                holder.add.setText("Present");
                Glide.with(context).load(imageID).into(holder.dp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, add;
        CircleImageView dp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            add = itemView.findViewById(R.id.attendance);
            dp = itemView.findViewById(R.id.profile_image);
        }
    }
}
