package com.example.habittrackerapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executors;

public class SettingsActivity extends AppCompatActivity {

    private HabitDatabase habitDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        habitDatabase = HabitDatabase.getInstance(this);

        // Dark Mode Toggle
        Switch switchDarkMode = findViewById(R.id.switchDarkMode);
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        switchDarkMode.setChecked(prefs.getBoolean("dark_mode", false));
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", isChecked).apply();

            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // Notifications Toggle (placeholder)
        Switch switchNotifications = findViewById(R.id.switchNotifications);
        switchNotifications.setChecked(prefs.getBoolean("notifications_enabled", false));
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "Notifications Enabled" : "Notifications Disabled", Toast.LENGTH_SHORT).show();
        });

        // Feedback / About Button
        Button btnFeedback = findViewById(R.id.btnFeedback);
        btnFeedback.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("About App")
                    .setMessage("HabitTrackerApp v1.0\n\nBuilt with ❤️ using Android Studio.\nFeedback: habittracker@example.com")
                    .setPositiveButton("OK", null)
                    .show();
        });

        // Reset Habits Button
        Button btnResetHabits = findViewById(R.id.btnResetHabits);
        btnResetHabits.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Reset All Habits?")
                    .setMessage("This will delete all your habits and XP. Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            habitDatabase.habitDao().deleteAllHabits();
                            runOnUiThread(() -> {
                                Toast.makeText(this, "All habits and XP reset!", Toast.LENGTH_SHORT).show();
                            });
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
}
