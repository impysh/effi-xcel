package com.sp.effixcel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> implements Filterable
{
    private List<Task> taskList;
    private List<Task> taskListFull; // This will store the full list of tasks for filtering
    private TasksFragment tasksFragment;
    private TasksHelper db;
    private int selectedTaskColor;

    public TaskAdapter(TasksHelper db, TasksFragment tasksFragment, String selectedTaskColor)
    {
        this.db = db;
        this.tasksFragment = tasksFragment;
        this.taskListFull = new ArrayList<>();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        db.openDatabase();
        Task item = taskList.get(position);
        holder.task_name.setText(item.getTaskName());

        if (item.getTaskDescription() != null && !item.getTaskDescription().isEmpty()) {
            holder.task_des.setVisibility(View.VISIBLE);
            holder.task_des.setText(item.getTaskDescription());
        } else {
            holder.task_des.setVisibility(View.GONE);
        }



        holder.task.setChecked(toBoolean(item.getStatus()));

        if (item.getColor() != -1) {
            holder.layout_task.setBackgroundColor(item.getColor());
        } else {
            holder.layout_task.setBackgroundColor(getContext().getResources().getColor(R.color.defaultColorYellow));
        }

        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus(item.getId(), 1);
                } else {
                    db.updateStatus(item.getId(), 0);
                }
            }
        });
    }

    public int getItemCount()
    {
        return taskList.size();
    }

    private boolean toBoolean(int n)
    {
        return n!=0;
    }

    public void setTasks(List<Task> taskList)
    {
        this.taskList = taskList;
        this.taskListFull = new ArrayList<>(taskList); // Update the full list whenever the task list is updated
        notifyDataSetChanged();
    }

    public Context getContext()
    {
        return tasksFragment.getActivity();
    }

    public void deleteItem(int position)
    {
        Task item = taskList.get(position);
        db.deleteTask(item.getId());
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position)
    {
        Task item = taskList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("taskName", item.getTaskName());
        bundle.putString("taskDescription", item.getTaskDescription());
        bundle.putInt("color", item.getColor());
        AddTaskFragment fragment = new AddTaskFragment();
        fragment.setArguments(bundle);
        fragment.setTargetFragment(tasksFragment,0);
        fragment.show(tasksFragment.getActivity().getSupportFragmentManager(), AddTaskFragment.TAG);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView task_name, task_des;
        CardView layout_task;
        CheckBox task;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.checkbox);
            task_name = view.findViewById(R.id.taskName);
            task_des = view.findViewById(R.id.taskDescription);
            layout_task = view.findViewById(R.id.task_card_view);
        }
    }

    @Override
    public Filter getFilter() {
        return taskFilter;
    }

    private Filter taskFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Task> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(taskListFull); // If the search query is empty, show the full list
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Task task : taskListFull) {
                    if (task.getTaskName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(task);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            taskList.clear();
            taskList.addAll((List<Task>) results.values);
            notifyDataSetChanged();
        }
    };
}