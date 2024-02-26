package com.sp.effixcel;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


public class AddTaskFragment extends BottomSheetDialogFragment
{
    public static final String TAG = "ActionBottomDialog";
    private Button save;
    private Button delete;
    private EditText task_name;
    private EditText task_description;
    private int defaultTaskColor;
    private int selectedTaskColor = -1;
    private View viewSubtitleIndicator;
    private FrameLayout red;
    private FrameLayout yellow;
    private FrameLayout green;
    private FrameLayout blue;
    private FrameLayout purple;
    private TasksHelper db;
    public static AddTaskFragment newInstance(Bundle args)
    {
        return new AddTaskFragment();
    }

    public AddTaskFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        defaultTaskColor  = getResources().getColor(R.color.defaultColorYellow);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        task_name = getView().findViewById(R.id.task_name);
        task_description = getView().findViewById(R.id.task_description);

        save = getView().findViewById(R.id.save_task);

        red = getView().findViewById(R.id.fTask1);
        yellow = getView().findViewById(R.id.fTask2);
        green = getView().findViewById(R.id.fTask3);
        blue = getView().findViewById(R.id.fTask4);
        purple = getView().findViewById(R.id.fTask5);
        viewSubtitleIndicator = view.findViewById(R.id.viewSubtitleIndicator);


        db = new TasksHelper(getActivity());
        db.openDatabase();

        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null)
        {
            isUpdate = true;
            String taskName = bundle.getString("taskName");
            String taskDescription = bundle.getString("taskDescription");
            int taskColor = bundle.getInt("color");

            task_name.setText(taskName);
            task_description.setText(taskDescription);

            if(taskName.length() > 0)
                save.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

            selectedTaskColor = taskColor;
            setSubtitleIndicatorColor();
        }

        task_name.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if(s.toString().equals(""))
                {
                    save.setEnabled(false);
                    save.setTextColor(Color.WHITE);
                } else
                {
                    save.setEnabled(true);
                    save.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Task Color Picker

        final ImageView imageColorRed = view.findViewById(R.id.red_color_task);
        final ImageView imageColorYellow = view.findViewById(R.id.yellow_color_task);
        final ImageView imageColorGreen = view.findViewById(R.id.green_color_task);
        final ImageView imageColorBlue = view.findViewById(R.id.blue_color_task);
        final ImageView imageColorPurple = view.findViewById(R.id.purple_color_task);

        red.findViewById(R.id.fTask1).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectedTaskColor = getResources().getColor(R.color.defaultColorRed);
                imageColorRed.setImageResource(R.drawable.baseline_check_24);
                imageColorYellow.setImageResource(0);
                imageColorGreen.setImageResource(0);
                imageColorBlue.setImageResource(0);
                imageColorPurple.setImageResource(0);
                setSubtitleIndicatorColor();


            }
        });

        yellow.findViewById(R.id.fTask2).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectedTaskColor = getResources().getColor(R.color.defaultColorYellow);
                imageColorRed.setImageResource(0);
                imageColorYellow.setImageResource(R.drawable.baseline_check_24);
                imageColorGreen.setImageResource(0);
                imageColorBlue.setImageResource(0);
                imageColorPurple.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        green.findViewById(R.id.fTask3).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectedTaskColor = getResources().getColor(R.color.defaultColorGreen);
                imageColorRed.setImageResource(0);
                imageColorYellow.setImageResource(0);
                imageColorGreen.setImageResource(R.drawable.baseline_check_24);
                imageColorBlue.setImageResource(0);
                imageColorPurple.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        blue.findViewById(R.id.fTask4).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectedTaskColor = getResources().getColor(R.color.defaultColorBlue);
                imageColorRed.setImageResource(0);
                imageColorYellow.setImageResource(0);
                imageColorGreen.setImageResource(0);
                imageColorBlue.setImageResource(R.drawable.baseline_check_24);
                imageColorPurple.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        purple.findViewById(R.id.fTask5).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                selectedTaskColor = getResources().getColor(R.color.defaultColorPurple);
                imageColorRed.setImageResource(0);
                imageColorYellow.setImageResource(0);
                imageColorGreen.setImageResource(0);
                imageColorBlue.setImageResource(0);
                imageColorPurple.setImageResource(R.drawable.baseline_check_24);
                setSubtitleIndicatorColor();
            }
        });

        boolean finalIsUpdate = isUpdate;
        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String nameStr = task_name.getText().toString();
                String desStr = task_description.getText().toString();


                // Get the selected color from the AddTaskFragment class
                int color = selectedTaskColor;

                if (color == defaultTaskColor)
                {
                    color = -1;
                }

                if(finalIsUpdate)
                {
                    db.updateTask(bundle.getInt("id"), nameStr, desStr, color);
                }
                else
                {
                    Task newTask = new Task();
                    newTask.setTaskName(nameStr);
                    newTask.setTaskDescription(desStr);
                    newTask.setColor(color);
                    db.insertTask(nameStr, desStr, color);
                }


                // Get the parent activity's fragment manager
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

// Create a new FragmentTransaction
                FragmentTransaction transaction = fragmentManager.beginTransaction();

// Replace the current fragment with the TasksFragment
                TasksFragment tasksFragment = new TasksFragment();
                transaction.replace(R.id.effixcelFragmentContainer, tasksFragment );

// Add the transaction to the back stack, if needed
                transaction.addToBackStack(null);

// Commit the transaction
                transaction.commit();

// Dismiss the BottomSheetDialogFragment
                dismiss();
                selectedTaskColor = defaultTaskColor;
                setSubtitleIndicatorColor();

            }
        });


    }

    private void setSubtitleIndicatorColor()
    {
        if (selectedTaskColor == -1)
        {
            selectedTaskColor = defaultTaskColor;
        }
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(selectedTaskColor);
    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener)
        {
            ((DialogCloseListener)activity).handleDialogClose(dialog);
        }
    }
}
