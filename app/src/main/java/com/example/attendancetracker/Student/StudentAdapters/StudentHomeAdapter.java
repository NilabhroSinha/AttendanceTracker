package com.example.attendancetracker.Student.StudentAdapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.attendancetracker.R;
import com.example.attendancetracker.Student.ClassAttendanceDetails.ClassDetails;
import com.example.attendancetracker.Student.StudentHomePage.StudentHome;
import com.example.attendancetracker.Student.StudentModel.StudentClassModel;
import com.example.attendancetracker.Teacher.ClassDetails.ClassDetailsView;
import com.example.attendancetracker.Teacher.TeacherAdapters.AddStudentsAdapter;
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

public class StudentHomeAdapter extends RecyclerView.Adapter<StudentHomeAdapter.ViewHolder>{
    String whichYear;
    Context context;
    ArrayList<StudentClassModel> classesArrayList;

    public StudentHomeAdapter(Context context, String whichYear, ArrayList<StudentClassModel> classesArrayList) {
        this.whichYear = whichYear;
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
        final StudentClassModel[] studentClassModel = {classesArrayList.get(position)};

        holder.teacher.setText(studentClassModel[0].getTeacherName());
        holder.subject.setText(studentClassModel[0].getClassName());


        Glide.with(context).load(studentClassModel[0].getTeacherImage()).into(holder.teacherDp);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ClassDetails.class);
//                intent.putExtra("className", className);
                intent.putExtra("whichYear", whichYear);
                intent.putExtra("teacherID", studentClassModel[0].getTeacherID());
                intent.putExtra("classID", classesArrayList.get(position).getClassID());
                context.startActivity(intent);
            }
        });

        ArrayList<Integer> classDays = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Teacher").child(studentClassModel[0].getTeacherID()).child(whichYear).child(studentClassModel[0].getClassID()).child("dateClass").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;
                classDays.clear();
                final int[] presentDays = {0};
                int classesTaken = 0;
                int classesRemaining = 0;

                for (DataSnapshot snapshot1 : snapshot.child("classDays").getChildren()) {
                    Integer item = snapshot1.getValue(Integer.class);
                    classDays.add(item);
                }

                holder.nextClass.setText("Next class: "+AllAssignedClassesAdapter2.getNextClass(classDays));

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

                        if(currentDate.before(today)){
                            classesTaken++;
                        } else if (currentDate.after(today)) {
                            classesRemaining++;
                        }else{
                            classesTaken++;
                        }
                    }

                    startCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                int finalClassesTaken = classesTaken;
                FirebaseDatabase.getInstance().getReference().child("student").child(whichYear).child(FirebaseAuth.getInstance().getUid()).child("allClasses").child(studentClassModel[0].getClassID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("presentDays").exists()){
                            for (DataSnapshot snapshot1 : snapshot.child("presentDays").getChildren()) {
                                presentDays[0]++;
                            }

                        }

                        double attendance = finalClassesTaken > 0 ? ((double) presentDays[0] / (double) finalClassesTaken)*100 : 0;
                        holder.attendance.setText("Attendance: " +attendance+"%");

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
            attendance = itemView.findViewById(R.id.atten);
            nextClass = itemView.findViewById(R.id.nxtclass);
            teacher = itemView.findViewById(R.id.teacher);
            teacherDp = itemView.findViewById(R.id.teacherDP);
        }
    }
}
