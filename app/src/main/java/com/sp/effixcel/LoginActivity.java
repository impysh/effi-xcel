package com.sp.effixcel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    Button login_button;

    TextView signupRedirectText;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);

        login_button = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginEmail = email.getText().toString().trim();
                String loginPassword = password.getText().toString().trim();

                if (loginEmail.equals("") || loginPassword.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please enter your credentials.", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(loginEmail, loginPassword)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);

                                        SharedPreferences emailPreferences = getSharedPreferences("UserEmail", MODE_PRIVATE);
                                        SharedPreferences.Editor emailEditor = emailPreferences.edit();
                                        emailEditor.putString("email", loginEmail);
                                        emailEditor.apply();

                                        SharedPreferences loginPreferences = getSharedPreferences("UserLogin", MODE_PRIVATE);
                                        SharedPreferences.Editor loginEditor = loginPreferences.edit();
                                        loginEditor.putBoolean("isLoggedIn", true);
                                        loginEditor.apply();

                                        finish(); // Close LoginActivity
                                    } else {
                                        // If sign-in fails, display a message to the user.
                                        Toast.makeText(LoginActivity.this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}