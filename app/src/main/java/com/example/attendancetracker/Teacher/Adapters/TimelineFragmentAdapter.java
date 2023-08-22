package com.example.attendancetracker.Teacher.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancetracker.R;
import com.example.attendancetracker.Teacher.TeacherAdapters.AllAssignedClassesAdapter2;
import com.example.attendancetracker.Teacher.TeacherModel.AttendanceModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TimelineFragmentAdapter extends RecyclerView.Adapter<TimelineFragmentAdapter.ViewHolder>{
    Context context;
    String className, time;
    ArrayList<AttendanceModel> attendanceModels;

    public TimelineFragmentAdapter(Context context, ArrayList<AttendanceModel> attendanceModels, String className, String time) {
        this.context = context;
        this.attendanceModels = attendanceModels;
        this.className = className;
        this.time = time;
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

        Date date = null;
        if(am != null) {

            date = am.getDate();
        }

        if(date != null){
            String month = getMonth(date.getMonth());
            holder.month.setText(month);

            holder.day.setText(new SimpleDateFormat("EEEE").format(date));
            holder.date.setText(String.valueOf(date.getDate()));
        }

        holder.time.setText(time);
        holder.subject.setText(className);
    }

    private String getMonth(int month) {
        switch (month){
            case 0:  return "JAN";
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.day);
            month = itemView.findViewById(R.id.month);
            date = itemView.findViewById(R.id.date);
            subject = itemView.findViewById(R.id.subject);
            time = itemView.findViewById(R.id.time);
        }
    }
}
