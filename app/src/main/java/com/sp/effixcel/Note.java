package com.sp.effixcel;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;

public class Note {
    public static ArrayList<Note> noteArrayList = new ArrayList<>();
    public static String NOTE_EDIT_EXTRA =  "noteEdit";
    private int id;
    private String description;
    private Date deleted;
    private int color;

    public Note(int id, String description, Date deleted, int color) {
        this.id = id;
        this.description = description;
        this.deleted = deleted;
        this.color = color;
    }


    public Note(int id, String description) {
        this.id = id;
        this.description = description;
        deleted = null;
    }

    public static Note getNoteForID(int passedNoteID) {
        for (Note note : noteArrayList) {
            if (note.getId() == passedNoteID)
                return note;
        }
        return null;
    }

    public static ArrayList<Note> nonDeletedNotes() {
        ArrayList<Note> nonDeleted = new ArrayList<>();
        for (Note note : noteArrayList) {
            if (note.getDeleted() == null)
                nonDeleted.add(note);
        }
        return nonDeleted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

}