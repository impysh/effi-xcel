package com.sp.effixcel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class SignupActivity extends AppCompatActivity {
    EditText email, password;

    Button signupButton;

    TextView loginRedirectText;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firebaseAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        signupButton = findViewById(R.id.signup_button);

        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String signupEmail = email.getText().toString().trim();
                String signupPassword = password.getText().toString().trim();

                if (signupEmail.equals("") || signupPassword.equals("")) {
                    Toast.makeText(SignupActivity.this, "Fill all the fields.", Toast.LENGTH_SHORT).show();
                } else {
                    // Validate password against requirements
                    int minLength = 8; // Minimum password length
                    boolean requiresUppercase = true; // Requires at least one uppercase character
                    boolean requiresLowercase = true; // Requires at least one lowercase character
                    boolean requiresNumeric = true; // Requires at least one numeric character

                    List<String> errors = getPasswordValidationErrors(signupPassword, minLength, requiresUppercase, requiresLowercase, requiresNumeric);
                    if (!errors.isEmpty()) {
                        // Show the first error as a toast message
                        Toast.makeText(SignupActivity.this, errors.get(0), Toast.LENGTH_LONG).show();
                        return;
                    }

                    firebaseAuth.createUserWithEmailAndPassword(signupEmail, signupPassword)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign up success, update UI with the signed-in user's information
                                        Toast.makeText(SignupActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);

                                        // Move this block inside the if (task.isSuccessful()) block
                                        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                                        if (currentUser != null) {
                                            String userUid = currentUser.getUid();
                                            // Now you have the user's UID, and you can pass it to the EventsHelper when needed.
                                        }
                                    } else {
                                        // If sign-up fails, display a message to the user.
                                        Toast.makeText(SignupActivity.this, "Registration Failed.", Toast.LENGTH_SHORT).show();

                                        // Log the error for debugging purposes
                                        Log.e("FirebaseError", "Registration failed: " + task.getException());
                                    }
                                }
                            });
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
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
