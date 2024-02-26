package com.sp.effixcel;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {

    TextView email;
    Button changepassButton;
    Button deleteaccButton;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        email = view.findViewById(R.id.current_email);
        changepassButton = view.findViewById(R.id.changepassword_button);
        deleteaccButton = view.findViewById(R.id.deleteaccount_button);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            // Update the email field with the user's email address
            email.setText(firebaseUser.getEmail());
        }

        changepassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the ChangePasswordFragment as a bottom sheet dialog
                ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
                changePasswordFragment.show(getParentFragmentManager(), changePasswordFragment.getTag());
            }
        });


        deleteaccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show the confirmation dialog before proceeding with the delete operation
                showDeleteConfirmationDialog();
            }
        });

        return view;
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you want to delete your account?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Delete," perform the delete operation
                deleteAccount();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Cancel," do nothing
            }
        });
        builder.create().show();
    }

    private void deleteAccount() {
        firebaseUser.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Account deleted successfully
                        // Redirect to LoginActivity
                        Toast.makeText(getActivity(), "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(loginIntent);
                        getActivity().finish(); // Finish the current activity (AccountFragment)
                    } else {
                        // Failed to delete account
                        Toast.makeText(getActivity(), "Failed to delete account.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
