package com.example.attendancetracker.Teacher.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
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
import com.example.attendancetracker.Student.StudentModel.StudentClassModel;
import com.example.attendancetracker.Student.StudentModel.StudentModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateAttendanceAdapter extends RecyclerView.Adapter<UpdateAttendanceAdapter.ViewHolder>{
    Context context;
    ArrayList<StudentModel> allStudents;
    Set<String> presentStudentList;
    String whichYear, classID;
    int pos;
    DatabaseReference studentRef, teacherRef;
    AlertDialog.Builder builder;
    RecyclerView rv;
    FirebaseAuth auth;
    FirebaseUser firebaseCurrentUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    public UpdateAttendanceAdapter(Context context, ArrayList<StudentModel> allStudents, Set<String> presentStudentList, String whichYear, String classID, int pos) {
        this.context = context;
        this.allStudents = allStudents;
        this.presentStudentList = presentStudentList;
        this.whichYear = whichYear;
        this.classID = classID;
        this.pos = pos;
    }

    @NonNull
    @Override
    public UpdateAttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_update_attendance, parent, false);
        return new UpdateAttendanceAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateAttendanceAdapter.ViewHolder holder, int position) {
        String userName = position <= getItemCount() ? allStudents.get(position).getName() : "";
        String profilePic = position <= getItemCount() ? allStudents.get(position).getImageID() : "";
        String studentID = position <= getItemCount() ? allStudents.get(position).getStuID() : "";

        holder.name.setText(userName);
        Glide.with(context).load(profilePic).into(holder.profile_image);

        studentRef = FirebaseDatabase.getInstance().getReference().child("student").child(whichYear).child(studentID);
        teacherRef = FirebaseDatabase.getInstance().getReference().child("Teacher").child(FirebaseAuth.getInstance().getUid()).child(whichYear).child(classID);


        teacherRef.child("timeTable").child(pos+"").child("presentStudents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(studentID)) {
                    holder.add.setVisibility(View.GONE);
                    holder.check.setVisibility(View.VISIBLE);
                }
                else{
                    holder.check.setVisibility(View.GONE);
                    holder.add.setVisibility(View.VISIBLE);
                    holder.add.setText("Mark present");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> map = new HashMap<>();

                map.put(studentID, studentID);

                teacherRef.child("timeTable").child(pos+"").child("presentStudents").updateChildren(map);

                FirebaseDatabase.getInstance().getReference().child("student").child(whichYear).child(studentID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String, Object> map1 = new HashMap<>();

                        teacherRef.child("timeTable").child(pos+"").child("date").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists()) return;
                                Long timeInLong = snapshot.child("time").getValue(Long.class);

                                Date date = new Date(timeInLong);

                                LocalDate localDate = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    map1.put(String.valueOf(localDate.getDayOfYear()), date);
                                }

                                FirebaseDatabase.getInstance().getReference().child("student").child(whichYear).child(studentID).child("allClasses").child(classID).child("presentDays").updateChildren(map1);

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

                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return allStudents != null ? allStudents.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, add;
        ImageView check;
        CircleImageView profile_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            add = itemView.findViewById(R.id.adds);
            profile_image = itemView.findViewById(R.id.profile_image);
            check = itemView.findViewById(R.id.checks);
        }
    }
}
