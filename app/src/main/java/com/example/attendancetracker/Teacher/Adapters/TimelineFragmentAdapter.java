package com.example.attendancetracker.Teacher.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Teacher.Attendance.TakeAttendance;
import com.example.attendancetracker.Teacher.ClassDetails.Fragments.TimelineFragment;
import com.example.attendancetracker.Teacher.TeacherModel.AttendanceModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class TimelineFragmentAdapter extends RecyclerView.Adapter<TimelineFragmentAdapter.ViewHolder>{
    Context context;
    String className, time, classID, whichYear;
    ArrayList<AttendanceModel> attendanceModels;

    public TimelineFragmentAdapter(Context context, ArrayList<AttendanceModel> attendanceModels, String className, String time, String classID, String whichYear) {
        this.context = context;
        this.attendanceModels = attendanceModels;
        this.className = className;
        this.time = time;
        this.classID = classID;
        this.whichYear = whichYear;
    }

    @NonNull
    @Override
    public TimelineFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_class_details_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineFragmentAdapter.ViewHolder holder, int position) {
        AttendanceModel am = position <= getItemCount() ? attendanceModels.get(position) : null;
        if(am == null) return;

        Date date = am.getDate();

        Date today = null;
        LocalDate localDate = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            localDate = LocalDate.now();
            today = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        if(date.before(today)){
            setAttendancePercentage(holder, am.getDate());
            holder.timelineicon.setImageResource(R.drawable.greencheck);
            holder.transparent.setVisibility(View.GONE);
            holder.cardstatus.setBackgroundTintList(context.getResources().getColorStateList(R.color.grey));
            holder.scanner.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TakeAttendance.class);
                    intent.putExtra("whichYear", whichYear);
                    intent.putExtra("classID", classID);
                    intent.putExtra("date", date);
                    context.startActivity(intent);
                }
            });
        }else if(date.after(today)){
            holder.timelineicon.setImageResource(R.drawable.greydot);
            holder.subject.setText("To Be Taken");
            holder.transparent.setVisibility(View.VISIBLE);
            holder.cardstatus.setBackgroundTintList(context.getResources().getColorStateList(R.color.yellow));
            holder.scanner.setVisibility(View.GONE);
        }else{
            setAttendancePercentage(holder, am.getDate());
            holder.transparent.setVisibility(View.GONE);
            holder.timelineicon.setImageResource(R.drawable.hourglass);
            holder.cardstatus.setBackgroundTintList(context.getResources().getColorStateList(R.color.green));
            holder.scanner.setVisibility(View.VISIBLE);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TakeAttendance.class);
                    intent.putExtra("whichYear", whichYear);
                    intent.putExtra("classID", classID);
                    intent.putExtra("date", date);
                    context.startActivity(intent);
                }
            });
        }



        String month = getMonth(date.getMonth());
        holder.month.setText(month);

        holder.day.setText(new SimpleDateFormat("EEEE").format(date));
        holder.date.setText(String.valueOf(date.getDate()));

        holder.time.setText(time);

    }

    private void setAttendancePercentage(ViewHolder holder, Date date) {
        DatabaseReference classDatabase = FirebaseDatabase.getInstance().getReference().child("Teacher").child(FirebaseAuth.getInstance().getUid()).child(whichYear).child(classID);

        classDatabase.child("allStudents").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) return;
                int totalStudents = 0;

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    totalStudents++;
                }

                int finalTotalStudents = totalStudents;
                classDatabase.child("timeTable").child(String.valueOf(TakeAttendance.getCurrentOrNextDate(attendanceModels, date))).child("presentStudents").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists()) return;

                        int presentStudents = 0;

                        for(DataSnapshot snapshot1: snapshot.getChildren()){
                            presentStudents++;
                        }

                        double percentage = Math.round(((double) presentStudents/(double) finalTotalStudents) * 100);

                        holder.subject.setText(percentage + "%");
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

    private String getMonth(int month) {
        switch (month){
            case 0: return "JAN";
            case 1: return "FEB";
            case 2: return "MAR";
            case 3: return "APR";
            case 4: return "MAY";
            case 5: return "JUN";
            case 6: return "JUL";
            case 7: return "AUG";
            case 8: return "SEP";
            case 9: return "OCT";
            case 10: return "NOV";
            case 11: return "DEC";
            default: return "";
        }
    }

    @Override
    public int getItemCount() {
        if(attendanceModels == null)
            return 0;
        return attendanceModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView day, month, date, subject, time;
        ConstraintLayout transparent, cardstatus;
        CircleImageView timelineicon;
        ImageView scanner;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.day);
            month = itemView.findViewById(R.id.month);
            date = itemView.findViewById(R.id.date);
            subject = itemView.findViewById(R.id.subject);
            time = itemView.findViewById(R.id.time);
            transparent = itemView.findViewById(R.id.transparent);
            timelineicon = itemView.findViewById(R.id.timelineIcon);
            cardstatus = itemView.findViewById(R.id.cardstatus);
            scanner = itemView.findViewById(R.id.scanner);
        }
    }
}

