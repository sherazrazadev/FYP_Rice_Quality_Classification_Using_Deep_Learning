package com.example.a1;

import static android.content.ContentValues.TAG;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, firstNameEditText, lastNameEditText;
    private DatabaseReference databaseReference;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        Button signupButton = findViewById(R.id.signup_button);
        TextView loginRedirectText = findViewById(R.id.loginRedirectText);
        firstNameEditText = findViewById(R.id.signup_first_name);
        lastNameEditText = findViewById(R.id.signup_last_name);
        db = FirebaseFirestore.getInstance();


        signupButton.setOnClickListener(view -> {
            String email = signupEmail.getText().toString().trim();
            String password = signupPassword.getText().toString().trim();
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();

            if (email.isEmpty()) {
                signupEmail.setError("Email is required");
                return;
            }

            if (password.isEmpty()) {
                signupPassword.setError("Password is required");
                return;
            }

            if (firstName.isEmpty()) {
                firstNameEditText.setError("First Name is required");
                return;
            }

            if (lastName.isEmpty()) {
                lastNameEditText.setError("Last Name is required");
                return;
            }
            Map<String, Object> user = new HashMap<>();
            user.put("firstName", firstName);
            user.put("lastName", lastName);
            user.put("email", email);
            user.put("password", password);

            // Retrieve the user's information from Firestore and set it in the navigation drawer header
            db.collection("users")
                    .add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "Stored successfully: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error -", e);
                        }
                    });

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Set the user's display name
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(firstName + " " + lastName) // Concatenate the first name and last name
                                        .build();

                                firebaseUser.updateProfile(profileUpdates)
                                        .addOnCompleteListener(updateProfileTask -> {
                                            if (updateProfileTask.isSuccessful()) {
                                                Log.d(TAG, "User profile updated.");

                                                // Send verification email
                                                firebaseUser.sendEmailVerification()
                                                        .addOnCompleteListener(emailVerificationTask -> {
                                                            if (emailVerificationTask.isSuccessful()) {
                                                                // Verification email sent successfully
                                                                Toast.makeText(SignUpActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                // Email not sent, handle failure
                                                                Toast.makeText(SignUpActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                                            }

                                                            // Now set the name and email in the navigation drawer header
                                                            NavigationView navigationView = findViewById(R.id.navigationView);
                                                            View headerView = navigationView.getHeaderView(0);
                                                            TextView userEmailTextView = headerView.findViewById(R.id.userEmailTextView);
                                                            userEmailTextView.setText(email);

                                                            TextView userNameTextView = headerView.findViewById(R.id.userNameTextView);
                                                            userNameTextView.setText(firstName + " " + lastName);

                                                            Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                            finish();
                                                        });
                                            } else {
                                                Log.w(TAG, "Failed to update user profile.", updateProfileTask.getException());
                                            }
                                        });
                            }

                        } else {
                            Toast.makeText(SignUpActivity.this, "Sign up failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        loginRedirectText.setOnClickListener(view -> startActivity(new Intent(SignUpActivity.this, LoginActivity.class)));
    }
}
