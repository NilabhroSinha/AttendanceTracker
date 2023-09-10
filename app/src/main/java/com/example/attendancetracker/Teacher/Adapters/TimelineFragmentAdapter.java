package com.example.attendancetracker.Teacher.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Teacher.Attendance.TakeAttendance;
import com.example.attendancetracker.Teacher.ClassesPage.AllAssignedClasses;
import com.example.attendancetracker.Teacher.TeacherHomePage.TeacherHome;
import com.example.attendancetracker.Teacher.TeacherModel.AttendanceModel;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class TimelineFragmentAdapter extends RecyclerView.Adapter<TimelineFragmentAdapter.ViewHolder>{
    Context context;
    String className, time, classID, department;
    ArrayList<AttendanceModel> attendanceModels;

    public TimelineFragmentAdapter(Context context, ArrayList<AttendanceModel> attendanceModels, String className, String time, String classID, String department) {
        this.context = context;
        this.attendanceModels = attendanceModels;
        this.className = className;
        this.time = time;
        this.classID = classID;
        this.department = department;
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
            holder.timelineicon.setImageResource(R.drawable.greencheck);
            holder.transparent.setVisibility(View.GONE);
            holder.cardstatus.setBackgroundTintList(context.getResources().getColorStateList(R.color.grey));
            holder.scanner.setVisibility(View.GONE);
        }else if(date.after(today)){
            holder.timelineicon.setImageResource(R.drawable.greydot);
            holder.transparent.setVisibility(View.VISIBLE);
            holder.cardstatus.setBackgroundTintList(context.getResources().getColorStateList(R.color.yellow));
            holder.scanner.setVisibility(View.GONE);
        }else{
            holder.transparent.setVisibility(View.GONE);
            holder.timelineicon.setImageResource(R.drawable.hourglass);
            holder.cardstatus.setBackgroundTintList(context.getResources().getColorStateList(R.color.green));
            holder.scanner.setVisibility(View.VISIBLE);

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TakeAttendance.class);
                intent.putExtra("department", department);
                intent.putExtra("classID", classID);
                context.startActivity(intent);
            }
        });

        String month = getMonth(date.getMonth());
        holder.month.setText(month);

        holder.day.setText(new SimpleDateFormat("EEEE").format(date));
        holder.date.setText(String.valueOf(date.getDate()));

        holder.time.setText(time);
        holder.subject.setText(className);


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

