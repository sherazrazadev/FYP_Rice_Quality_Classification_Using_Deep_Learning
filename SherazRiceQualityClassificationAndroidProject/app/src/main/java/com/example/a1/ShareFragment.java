package com.example.a1;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

public class ShareFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        // Find the "Share Now" button in the layout
        Button shareNowButton = view.findViewById(R.id.btn_share);

        // Set a click listener on the "Share Now" button
        shareNowButton.setOnClickListener(v -> {
            // Replace "your.share.content" with the content you want to share
            String shareContent = "Check out this amazing app!";

            // Create the share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share the App");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);

            // Create a chooser to show available sharing apps
            Intent chooserIntent = Intent.createChooser(shareIntent, "Share via");

            // Check if Facebook app is installed and add to the chooser
            if (isAppInstalled("com.facebook.katana")) {
                shareIntent.setPackage("com.facebook.katana");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{shareIntent});
            }

            // Check if WhatsApp app is installed and add to the chooser
            if (isAppInstalled("com.whatsapp")) {
                shareIntent.setPackage("com.whatsapp");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{shareIntent});
            }

            // Check if LinkedIn app is installed and add to the chooser
            if (isAppInstalled("com.linkedin.android")) {
                shareIntent.setPackage("com.linkedin.android");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{shareIntent});
            }

            // Start the chooser
            startActivity(chooserIntent);
        });

        return view;
    }

    // Helper method to check if an app is installed on the device
    private boolean isAppInstalled(String packageName) {
        PackageManager pm = requireContext().getPackageManager();

// Get the list of installed applications
        List<ApplicationInfo> installedApplications = pm.getInstalledApplications(PackageManager.GET_META_DATA);

// Loop through the list of installed applications
        for (ApplicationInfo appInfo : installedApplications) {

            // Get the app's package name
            String appPackageName = appInfo.packageName;

            // Get the app's label
            String label = appInfo.loadLabel(pm).toString();

            // Print the app's package name and label
            Log.d("PackageVisibility", "Package name: " + appPackageName + ", Label: " + label);
        }

        return false;
    }
}
