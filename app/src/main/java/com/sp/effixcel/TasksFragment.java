package com.sp.effixcel;

import static com.sp.effixcel.AddTaskFragment.TAG;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TasksFragment extends Fragment implements DialogCloseListener, SearchView.OnQueryTextListener
{
    private Button new_task;
    private SearchView taskSearchBar;
    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private TasksHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        taskSearchBar = view.findViewById(R.id.taskSearchBar);
        taskSearchBar.setOnQueryTextListener(this);

        new_task = view.findViewById(R.id.new_task);
        new_task.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AddTaskFragment addTaskFragmentFragment = new AddTaskFragment();
                addTaskFragmentFragment.show(getParentFragmentManager(), TAG);
            }
        });

        db = new TasksHelper(getContext());
        db.openDatabase();

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(db, this, "#606570");

        tasksRecyclerView = view.findViewById(R.id.task_list);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tasksRecyclerView.setAdapter(taskAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(taskAdapter));
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView);

        taskList = db.getAll();
        Collections.reverse(taskList);
        taskAdapter.setTasks(taskList);

        return view;
    }

    @Override
    public void handleDialogClose(DialogInterface dialog)
    {
        taskList = db.getAll();
        Collections.reverse(taskList);
        taskAdapter.setTasks(taskList);
        taskAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        taskAdapter.getFilter().filter(newText);
        return true;
    }
}