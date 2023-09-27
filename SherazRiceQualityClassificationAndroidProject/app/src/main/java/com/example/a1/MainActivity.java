package com.example.a1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        FirebaseApp.initializeApp(this);

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setTitle("Rice Classify");// Set your menu icon

        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Toggle button for opening/closing the side navigation drawer
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Set the initial fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();


        if (user != null) {
            NavigationView navigationView = findViewById(R.id.navigationView);
            View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
            TextView userEmailTextView = headerView.findViewById(R.id.userEmailTextView);
            userEmailTextView.setText(user.getEmail());

            TextView userNameTextView = headerView.findViewById(R.id.userNameTextView);
            userNameTextView.setText(user.getDisplayName());

            ImageView profileImageView = headerView.findViewById(R.id.profileImageView);
            String profileImageUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
            if (profileImageUrl != null) {
                Glide.with(this)
                        .load(profileImageUrl)
                        .circleCrop()
                        .into(profileImageView);
            } else {
                Glide.with(this)
                        .load(R.drawable.rice_logo)
                        .circleCrop()
                        .into(profileImageView);
            }
        }
    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle side navigation drawer item selections
        int itemId = item.getItemId();

        Fragment selectedFragment = null;
        if (itemId == R.id.navigation_home) {
            selectedFragment = new HomeFragment();
        } else if (itemId == R.id.navigation_settings) {
            selectedFragment = new LogoutFragment();
        } else if (itemId == R.id.nav_rate_us) {
            selectedFragment = new RateUsFragment();
        } else if (itemId == R.id.nav_share_us) {
            selectedFragment = new ShareFragment();
        }  else if (itemId == R.id.nav_feedback) {
            selectedFragment = new FeedbackFragment();
        } else if (itemId == R.id.nav_help) {
            selectedFragment = new HelpFragment();
        } else if (itemId == R.id.nav_privacy_policy) {
            selectedFragment = new PrivacyPolicyFragment();
        }

        if (selectedFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, selectedFragment).commit();
        }

        // Close the side navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        // Close the side navigation drawer on back button press if it's open
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
