package com.sp.effixcel;

// CalendarFragment.java

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener,
        EventAdapter.OnItemClickListener {
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;
    private Button previousWeekButton, nextWeekButton, newEventButton;
    private static final int REQUEST_CODE_EDIT_EVENT = 1;

    private EventsHelper eventsHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Initialize selectedDate to the current date if it's null
        if (CalendarUtils.selectedDate == null) {
            CalendarUtils.selectedDate = LocalDate.now();
        }

        eventsHelper = EventsHelper.instanceOfDatabase(requireContext());

        initWidgets(view);
        setWeekView();
        return view;
    }

    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
        eventListView = view.findViewById(R.id.eventListView);
        previousWeekButton = view.findViewById(R.id.previousWeekButton);
        nextWeekButton = view.findViewById(R.id.nextWeekButton);
        newEventButton = view.findViewById(R.id.newEventButton);

        // Set OnClickListener for the buttons
        previousWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
                setWeekView();
            }
        });

        nextWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
                setWeekView();
            }
        });

        newEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show the AddEventFragment as a BottomSheetDialogFragment
                AddEventFragment fragment = new AddEventFragment();
                fragment.show(getChildFragmentManager(), "ADD_EVENT_FRAGMENT_TAG");
            }
        });
    }

    private void setWeekView() {
        monthYearText.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = CalendarUtils.daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setEventAdapter();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalendarUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    public void onResume() {
        super.onResume();
        setEventAdapter();
    }

    private void setEventAdapter() {
        ArrayList<Event> dailyEvents = Event.eventArrayList;

        // Clear the eventArrayList before adding new events
        Event.eventArrayList.clear();
        Event.eventArrayList.addAll(eventsHelper.eventsForDate(CalendarUtils.selectedDate));

        EventAdapter eventAdapter = new EventAdapter(requireContext(), dailyEvents);
        eventAdapter.setOnItemClickListener(this);
        eventListView.setAdapter(eventAdapter);
    }

    @Override
    public void onEventClick(Event event) {
        // Start the AddEventFragment with the selected event's information
        AddEventFragment fragment = new AddEventFragment();

        // Pass the event index as an argument
        Bundle args = new Bundle();
        args.putInt("EVENT_INDEX", Event.eventArrayList.indexOf(event));
        fragment.setArguments(args);

        fragment.show(getChildFragmentManager(), "ADD_EVENT_FRAGMENT_TAG");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDIT_EVENT && resultCode == getActivity().RESULT_OK) {
            // Retrieve the event index from the result data
            int eventIndex = data.getIntExtra("EVENT_INDEX", -1);

            if (eventIndex != -1) {
                // Refresh the eventListView after editing an event
                setEventAdapter();
            }
        }
    }
}