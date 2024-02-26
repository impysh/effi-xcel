package com.sp.effixcel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends ArrayAdapter<Note> {
    private List<Note> originalNotesList;
    private List<Note> filteredNotesList;

    public NoteAdapter(Context context, List<Note> notes) {
        super(context, 0, notes);
        this.originalNotesList = new ArrayList<>(notes);
        this.filteredNotesList = new ArrayList<>(notes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Note note = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_note_layout, parent, false);

        LinearLayout cell = convertView.findViewById(R.id.cellLayout);

        // Set the description text to the TextView
        TextView desc = convertView.findViewById(R.id.cellDesc);
        desc.setText(getItem(position).getDescription());
        
        // Set the background color for the cellDesc TextView based on the note's color
        if (note.getColor() != -1) {
            cell.setBackgroundColor(note.getColor());
        } else {
            // Set a default background color if no color is set for the note
            cell.setBackgroundColor(getContext().getResources().getColor(R.color.defaultColorYellow));
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return filteredNotesList.size();
    }

    @Override
    public Note getItem(int position) {
        return filteredNotesList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return noteFilter;
    }

    private Filter noteFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Note> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                // If the search query is empty, show the original list
                filteredList.addAll(originalNotesList);
            } else {
                // Filter the notes based on the search query
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Note note : originalNotesList) {
                    if (note.getDescription().toLowerCase().contains(filterPattern)) {
                        filteredList.add(note);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredNotesList.clear();
            filteredNotesList.addAll((List<Note>) results.values);
            notifyDataSetChanged();
        }
    };
}
