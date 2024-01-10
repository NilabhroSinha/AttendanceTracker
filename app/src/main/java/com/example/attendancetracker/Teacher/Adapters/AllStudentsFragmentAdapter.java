package com.example.attendancetracker.Teacher.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.StudentModel.StudentModel;
import com.example.attendancetracker.Teacher.ClassDetails.Fragments.AllStudentsFragment;
import com.example.attendancetracker.Teacher.TeacherAdapters.AllAssignedClassesAdapter2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllStudentsFragmentAdapter extends RecyclerView.Adapter<AllStudentsFragmentAdapter.ViewHolder>{
    String classID;
    Context context;
    ArrayList<StudentModel> arrayList;
    ArrayList<Integer> classDays = new ArrayList<>();

    public AllStudentsFragmentAdapter() {
    }

    public AllStudentsFragmentAdapter(Context context, String classID, ArrayList<StudentModel> arrayList) {
        this.context = context;
        this.classID = classID;
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

        FirebaseDatabase.getInstance().getReference().child("Teacher").child(FirebaseAuth.getInstance().getUid()).child(sm.getWhichYear()).child(classID).child("dateClass").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;
                classDays.clear();
                final int[] presentDays = {0};
                int classesTaken = 0;

                for (DataSnapshot snapshot1 : snapshot.child("classDays").getChildren()) {
                    Integer item = snapshot1.getValue(Integer.class);
                    classDays.add(item);
                }

                Date startDate = null, endDate = null;

                Long startDateLong = snapshot.child("startDate").child("time").getValue(Long.class);
                Long endDateLong = snapshot.child("endDate").child("time").getValue(Long.class);

                startDate = new Date(startDateLong);
                endDate = new Date(endDateLong);

                Date today = Calendar.getInstance().getTime();

                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTime(startDate);

                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(endDate);

                while (startCalendar.before(endCalendar)) {
                    Date currentDate = startCalendar.getTime();

                    int dayOfWeek = startCalendar.get(Calendar.DAY_OF_WEEK);


                    if(classDays.contains(dayOfWeek-2)){

                        if(currentDate.before(today) || currentDate.equals(today)){
                            classesTaken++;
                        }
                    }

                    startCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                int finalClassesTaken = classesTaken;
                FirebaseDatabase.getInstance().getReference().child("student").child(sm.getWhichYear()).child(sm.getStuID()).child("allClasses").child(classID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("presentDays").exists()){
                            for (DataSnapshot snapshot1 : snapshot.child("presentDays").getChildren()) {
                                presentDays[0]++;
                            }

                        }
                        double attendance = finalClassesTaken > 0 ? ((double)presentDays[0] / (double)finalClassesTaken)*100 : 0;
                        holder.attendance.setText(attendance+"%");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
        CircleImageView profile_image;
        TextView name, attendance;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.name);
            attendance = itemView.findViewById(R.id.attendance);
        }
    }
}
