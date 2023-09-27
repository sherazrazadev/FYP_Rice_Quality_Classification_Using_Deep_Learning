package com.example.a1;

import static android.content.ContentValues.TAG;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FeedbackFragment extends Fragment {

    private static final String SHARED_PREFS_KEY = "feedback_prefs";
    private static final String FEEDBACK_KEY = "feedback";

    private EditText emailEditText;
    private EditText feedbackEditText;
    private Button submitButton;

    private FirebaseFirestore db;

    public FeedbackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        emailEditText = view.findViewById(R.id.emailEditText); // Find the emailEditText view in the layout
        feedbackEditText = view.findViewById(R.id.feedbackEditText);
        submitButton = view.findViewById(R.id.submitButton);

        db = FirebaseFirestore.getInstance(); // Initialize the db variable

        submitButton.setOnClickListener(v -> submitFeedback());

        return view;
    }

    private void submitFeedback() {
        String userEmail = emailEditText.getText().toString().trim();
        String feedbackMessage = feedbackEditText.getText().toString().trim();

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("email", userEmail);
        user.put("feedback", feedbackMessage);

// Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Feedback submitted " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error feedback submission", e);
                    }
                });


    }
}
