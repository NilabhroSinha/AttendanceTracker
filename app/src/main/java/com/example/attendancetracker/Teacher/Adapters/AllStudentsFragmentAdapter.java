package com.example.attendancetracker.Teacher.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentModel.StudentModel;
import com.example.attendancetracker.Teacher.ClassDetails.Fragments.AllStudentsFragment;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllStudentsFragmentAdapter extends RecyclerView.Adapter<AllStudentsFragmentAdapter.ViewHolder>{
    Context context;
    ArrayList<StudentModel> arrayList;

    public AllStudentsFragmentAdapter() {
    }

    public AllStudentsFragmentAdapter(Context context, ArrayList<StudentModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public AllStudentsFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_all_student_fragment_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllStudentsFragmentAdapter.ViewHolder holder, int position) {
        StudentModel sm = arrayList.get(position);

        holder.name.setText(sm.getName());
        Glide.with(context).load(sm.getImageID()).into(holder.profile_image);
    }

    @Override
    public int getItemCount() {
        return arrayList == null ? 0 : arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profile_image;
        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.name);
        }
    }
}
