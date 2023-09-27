package com.example.a1;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class LogoutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        Button logoutBtn = view.findViewById(R.id.logoutbtn);

        // Set an OnClickListener for the logout button
        logoutBtn.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
        });

        return view;
    }

}
