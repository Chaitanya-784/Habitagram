package com.example.habittrackerapp;

import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView tvCompletedHabits;
    private TextView tvHabitsForDate;
    private RecyclerView rvHabitsForDate;
    private HabitAdapter habitAdapter;

    private HabitDatabase db;
    private String username;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        username = getIntent().getStringExtra("username");

        calendarView = findViewById(R.id.nativeCalendarView);
        tvCompletedHabits = findViewById(R.id.tvCompletedHabits);
        tvHabitsForDate = findViewById(R.id.tvHabitsForDate);
        rvHabitsForDate = findViewById(R.id.rvHabitsForDate);

        rvHabitsForDate.setLayoutManager(new LinearLayoutManager(this));
        habitAdapter = new HabitAdapter(
                new java.util.ArrayList<>(),
                (habit, isChecked) -> {}, // No action needed in calendar view
                habit -> {}               // No action for long click in calendar view
        );

        rvHabitsForDate.setAdapter(habitAdapter);

        db = HabitDatabase.getInstance(this);

        // Load today's habits by default
        selectedDate = formatDate(calendarView.getDate());
        loadCompletedHabits(selectedDate);
        loadHabitsForDate(selectedDate);

        // ✅ This is in the perfect place already
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, month + 1, year);
            loadCompletedHabits(selectedDate);  // ✅ Load completed habits for new date
            loadHabitsForDate(selectedDate);    // ✅ Load all habits for that date
        });
    }


    private void loadCompletedHabits(String date) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<String> completedHabitNames = db.habitCompletionDao().getHabitNamesByDate(username, date);

            runOnUiThread(() -> {
                if (completedHabitNames.isEmpty()) {
                    // Removed the line that shows "No habits completed on this day."
                    tvCompletedHabits.setText(""); // Or you can just hide the view if you prefer
                    // tvCompletedHabits.setVisibility(View.GONE);
                } else {
                    StringBuilder sb = new StringBuilder("✅ Habits completed on " + date + ":\n");
                    for (String habit : completedHabitNames) {
                        sb.append("• ").append(habit).append("\n");
                    }
                    tvCompletedHabits.setText(sb.toString());
                }
            });
        });
    }


    private void loadHabitsForDate(String date) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<HabitEntity> habits = db.habitDao().getHabitsByDateAndUser(date, username);

            runOnUiThread(() -> {
                if (habits.isEmpty()) {
                    tvHabitsForDate.setText("No habits for " + date);
                } else {
                    tvHabitsForDate.setText("📌 Habits for " + date);
                }
                habitAdapter.updateList(habits); // Always update adapter, even if empty
            });
        });
    }


    private String formatDate(long millis) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new java.util.Date(millis));
    }
}
