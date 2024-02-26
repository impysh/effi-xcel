package com.sp.effixcel;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Date;

public class AddNoteFragment extends BottomSheetDialogFragment {
    private static final int RESULT_OK = Activity.RESULT_OK;

    // UI elements
    private EditText descEditText;
    private Button deleteButton;
    private ImageView imageColorRed, imageColorYellow, imageColorGreen, imageColorBlue, imageColorPurple;
    private int selectedColor = -1;
    private Note selectedNote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_note, container, false);
        initWidgets(view);

        checkForEditNote();

        return view;
    }

    private void initWidgets(View view) {
        descEditText = view.findViewById(R.id.descriptionEditText);

        deleteButton = view.findViewById(R.id.deleteNoteButton);
        Button saveButton = view.findViewById(R.id.saveNoteButton);

        FrameLayout fNote1 = view.findViewById(R.id.fNote1);
        FrameLayout fNote2 = view.findViewById(R.id.fNote2);
        FrameLayout fNote3 = view.findViewById(R.id.fNote3);
        FrameLayout fNote4 = view.findViewById(R.id.fNote4);
        FrameLayout fNote5 = view.findViewById(R.id.fNote5);

        imageColorRed = view.findViewById(R.id.red_color_note);
        imageColorYellow = view.findViewById(R.id.yellow_color_note);
        imageColorGreen = view.findViewById(R.id.green_color_note);
        imageColorBlue = view.findViewById(R.id.blue_color_note);
        imageColorPurple = view.findViewById(R.id.purple_color_note);

        fNote1.setOnClickListener(v -> {
            selectedColor = getResources().getColor(R.color.defaultColorRed);
            imageColorRed.setImageResource(R.drawable.baseline_check_24);
            imageColorYellow.setImageResource(0);
            imageColorGreen.setImageResource(0);
            imageColorBlue.setImageResource(0);
            imageColorPurple.setImageResource(0);
        });

        fNote2.setOnClickListener(v -> {
            selectedColor = getResources().getColor(R.color.defaultColorYellow);
            imageColorRed.setImageResource(0);
            imageColorYellow.setImageResource(R.drawable.baseline_check_24);
            imageColorGreen.setImageResource(0);
            imageColorBlue.setImageResource(0);
            imageColorPurple.setImageResource(0);
        });

        fNote3.setOnClickListener(v -> {
            selectedColor = getResources().getColor(R.color.defaultColorGreen);
            imageColorRed.setImageResource(0);
            imageColorYellow.setImageResource(0);
            imageColorGreen.setImageResource(R.drawable.baseline_check_24);
            imageColorBlue.setImageResource(0);
            imageColorPurple.setImageResource(0);
        });

        fNote4.setOnClickListener(v -> {
            selectedColor = getResources().getColor(R.color.defaultColorBlue);
            imageColorBlue.setImageResource(R.drawable.baseline_check_24);
            imageColorRed.setImageResource(0);
            imageColorYellow.setImageResource(0);
            imageColorGreen.setImageResource(0);
            imageColorBlue.setImageResource(R.drawable.baseline_check_24);
            imageColorPurple.setImageResource(0);
        });

        fNote5.setOnClickListener(v -> {
            selectedColor = getResources().getColor(R.color.defaultColorPurple);
            imageColorRed.setImageResource(0);
            imageColorYellow.setImageResource(0);
            imageColorGreen.setImageResource(0);
            imageColorBlue.setImageResource(0);
            imageColorPurple.setImageResource(R.drawable.baseline_check_24);
        });

        // Handle save button click
        saveButton.setOnClickListener(v -> {
            NotesHelper NotesDB = NotesHelper.instanceOfDatabase(getContext());
            String desc = String.valueOf(descEditText.getText());
            if (selectedNote == null) {
                // If no note is selected, create a new note and add it to the list
                int id = Note.noteArrayList.size();
                Note newNote = new Note(id, desc, null, selectedColor);

                Note.noteArrayList.add(newNote);

                // Add the new note to the database
                NotesHelper notesDB = NotesHelper.instanceOfDatabase(getContext());
                NotesDB.addNoteToDatabase(newNote);
            } else {
                // If a note is selected, update its description and color, and imageUris and update it in the database
                selectedNote.setDescription(desc);

                selectedNote.setColor(selectedColor);

                NotesHelper notesDB = NotesHelper.instanceOfDatabase(getContext());
                NotesDB.updateNoteInDB(selectedNote);
            }

            // Get the FragmentManager
            FragmentManager fragmentManager = getParentFragmentManager();

            // Create a new FragmentTransaction
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Replace the current fragment with the NotesFragment
            NotesFragment notesFragment = new NotesFragment();
            transaction.replace(R.id.effixcelFragmentContainer, notesFragment); // Replace "R.id.fragment_container" with the appropriate ID of the container where NotesFragment should be displayed

            // Add the transaction to the back stack, so pressing the back button will return to the previous fragment (if desired)
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();

            // Dismiss the BottomSheetDialogFragment
            dismiss();

        });

        deleteButton.setOnClickListener(v -> {
            // Create an AlertDialog to confirm the deletion
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // Perform the note deletion
                        deleteNote();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
    private void deleteNote() {
        // Set the deleted timestamp of the selected note and update it in the database
        selectedNote.setDeleted(new Date());
        NotesHelper notesDB = NotesHelper.instanceOfDatabase(getContext());
        notesDB.updateNoteInDB(selectedNote);

        // THE WHOLE CODE BELOW ENSURES THAT WHEN I ADD, EDIT, DELETE A NOTE,
        // ITS INSTANTANEOUS SO FRAGMENT TRANSACTION AND DISMISS HAVE TO WORK TOGETHER

        // Get the FragmentManager
        FragmentManager fragmentManager = getParentFragmentManager();

        // Create a new FragmentTransaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the current fragment with the NotesFragment
        NotesFragment notesFragment = new NotesFragment();
        transaction.replace(R.id.effixcelFragmentContainer, notesFragment); // Replace "R.id.fragment_container" with the appropriate ID of the container where NotesFragment should be displayed

        // Add the transaction to the back stack, so pressing the back button will return to the previous fragment (if desired)
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();

        // Dismiss the BottomSheetDialogFragment
        dismiss();
    }

    private void checkForEditNote() {
        Bundle args = getArguments();
        if (args != null) {
            int passedNoteID = args.getInt(Note.NOTE_EDIT_EXTRA, -1);
            selectedNote = Note.getNoteForID(passedNoteID);

            if (selectedNote != null) {
                // If a note is being edited, display its description in the EditText
                descEditText.setText(selectedNote.getDescription());

                selectedColor = selectedNote.getColor(); // Add a getColor method in the Note class to get the color value
            }

            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.INVISIBLE);
        }
    }
}