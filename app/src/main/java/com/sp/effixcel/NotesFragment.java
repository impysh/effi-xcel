package com.sp.effixcel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NotesFragment extends Fragment {
    private ListView noteListView;
    private View rootView;
    private SearchView noteSearchBar;
    private Button newNoteButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notes, container, false);
        initWidgets(rootView);
        loadFromDBToMemory();
        setNoteAdapter();
        setOnClickListener();
        setButtonClickListener();
        return rootView;
    }

    private void initWidgets(View rootView) {
        noteListView = rootView.findViewById(R.id.noteListView);
        newNoteButton = rootView.findViewById(R.id.newNoteButton);
        noteSearchBar = rootView.findViewById(R.id.noteSearchBar);
    }

    private void loadFromDBToMemory() {
        NotesHelper notesDB = NotesHelper.instanceOfDatabase(requireContext());
        notesDB.populateNoteListArray(); // Use the updated method to populate the notes list
    }

    private void setNoteAdapter() {
        NoteAdapter noteAdapter = new NoteAdapter(requireContext(), Note.nonDeletedNotes());
        noteListView.setAdapter(noteAdapter);
    }

    private void setOnClickListener() {
        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Note selectedNote = (Note) noteListView.getItemAtPosition(position);

                AddNoteFragment addNoteFragment = new AddNoteFragment();
                Bundle args = new Bundle();
                args.putInt(Note.NOTE_EDIT_EXTRA, selectedNote.getId());
                addNoteFragment.setArguments(args);
                addNoteFragment.show(getParentFragmentManager(), "AddNoteFragment");
            }
        });
    }

    private void setButtonClickListener() {
        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNoteFragment addNoteFragment = new AddNoteFragment();
                Bundle args = new Bundle();
                // You can omit the selected note ID here since it's a new note
                addNoteFragment.setArguments(args);
                addNoteFragment.show(getParentFragmentManager(), "AddNoteFragment");
            }
        });
    }

    private void filterNotes(String query) {
        NoteAdapter noteAdapter = (NoteAdapter) noteListView.getAdapter();
        if (noteAdapter != null) {
            noteAdapter.getFilter().filter(query);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        noteSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle the search query when the user submits
                filterNotes(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle the search query as the user types
                filterNotes(newText);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setNoteAdapter();
    }
}