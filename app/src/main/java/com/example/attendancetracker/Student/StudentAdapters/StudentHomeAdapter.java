package com.example.attendancetracker.Student.StudentAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentHomePage.StudentHome;
import com.example.attendancetracker.Student.StudentModel.StudentClassModel;
import com.example.attendancetracker.Teacher.TeacherAdapters.AddStudentsAdapter;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentHomeAdapter extends RecyclerView.Adapter<StudentHomeAdapter.ViewHolder>{
    Context context;
    ArrayList<StudentClassModel> classesArrayList;

    public StudentHomeAdapter(Context context, ArrayList<StudentClassModel> classesArrayList) {
        this.context = context;
        this.classesArrayList = classesArrayList;
    }

    @NonNull
    @Override
    public StudentHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_classes_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentHomeAdapter.ViewHolder holder, int position) {
        StudentClassModel studentClassModel = classesArrayList.get(position);

        holder.teacher.setText(studentClassModel.getTeacherName());
        holder.subject.setText(studentClassModel.getClassName());

        Glide.with(context).load(studentClassModel.getTeacherImage()).into(holder.teacherDp);

    }

    @Override
    public int getItemCount() {
        if(classesArrayList == null)
            return 0;
        return classesArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subject, teacher, nextClass, attendance;
        CircleImageView teacherDp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.subject);
            attendance = itemView.findViewById(R.id.attendance);
            nextClass = itemView.findViewById(R.id.nextclass);
            teacher = itemView.findViewById(R.id.teacher);
            teacherDp = itemView.findViewById(R.id.teacherDP);
        }
    }
}
