package com.example.a1;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RateUsFragment extends Fragment {

    public RateUsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate_us, container, false);

        // Find the "Rate Now" button in the layout
        Button rateNowButton = view.findViewById(R.id.btn_rate_now);

        // Set a click listener on the "Rate Now" button
        rateNowButton.setOnClickListener(v -> {
            // Replace "your.package.name" with your app's package name in the URL
            String appPackageName = "com.example.a1";
            try {
                // Open the Play Store link for rating
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (ActivityNotFoundException e) {
                // If Play Store is not installed, open the link in the browser
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });

        return view;
    }
}