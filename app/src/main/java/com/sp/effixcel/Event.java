package com.sp.effixcel;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

public class Event implements Serializable {
    public static ArrayList<Event> eventArrayList = new ArrayList<>();

    public static ArrayList<Event> eventsForDate(LocalDate date) {
        ArrayList<Event> events = new ArrayList<>();

        for (Event event : eventArrayList) {
            if (event.getDate().equals(date))
                events.add(event);
        }

        return events;
    }

    public static ArrayList<Event> eventsForDateAndTime(LocalDate date, LocalTime time) {
        ArrayList<Event> events = new ArrayList<>();

        for (Event event : eventArrayList) {
            int eventHour = event.getTime().getHour();
            int cellHour = time.getHour();
            if (event.getDate().equals(date) && eventHour == cellHour)
                events.add(event);
        }

        return events;
    }

    private int id;
    private String name;
    private LocalDate date;
    private LocalTime time;
    private Date deleted; // Using java.util.Date for compatibility
    private int color;
    private LocalTime alarmTime; // Add a variable to store the alarm time


    // Constructor for new events with an ID
    public Event(int id, String name, LocalDate date, LocalTime time, Date deleted, int color, LocalTime alarmTime) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.deleted = deleted;
        this.color = color;
        this.alarmTime = alarmTime;
    }

    // Constructor without the id parameter for new events
    public Event(String name, LocalDate date, LocalTime time, Date deleted, int color, LocalTime alarmTime) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.deleted = null;
        this.color = color;
        this.alarmTime = alarmTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setAlarmTime(LocalTime alarmTime) {
        this.alarmTime = alarmTime;
    }

    // Add a method to get the alarm time as LocalTime
    public LocalTime getAlarmTime() {
        return alarmTime;
    }
}