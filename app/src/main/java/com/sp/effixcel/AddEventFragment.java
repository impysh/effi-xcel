package com.sp.effixcel;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.time.LocalTime;
import java.util.Calendar;

public class AddEventFragment extends BottomSheetDialogFragment {
    private static final int RESULT_OK = Activity.RESULT_OK;
    private boolean isEditMode = false;
    private static final int RESULT_CODE_EVENT_DELETED = 101;

    private EditText eventNameET;
    private TextView alarmTimeTextView;
    private LocalTime time;
    private Button saveButton, deleteButton;
    private ImageButton selectTimeButton, setAlarmButton, cancelAlarmButton;
    private FrameLayout fEvent1, fEvent2, fEvent3, fEvent4, fEvent5;
    private ImageView imageColorRed, imageColorYellow, imageColorGreen, imageColorBlue, imageColorPurple;
    private int eventIndex;
    private int selectedColor = -1;
    private EventsHelper eventsHelper;
    private MaterialTimePicker timePicker; // Add this line
    private Calendar alarmCalendar; // Add this line
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent; // Add this line
    private Event event;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_event, container, false);
        eventsHelper = EventsHelper.instanceOfDatabase(requireContext());
        alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);


        // Retrieve the event data from the arguments
        Bundle args = getArguments();
        if (args != null && args.containsKey("EVENT")) {
            event = (Event) args.getSerializable("EVENT");
        }

        initWidgets(rootView);
        setOnClickListeners();
        loadEventData();
        createNotificationChannel();

        return rootView;
    }

    private void initWidgets(View rootView) {
        eventNameET = rootView.findViewById(R.id.eventNameET);
        alarmTimeTextView = rootView.findViewById(R.id.alarmTimeTextView);

        saveButton = rootView.findViewById(R.id.saveEventButton);
        deleteButton = rootView.findViewById(R.id.deleteEventButton);
        selectTimeButton = rootView.findViewById(R.id.selectTimeButton);
        setAlarmButton = rootView.findViewById(R.id.setAlarmButton);
        cancelAlarmButton = rootView.findViewById(R.id.cancelAlarmButton);

        fEvent1 = rootView.findViewById(R.id.fEvent1);
        fEvent2 = rootView.findViewById(R.id.fEvent2);
        fEvent3 = rootView.findViewById(R.id.fEvent3);
        fEvent4 = rootView.findViewById(R.id.fEvent4);
        fEvent5 = rootView.findViewById(R.id.fEvent5);

        imageColorRed = rootView.findViewById(R.id.red_color_event);
        imageColorYellow = rootView.findViewById(R.id.yellow_color_event);
        imageColorGreen = rootView.findViewById(R.id.green_color_event);
        imageColorBlue = rootView.findViewById(R.id.blue_color_event);
        imageColorPurple = rootView.findViewById(R.id.purple_color_event);
    }

    private void setOnClickListeners() {

        fEvent1.setOnClickListener(v -> {
            selectedColor = getResources().getColor(R.color.defaultColorRed);
            imageColorRed.setImageResource(R.drawable.baseline_check_24);
            imageColorYellow.setImageResource(0);
            imageColorGreen.setImageResource(0);
            imageColorBlue.setImageResource(0);
            imageColorPurple.setImageResource(0);
        });

        fEvent2.setOnClickListener(v -> {
            selectedColor = getResources().getColor(R.color.defaultColorYellow);
            imageColorRed.setImageResource(0);
            imageColorYellow.setImageResource(R.drawable.baseline_check_24);
            imageColorGreen.setImageResource(0);
            imageColorBlue.setImageResource(0);
            imageColorPurple.setImageResource(0);
        });

        fEvent3.setOnClickListener(v -> {
            selectedColor = getResources().getColor(R.color.defaultColorGreen);
            imageColorRed.setImageResource(0);
            imageColorYellow.setImageResource(0);
            imageColorGreen.setImageResource(R.drawable.baseline_check_24);
            imageColorBlue.setImageResource(0);
            imageColorPurple.setImageResource(0);
        });

        fEvent4.setOnClickListener(v -> {
            selectedColor = getResources().getColor(R.color.defaultColorBlue);
            imageColorRed.setImageResource(0);
            imageColorYellow.setImageResource(0);
            imageColorGreen.setImageResource(0);
            imageColorBlue.setImageResource(R.drawable.baseline_check_24);
            imageColorPurple.setImageResource(0);
        });

        fEvent5.setOnClickListener(v -> {
            selectedColor = getResources().getColor(R.color.defaultColorPurple);
            imageColorRed.setImageResource(0);
            imageColorYellow.setImageResource(0);
            imageColorGreen.setImageResource(0);
            imageColorBlue.setImageResource(0);
            imageColorPurple.setImageResource(R.drawable.baseline_check_24);
        });

        saveButton.setOnClickListener(view -> saveEvent());

        deleteButton.setOnClickListener(view -> deleteEvent());

        selectTimeButton.setOnClickListener(view -> {

            // Set the initial time for the time picker based on the previous alarm time
            int initialHour = (alarmCalendar != null) ? alarmCalendar.get(Calendar.HOUR_OF_DAY) : 12;
            int initialMinute = (alarmCalendar != null) ? alarmCalendar.get(Calendar.MINUTE) : 0;

            timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(initialHour)
                    .setMinute(initialMinute)
                    .setTitleText("Select Alarm Time")
                    .build();
            timePicker.show(requireActivity().getSupportFragmentManager(), "androidknowledge");
            timePicker.addOnPositiveButtonClickListener(view1 -> {
                if (timePicker.getHour() > 12) {
                    alarmTimeTextView.setText(
                            String.format("%02d", (timePicker.getHour() - 12)) + ":" + String.format("%02d", timePicker.getMinute()) + " PM"
                    );
                } else {
                    alarmTimeTextView.setText(timePicker.getHour() + ":" + timePicker.getMinute() + " AM");
                }
                alarmCalendar = Calendar.getInstance();
                alarmCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                alarmCalendar.set(Calendar.MINUTE, timePicker.getMinute());
                alarmCalendar.set(Calendar.SECOND, 0);
                alarmCalendar.set(Calendar.MILLISECOND, 0);
            });
        });


        setAlarmButton.setOnClickListener(view -> {
            if (alarmCalendar != null) {
                setAlarm();
            }
        });

        cancelAlarmButton.setOnClickListener(view -> {
            if (alarmCalendar != null) {
                cancelAlarm(requireContext()); // Assuming `requireContext()` returns the appropriate Context
            } else {
                Toast.makeText(requireContext(), "No alarm set", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadEventData() {
        Bundle args = getArguments();
        if (args != null && args.containsKey("EVENT_INDEX")) {
            eventIndex = args.getInt("EVENT_INDEX");
            if (eventIndex != -1) {
                event = Event.eventArrayList.get(eventIndex);
                eventNameET.setText(event.getName());
                time = event.getTime();

                isEditMode = true;
                deleteButton.setVisibility(View.VISIBLE);

                // Check if the event has a color set and update the selectedColor and color buttons accordingly
                if (event != null && event.getColor() != -1) {
                    selectedColor = event.getColor();
                }

                if (event.getAlarmTime() != null) {
                    alarmCalendar = Calendar.getInstance();
                    alarmCalendar.set(Calendar.HOUR_OF_DAY, event.getAlarmTime().getHour());
                    alarmCalendar.set(Calendar.MINUTE, event.getAlarmTime().getMinute());
                    alarmCalendar.set(Calendar.SECOND, 0);
                    alarmCalendar.set(Calendar.MILLISECOND, 0);

                    updateAlarmTimeTextView();
                } else {
                    alarmTimeTextView.setText("NULL");
                }
            }
        } else {
            time = LocalTime.now();
            isEditMode = false;
            alarmCalendar = null;
            deleteButton.setVisibility(View.GONE);
        }
    }

    private void saveEvent() {
        String eventName = eventNameET.getText().toString().trim();
        if (eventName.isEmpty()) {
            Toast.makeText(requireContext(), "Event name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            performEventUpdate();
        } else {
            performEventCreation(eventName);
        }
    }

    private void performEventUpdate() {
        if (eventIndex != -1 && eventIndex < Event.eventArrayList.size()) {
            Event eventToUpdate = Event.eventArrayList.get(eventIndex);

            // Check if the event name is changed and update it
            String eventName = eventNameET.getText().toString().trim();
            if (!eventName.isEmpty() && !eventName.equals(eventToUpdate.getName())) {
                eventToUpdate.setName(eventName);

                // Cancel the old notification and alarm
                cancelOldNotification();
                cancelOldAlarm();

                // Update the event name in the intent for AlarmReceiver
                Intent intent = new Intent(requireContext(), AlarmReceiver.class);
                intent.putExtra("EVENT_NAME", eventName);

                pendingIntent = PendingIntent.getBroadcast(requireContext(), eventToUpdate.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            // Check if the event time is changed and update it
            if (!time.equals(eventToUpdate.getTime())) {
                eventToUpdate.setTime(time);
            }

            // Check if the event color is changed and update it
            if (selectedColor != -1 && selectedColor != eventToUpdate.getColor()) {
                eventToUpdate.setColor(selectedColor);
            }

            // Check if the alarm time is changed and update it
            LocalTime alarmTime = (alarmCalendar != null) ? LocalTime.of(alarmCalendar.get(Calendar.HOUR_OF_DAY), alarmCalendar.get(Calendar.MINUTE)) : null;
            if (alarmTime != null && !alarmTime.equals(eventToUpdate.getAlarmTime())) {
                cancelOldAlarm(); // Cancel the old alarm before setting a new one during event update
                eventToUpdate.setAlarmTime(alarmTime);
                setAlarm(); // Set the new alarm
            } else if (alarmTime == null && eventToUpdate.getAlarmTime() != null) {
                cancelOldAlarm(); // Cancel the old alarm as the alarm is removed
                eventToUpdate.setAlarmTime(null);
            }

            setAlarm();

            // Update the event in the database
            eventsHelper.updateEventInDB(eventToUpdate);

            // Set the result and dismiss the dialog fragment
            Intent resultIntent = new Intent();
            resultIntent.putExtra("EVENT_INDEX", eventIndex);
            requireActivity().setResult(RESULT_OK, resultIntent);

// Get the parent activity's fragment manager
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

// Create a new FragmentTransaction
            FragmentTransaction transaction = fragmentManager.beginTransaction();

// Replace the current fragment with the CalendarFragment
            CalendarFragment calendarFragment = new CalendarFragment();
            transaction.replace(R.id.effixcelFragmentContainer, calendarFragment);

// Add the transaction to the back stack, if needed
            transaction.addToBackStack(null);

// Commit the transaction
            transaction.commit();

// Dismiss the BottomSheetDialogFragment
            dismiss();
        }
    }

    private void performEventCreation(String eventName) {
        int newEventColor = (selectedColor != -1) ? selectedColor : ContextCompat.getColor(requireContext(), R.color.defaultColorYellow);

        Event newEvent = new Event(eventName, CalendarUtils.selectedDate, time, null, newEventColor, alarmCalendar != null ? LocalTime.of(alarmCalendar.get(Calendar.HOUR_OF_DAY), alarmCalendar.get(Calendar.MINUTE)) : null);

        Event.eventArrayList.add(newEvent);
        eventsHelper.addEventToDatabase(newEvent);

        if (alarmCalendar != null) {
            setAlarm();
        }

        // Pass the newly created event name to the intent for AlarmReceiver
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("EVENT_NAME", eventName);

// Get the parent activity's fragment manager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

// Create a new FragmentTransaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

// Replace the current fragment with the CalendarFragment
        CalendarFragment calendarFragment = new CalendarFragment();
        transaction.replace(R.id.effixcelFragmentContainer, calendarFragment);

// Add the transaction to the back stack, if needed
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();

// Dismiss the BottomSheetDialogFragment
        dismiss();
    }

    private void deleteEvent() {
        if (isEditMode && eventIndex != -1 && eventIndex < Event.eventArrayList.size()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton("Delete", (dialog, which) -> performEventDeletion())
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
// Get the parent activity's fragment manager
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

// Create a new FragmentTransaction
            FragmentTransaction transaction = fragmentManager.beginTransaction();

// Replace the current fragment with the CalendarFragment
            CalendarFragment calendarFragment = new CalendarFragment();
            transaction.replace(R.id.effixcelFragmentContainer, calendarFragment);

// Add the transaction to the back stack, if needed
            transaction.addToBackStack(null);

// Commit the transaction
            transaction.commit();

// Dismiss the BottomSheetDialogFragment
            dismiss();        }
    }

    private void performEventDeletion() {
        Event eventToDelete = Event.eventArrayList.get(eventIndex);
        eventsHelper.deleteEventFromDB(eventToDelete.getId());
        Event.eventArrayList.remove(eventIndex);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("EVENT_INDEX", eventIndex);
        requireActivity().setResult(RESULT_CODE_EVENT_DELETED, resultIntent);

        cancelAlarm(requireContext()); // Assuming `requireContext()` returns the appropriate Context
// Get the parent activity's fragment manager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

// Create a new FragmentTransaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

// Replace the current fragment with the CalendarFragment
        CalendarFragment calendarFragment = new CalendarFragment();
        transaction.replace(R.id.effixcelFragmentContainer, calendarFragment);

// Add the transaction to the back stack, if needed
        transaction.addToBackStack(null);

// Commit the transaction
        transaction.commit();

// Dismiss the BottomSheetDialogFragment
        dismiss();    }

    private void updateAlarmTimeTextView() {
        if (alarmCalendar != null) {
            int hour = alarmCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = alarmCalendar.get(Calendar.MINUTE);
            String amPm = (hour >= 12) ? "PM" : "AM";

            if (hour > 12) {
                hour -= 12;
            }

            String alarmTime = String.format("%02d:%02d %s", hour, minute, amPm);
            alarmTimeTextView.setText(alarmTime);
        } else {
            alarmTimeTextView.setText("NULL");
        }
    }

    private void setAlarm() {
        if (alarmCalendar != null) {

            alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(requireContext(), AlarmReceiver.class);

            // Pass the event name to the AlarmReceiver class through the intent
            intent.putExtra("EVENT_NAME", eventNameET.getText().toString());

            pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);

            // Update the alarm time text view with the new alarm time
            updateAlarmTimeTextView();

            Toast.makeText(requireContext(), "Alarm Set", Toast.LENGTH_SHORT).show();
        }  else {
            Toast.makeText(requireContext(), "No alarm set", Toast.LENGTH_SHORT).show();
        }
    }

    private void cancelAlarm(Context context) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }

        alarmCalendar = null;
        // Update the alarm time text view to display "NULL"
        alarmTimeTextView.setText("NULL");

        pendingIntent.cancel(); // Also cancel the PendingIntent to remove the old notification

        Toast.makeText(context, "Alarm Cancelled", Toast.LENGTH_SHORT).show();
    }

    private void cancelOldAlarm() {
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null && pendingIntent != null) {
            // Cancel the old alarm and the notification pending intent
            alarmManager.cancel(pendingIntent);
        }
    }


    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "akchannel";
            String desc = "Channel for Alarm Manager";
            int imp = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("androidknowledge", name, imp);
            channel.setDescription(desc);
            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void cancelOldNotification() {
        // Create a PendingIntent with the old event name
        Intent intent = new Intent(requireContext(), AlarmReceiver.class);
        intent.putExtra("EVENT_NAME", event.getName()); // Use the old event name

        int requestCode = event.getId(); // Get the same request code used when setting the alarm

        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Cancel the old notification by canceling the PendingIntent
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}