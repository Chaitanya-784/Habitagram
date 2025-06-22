package com.example.habittrackerapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnDashboard, btnTracker, btnCalendar, btnSettings,btnMyFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String username = getIntent().getStringExtra("username");  // Get the username

        // Initialize all buttons
        btnDashboard = findViewById(R.id.btnDashboard);
        btnTracker = findViewById(R.id.btnTracker);
        btnCalendar = findViewById(R.id.btnCalendar);
        btnSettings = findViewById(R.id.btnSettings);
        btnMyFriends = findViewById(R.id.btnMyFriends);


        // Set click listeners
        btnDashboard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        btnTracker.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, HabitTrackerActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });


        btnCalendar.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        findViewById(R.id.btnAddFriends).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddFriendsActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        btnMyFriends.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyFriendsActivity.class);
            intent.putExtra("username", username); // pass logged-in user's username
            startActivity(intent);
        });


        btnSettings.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });
    }
}
