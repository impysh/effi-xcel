package com.sp.effixcel;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment; // Add this import

public class ChangePasswordFragment extends BottomSheetDialogFragment {

    EditText password;
    Button updateButton;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        password = view.findViewById(R.id.password_reset);
        updateButton = view.findViewById(R.id.update_button);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String changedPassword = password.getText().toString();

                // Define password requirements
                int minLength = 8; // Minimum password length
                boolean requiresUppercase = true; // Requires at least one uppercase character
                boolean requiresLowercase = true; // Requires at least one lowercase character
                boolean requiresNumeric = true; // Requires at least one numeric character

                // Validate password against requirements
                List<String> errors = getPasswordValidationErrors(changedPassword, minLength, requiresUppercase, requiresLowercase, requiresNumeric);
                if (!errors.isEmpty()) {
                    StringBuilder errorMessage = new StringBuilder();
                    for (String error : errors) {
                        errorMessage.append(error).append("\n");
                    }
                    Toast.makeText(getActivity(), errorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                // Update the password in the database using Firebase Authentication
                firebaseUser.updatePassword(changedPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Password updated successfully
                                Toast.makeText(getActivity(), "Password updated successfully!", Toast.LENGTH_SHORT).show();

                                Intent loginIntent = new Intent(getActivity(), LoginActivity.class); // Redirect to LoginActivity
                                startActivity(loginIntent);

                                dismiss(); // Close the bottom sheet dialog
                            } else {
                                // Failed to update password
                                Toast.makeText(getActivity(), "Failed to update password.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return view;
    }

    private List<String> getPasswordValidationErrors(String password, int minLength, boolean requiresUppercase, boolean requiresLowercase, boolean requiresNumeric) {
        List<String> errors = new ArrayList<>();

        if (password.length() < minLength) {
            errors.add("Password must be at least " + minLength + " characters long.");
        }

        // Check for uppercase character if required
        if (requiresUppercase && !password.matches(".*[A-Z].*")) {
            errors.add("Password must contain at least one uppercase letter.");
        }

        // Check for lowercase character if required
        if (requiresLowercase && !password.matches(".*[a-z].*")) {
            errors.add("Password must contain at least one lowercase letter.");
        }

        // Check for numeric character if required
        if (requiresNumeric && !password.matches(".*\\d.*")) {
            errors.add("Password must contain at least one numeric digit.");
        }

        // Check for special characters
        if (password.matches(".*[^a-zA-Z0-9].*")) {
            errors.add("Special characters are not allowed in the password.");
        }

        return errors;
    }
}