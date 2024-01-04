package com.example.attendancetracker.Student.TimeTable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancetracker.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    List<HolidayEvent> eventList;

    public EventAdapter(List<HolidayEvent> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_calendar_item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        HolidayEvent event = eventList.get(position);
        holder.eventTitle.setText(event.getTitle());

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        String formattedDate = sdf.format(event.getDateTime());
        holder.eventDateTime.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public HolidayEvent getItem(int position) {
        return eventList.get(position);
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle;
        TextView eventDateTime;

        EventViewHolder(View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventDateTime = itemView.findViewById(R.id.eventDateTime);
        }
    }
}
