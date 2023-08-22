package com.example.attendancetracker.Teacher.TeacherAdapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.example.attendancetracker.Teacher.Adapters.AllAssignedClassesAdapter;
import com.example.attendancetracker.Teacher.ClassDetails.ClassDetailsView;
import com.example.attendancetracker.Teacher.TeacherModel.DateClass;
import com.example.attendancetracker.Teacher.TeacherModel.TeacherClassModel;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllAssignedClassesAdapter2  extends RecyclerView.Adapter<AllAssignedClassesAdapter2.ViewHolder>{
    Context context;
    String profilePicture, department;
    ArrayList<TeacherClassModel> classesArrayList;

    public AllAssignedClassesAdapter2(Context context, ArrayList<TeacherClassModel> classesArrayList, String profilePicture, String department) {
        this.context = context;
        this.classesArrayList = classesArrayList;
        this.profilePicture = profilePicture;
        this.department = department;
    }

    @NonNull
    @Override
    public AllAssignedClassesAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_teacher_class_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllAssignedClassesAdapter2.ViewHolder holder, int position) {
        if(classesArrayList == null) return;

        String className = position <= getItemCount() ? classesArrayList.get(position).getClassName() : "";
        String time = position <= getItemCount() ? classesArrayList.get(position).getClass_Timing() : "";
        DateClass date = position <= getItemCount() ? classesArrayList.get(position).getDateClass() : null;

        Date startDate = date.getStartDate();
        Date endDate = date.getEndDate();
        ArrayList<Integer> classDays = date.getClassDays();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");

        int startYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(startDate));
        int startMonth = Integer.parseInt(new SimpleDateFormat("MM").format(startDate));
        int startDay = Integer.parseInt(new SimpleDateFormat("dd").format(startDate));

        int lastYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(endDate));
        int lastMonth = Integer.parseInt(new SimpleDateFormat("MM").format(endDate));
        int lastDay = Integer.parseInt(new SimpleDateFormat("dd").format(endDate));

        LocalDate firstClass = null, lastDate = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            firstClass = LocalDate.of(startYear, startMonth, startDay);   //   YYYY/MM/DD
            lastDate = LocalDate.of(lastYear, lastMonth, lastDay);
        }

        long remainingClasses = getTotalNoOfClasses(classDays, firstClass, lastDate);
        String nextClass = getNextClass(classDays);

        holder.remainingClasses.setText("Remaining Classes: " + remainingClasses);
        holder.nextClass.setText("Next Class: "+nextClass);
        Glide.with(context).load(profilePicture).into(holder.teacherDp);
        holder.subject.setText(className);
        holder.time.setText("Time: " + time + "AM");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ClassDetailsView.class);
                intent.putExtra("className", className);
                intent.putExtra("department", department);
                intent.putExtra("time", time);
                intent.putExtra("classID", classesArrayList.get(position).getClassID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(classesArrayList != null)
            return classesArrayList.size();
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subject, remainingClasses, nextClass, time;
        CircleImageView teacherDp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            subject = itemView.findViewById(R.id.subject);
            remainingClasses = itemView.findViewById(R.id.remainingClasses);
            nextClass = itemView.findViewById(R.id.nextclass);
            time = itemView.findViewById(R.id.time);
            teacherDp = itemView.findViewById(R.id.teacherDP);
        }
    }

    public static int getNextDay(ArrayList<Integer> arr,int day){
        if(arr == null)
            return 0;

        for (int i = 0; i <arr.size(); i++){

            if(arr.get(i)>day){
                return arr.get(i);
            }
        }

        for (int i = 0; i <arr.size(); i++){

            if(arr.get(i)<day){
                return arr.get(i);
            }
        }

        return 0;
    }

    public static  String getNextClass(ArrayList<Integer> arr){
        if(arr == null) return "";
        ArrayList<String> weekd = new ArrayList<String>(7);


        //Initialize current date
        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
        }
        DayOfWeek day = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            day = currentDate.getDayOfWeek();
        }

        weekd.add("MONDAY");
        weekd.add("TUESDAY");
        weekd.add("WEDNESDAY");
        weekd.add("THURSDAY");
        weekd.add("FRIDAY");
        weekd.add("SATURDAY");
        weekd.add("SUNDAY");

        if(day.name()=="MONDAY"){
            return weekd.get(getNextDay(arr,0));
        }
        else if(day.name()=="TUESDAY"){
            return weekd.get(getNextDay(arr,1));
        }
        else if(day.name()=="WEDNESDAY"){
            return weekd.get(getNextDay(arr,2));
        }
        else if(day.name()=="THURSDAY"){
            return weekd.get(getNextDay(arr,3));
        }
        else if(day.name()=="FRIDAY"){
            return weekd.get(getNextDay(arr,5));
        }
        else{
            return weekd.get(getNextDay(arr,0));
        }

    }

    public static long noOfMondaysBetween(LocalDate first, LocalDate last) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (last.isBefore(first)) {
                throw new IllegalArgumentException("first " + first + " was after last " + last);
            }
        }
        // find first Monday in interval
        LocalDate firstMonday = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            firstMonday = first.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
        }
        // similarly find last Monday
        LocalDate lastMonday = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lastMonday = last.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
        // count
        long number = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            number = ChronoUnit.WEEKS.between(firstMonday, lastMonday);
        }
        // add one to count both first Monday and last Monday in
        return number + 1;
    }

    //Counting no of tuesdays
    public static long noOfTuesdaysBetween(LocalDate first, LocalDate last) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (last.isBefore(first)) {
                throw new IllegalArgumentException("first " + first + " was after last " + last);
            }
        }
        // find first tuesday in interval
        LocalDate firstTuesday = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            firstTuesday = first.with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
        }
        // similarly find last Tuesday
        LocalDate lastTuesday = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            lastTuesday = last.with(TemporalAdjusters.previousOrSame(DayOfWeek.TUESDAY));
        }
        // count
        long number = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            number = ChronoUnit.WEEKS.between(firstTuesday, lastTuesday);
        }
        // add one to count both first tuesday and last tuesday in
        return number + 1;
    }

    //Counting no of wednesdays
    public static long noOfWednesdaysBetween(LocalDate first, LocalDate last) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (last.isBefore(first)) {
                throw new IllegalArgumentException("first " + first + " was after last " + last);
            }
        }
        // find first wednesday in interval
        LocalDate firstWednesday = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            firstWednesday = first.with(TemporalAdjusters.nextOrSame(DayOfWeek.WEDNESDAY));
        }
        // similarly find last Tuesday
        LocalDate lastWednesday = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            lastWednesday = last.with(TemporalAdjusters.previousOrSame(DayOfWeek.WEDNESDAY));
        }
        // count
        long number = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            number = ChronoUnit.WEEKS.between(firstWednesday, lastWednesday);
        }
        // add one to count both first wednesday and last wednesday in
        return number + 1;
    }

    //Counting no of thursdays
    public static long noOfThursdaysBetween(LocalDate first, LocalDate last) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (last.isBefore(first)) {
                throw new IllegalArgumentException("first " + first + " was after last " + last);
            }
        }
        // find first thursday in interval
        LocalDate firstThursday = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            firstThursday = first.with(TemporalAdjusters.nextOrSame(DayOfWeek.THURSDAY));
        }
        // similarly find last Thursday
        LocalDate lastThursday = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            lastThursday = last.with(TemporalAdjusters.previousOrSame(DayOfWeek.THURSDAY));
        }
        // count
        long number = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            number = ChronoUnit.WEEKS.between(firstThursday, lastThursday);
        }
        // add one to count both first thursday and last thursday in
        return number + 1;
    }

    //Counting no of fridays
    public static long noOfFridaysBetween(LocalDate first, LocalDate last) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (last.isBefore(first)) {
                throw new IllegalArgumentException("first " + first + " was after last " + last);
            }
        }
        // find first friday in interval
        LocalDate firstFriday = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            firstFriday = first.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY));
        }
        // similarly find last friday
        LocalDate lastFriday = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            lastFriday = last.with(TemporalAdjusters.previousOrSame(DayOfWeek.FRIDAY));
        }
        // count
        long number = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            number = ChronoUnit.WEEKS.between(firstFriday, lastFriday);
        }
        // add one to count both first thursday and last thursday in
        return number + 1;
    }

    //calculate total no of classes
    public static long getTotalNoOfClasses(ArrayList<Integer> arr,LocalDate firstDate, LocalDate lastDate){
        if(arr == null) return 0L;

        long classes=0;

        for (int i = 0; i <arr.size(); i++) {

            if(arr.get(i)==0){
                classes=classes+noOfMondaysBetween(firstDate,lastDate);
            }
            else if(arr.get(i)==1){
                classes=classes+noOfTuesdaysBetween(firstDate,lastDate);



            }
            else if(arr.get(i)==2){

                classes=classes+noOfWednesdaysBetween(firstDate,lastDate);

            }
            else if(arr.get(i)==3){

                classes=classes+noOfThursdaysBetween(firstDate,lastDate);

            }
            else{
                classes=classes+noOfFridaysBetween(firstDate,lastDate);

            }

        }

        return classes;
        //  System.out.println("no of mondays:"+noOfMondaysBetween(f,l));
    }

    public static long getRemainingClasses(ArrayList<Integer> arr,LocalDate first, LocalDate last){
        if(arr == null) return 0L;
        LocalDate cd = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            cd = LocalDate.now();
        }

        long totalNoOfClasses = getTotalNoOfClasses(arr,first,last);
        long classesTaken = getTotalNoOfClasses(arr,first,cd);

        return totalNoOfClasses - classesTaken;
    }
}
