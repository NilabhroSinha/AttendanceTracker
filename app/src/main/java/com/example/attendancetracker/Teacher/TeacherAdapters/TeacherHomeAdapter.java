package com.example.attendancetracker.Teacher.TeacherAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentModel.StudentClassModel;

import java.util.ArrayList;

public class TeacherHomeAdapter extends RecyclerView.Adapter<TeacherHomeAdapter.ViewHolder>{
    Context context;
    ArrayList<StudentClassModel> arrayList;
    AlertDialog.Builder builder;

    @NonNull
    @Override
    public TeacherHomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_classes_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherHomeAdapter.ViewHolder holder, int position) {
        StudentClassModel classModel = arrayList.get(position);
        subject.setText(classModel.getClassName().toString());
//        attendance.setText(getAttendance());


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    TextView subject, attendance, nextClass, teacherName, teacherDP;
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            subject = itemView.findViewById(R.id.subject);
            attendance= itemView.findViewById(R.id.attendance);
            nextClass= itemView.findViewById(R.id.nextclass);
            teacherName= itemView.findViewById(R.id.teacher);
            teacherDP= itemView.findViewById(R.id.teacherDP);
        }
    }
}
