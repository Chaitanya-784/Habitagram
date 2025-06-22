package com.example.habittrackerapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.annotation.NonNull;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.Calendar;
import android.app.DatePickerDialog;

public class HabitTrackerActivity extends AppCompatActivity {

    private TextView tvStreak;
    private ProgressBar streakProgressBar;

    private List<HabitEntity> habitList = new ArrayList<>();
    private HabitDatabase habitDatabase;
    private HabitAdapter habitAdapter;
    private String username;
    private int streak = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_tracker);

        username = getIntent().getStringExtra("username");

        tvStreak = findViewById(R.id.tvStreak);
        streakProgressBar = findViewById(R.id.streakProgressBar);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewHabits);
        Button btnAddHabit = findViewById(R.id.btnAddHabit);
        Button btnSettings = findViewById(R.id.btnSettings);
        Button btnCalendar = findViewById(R.id.btnCalendar);

        habitDatabase = HabitDatabase.getInstance(this);

        habitAdapter = new HabitAdapter(habitList, (habit, isChecked) -> {
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            Executors.newSingleThreadExecutor().execute(() -> {
                if (isChecked) {
                    HabitCompletionEntity existing = habitDatabase.habitCompletionDao().getCompletion(habit.getId(), username, today);
                    if (existing == null) {
                        habit.setCompleted(true);
                        habitDatabase.habitDao().updateHabit(habit);
                        habitDatabase.habitCompletionDao().insertCompletion(new HabitCompletionEntity(habit.getId(), username, today));
                    }
                } else {
                    habit.setCompleted(false);
                    habitDatabase.habitDao().updateHabit(habit);
                    habitDatabase.habitCompletionDao().deleteCompletion(habit.getId(), username, today);
                }
                updateStreakUI();
            });
        }, this::showEditHabitDialog);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(habitAdapter);

        btnAddHabit.setOnClickListener(v -> showAddHabitDialog());
        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        btnCalendar.setOnClickListener(v -> {
            Intent intent = new Intent(this, CalendarActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) { return false; }
            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                HabitEntity habit = habitList.get(position);
                habitList.remove(position);
                habitAdapter.notifyItemRemoved(position);
                Executors.newSingleThreadExecutor().execute(() -> {
                    habitDatabase.habitDao().deleteHabit(habit);
                    updateStreakUI();
                });
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        habitDatabase.habitDao().getHabitsByUserLive(username).observe(this, habits -> {
            habitList.clear();
            habitList.addAll(habits);
            habitAdapter.notifyDataSetChanged();
            updateStreakUI();
        });
    }

    private void updateStreakUI() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Set<String> completedDates = new HashSet<>();
            List<HabitEntity> completedHabits = habitDatabase.habitDao().getCompletedHabitsByUser(username);
            for (HabitEntity h : completedHabits) {
                completedDates.add(h.getDate());
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar today = Calendar.getInstance();
            int localStreak = 0;

            while (true) {
                String date = sdf.format(today.getTime());
                if (completedDates.contains(date)) {
                    localStreak++;
                    today.add(Calendar.DATE, -1);
                } else break;
            }

            int finalStreak = localStreak;
            runOnUiThread(() -> {
                streak = finalStreak;
                tvStreak.setText("🔥 Streak: " + streak + " days");
                streakProgressBar.setProgress(streak);
            });
        });
    }

    private void showAddHabitDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_habit, null);
        EditText etName = dialogView.findViewById(R.id.etHabitName);
        EditText etTime = dialogView.findViewById(R.id.etHabitTime);
        TextView tvSelectDate = dialogView.findViewById(R.id.tvSelectDate);

        final String[] habitDate = {""};
        final String[] habitTime = {""};

        etTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            new android.app.TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                etTime.setText(selectedTime);
                habitTime[0] = selectedTime;
            }, hour, minute, true).show();
        });

        tvSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, month1, dayOfMonth) -> {
                        String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, month1 + 1, year1);
                        tvSelectDate.setText(selectedDate);
                        habitDate[0] = selectedDate;
                    }, year, month, day);
            datePickerDialog.show();
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("➕ Add New Habit")
                .setView(dialogView)
                .setPositiveButton("Add", (dialogInterface, i) -> {
                    String name = etName.getText().toString();

                    if (!name.isEmpty() && !habitTime[0].isEmpty() && !habitDate[0].isEmpty()) {
                        HabitEntity newHabit = new HabitEntity(name, habitTime[0], 0, false, username, habitDate[0]);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            habitDatabase.habitDao().insertHabit(newHabit);
                        });
                    } else {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private void showEditHabitDialog(HabitEntity habit) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_habit, null);
        EditText etName = dialogView.findViewById(R.id.etHabitName);
        EditText etTime = dialogView.findViewById(R.id.etHabitTime);
        EditText etXP = dialogView.findViewById(R.id.etHabitXP);
        TextView tvSelectDate = dialogView.findViewById(R.id.tvSelectDate);

        etName.setText(habit.getName());
        etTime.setText(habit.getTime());
        etXP.setText(String.valueOf(habit.getXp()));
        tvSelectDate.setText(habit.getDate());

        final String[] updatedTime = {habit.getTime()};
        final String[] updatedDate = {habit.getDate()};

        etTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            new android.app.TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute1);
                etTime.setText(selectedTime);
                updatedTime[0] = selectedTime;
            }, hour, minute, true).show();
        });

        tvSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year1, month1, dayOfMonth) -> {
                        String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, month1 + 1, year1);
                        tvSelectDate.setText(selectedDate);
                        updatedDate[0] = selectedDate;
                    }, year, month, day);
            datePickerDialog.show();
        });

        new AlertDialog.Builder(this)
                .setTitle("✏️ Edit Habit")
                .setView(dialogView)
                .setPositiveButton("Update", (dialogInterface, i) -> {
                    String name = etName.getText().toString();
                    String xpStr = etXP.getText().toString();

                    if (!name.isEmpty() && !updatedTime[0].isEmpty() && !xpStr.isEmpty()) {
                        try {
                            int xp = Integer.parseInt(xpStr);
                            habit.setName(name);
                            habit.setTime(updatedTime[0]);
                            habit.setXp(xp);
                            habit.setDate(updatedDate[0]);

                            Executors.newSingleThreadExecutor().execute(() -> {
                                habitDatabase.habitDao().updateHabit(habit);
                            });
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "XP must be a number", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
