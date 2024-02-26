package com.sp.effixcel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalTime;
import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {
    private OnItemClickListener listener;

    public EventAdapter(@NonNull Context context, List<Event> events) {
        super(context, 0, events);
    }

    private static class ViewHolder {
        LinearLayout eventCellLayout;
        TextView eventCellTV;
        TextView eventCellAlarm;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Event event = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_event_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.eventCellAlarm = convertView.findViewById(R.id.eventCellAlarm);
            viewHolder.eventCellLayout = convertView.findViewById(R.id.eventCellLayout);
            viewHolder.eventCellTV = convertView.findViewById(R.id.eventCellTV);
            viewHolder.eventCellAlarm = convertView.findViewById(R.id.eventCellAlarm); // Add this line to initialize the TextView
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String eventTitle = event.getName();
        viewHolder.eventCellTV.setText(eventTitle);
        viewHolder.eventCellLayout.setBackgroundColor(event.getColor());

        // Check if the event has an alarm time set
        if (event.getAlarmTime() != null) {
            viewHolder.eventCellAlarm.setVisibility(View.VISIBLE);
            viewHolder.eventCellAlarm.setText("ALARM SET: " + formatAlarmTime(event.getAlarmTime()));
        } else {
            viewHolder.eventCellAlarm.setVisibility(View.GONE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onEventClick(event);
                }
            }
        });

        return convertView;
    }

    // Add this method to format the alarm time
    private String formatAlarmTime(LocalTime alarmTime) {
        int hour = alarmTime.getHour();
        int minute = alarmTime.getMinute();
        String amPm = (hour >= 12) ? "PM" : "AM";

        if (hour > 12) {
            hour -= 12;
        }

        return String.format("%02d:%02d %s", hour, minute, amPm);
    }

    public interface OnItemClickListener {
        void onEventClick(Event event);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}