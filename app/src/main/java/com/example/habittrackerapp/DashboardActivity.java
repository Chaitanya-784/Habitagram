package com.example.habittrackerapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvGreeting, tvHabitCount, tvCompleted, tvProfileUsername, tvFriendsCount, tvStreak;
    private CircleImageView imgProfile;
    private RecyclerView rvGroupedHabits;
    private DashboardAdapter dashboardAdapter;
    private HabitDatabase db;
    private String username;
    private ProgressBar streakProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        db = HabitDatabase.getInstance(this);

        tvGreeting = findViewById(R.id.tvGreeting);
        tvHabitCount = findViewById(R.id.tvHabitCount);
        tvCompleted = findViewById(R.id.tvCompletedHabits);
        rvGroupedHabits = findViewById(R.id.rvGroupedHabits);
        tvProfileUsername = findViewById(R.id.tvProfileUsername);
        tvFriendsCount = findViewById(R.id.tvFriendsCount);
        tvStreak = findViewById(R.id.tvStreak);
        imgProfile = findViewById(R.id.imgProfile);
        streakProgressBar = findViewById(R.id.streakProgressBar);

        username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            username = "User";
        }

        tvGreeting.setText("👋 Hi, " + username + "!");
        tvProfileUsername.setText("Username: " + username);

        rvGroupedHabits.setLayoutManager(new LinearLayoutManager(this));
        dashboardAdapter = new DashboardAdapter(new ArrayList<>());
        rvGroupedHabits.setAdapter(dashboardAdapter);

        loadUserStats();
        loadGroupedHabitsByDate();
    }

    private void loadUserStats() {
        Executors.newSingleThreadExecutor().execute(() -> {
            UserEntity user = db.userDao().getUserByUsername(username);
            List<HabitEntity> habits = db.habitDao().getHabitsByUser(username);
            List<HabitEntity> completedHabits = db.habitDao().getCompletedHabitsByUser(username);

            int habitCount = habits.size();
            int completedCount = completedHabits.size();

            Set<String> completedDates = new HashSet<>();
            for (HabitEntity habit : completedHabits) {
                completedDates.add(habit.getDate());
            }

            int streak = calculateStreak(completedDates);
            List<FriendEntity> friendsCount = db.friendDao().getAcceptedFriends(username);

            runOnUiThread(() -> {
                tvHabitCount.setText("📌 Total Habits: " + habitCount);
                tvCompleted.setText("✅ Completed: " + completedCount);
                tvStreak.setText("Streak: " + streak + " 🔥");
                streakProgressBar.setProgress(streak);
                tvFriendsCount.setText("Friends: " + friendsCount);
                imgProfile.setImageResource(R.drawable.ic_user);
            });
        });
    }

    private void loadGroupedHabitsByDate() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<String> dates = db.habitDao().getAllDatesForUser(username);
            List<HabitsByDate> grouped = new ArrayList<>();

            for (String date : dates) {
                List<HabitEntity> habits = db.habitDao().getHabitsByUserAndDate(username, date);
                if (!habits.isEmpty()) {
                    grouped.add(new HabitsByDate(date, habits));
                }
            }

            runOnUiThread(() -> {
                TextView tvNoHabitsMessage = findViewById(R.id.tvNoHabitsMessage);
                if (grouped.isEmpty()) {
                    tvNoHabitsMessage.setVisibility(View.VISIBLE);
                    rvGroupedHabits.setVisibility(View.GONE);
                } else {
                    tvNoHabitsMessage.setVisibility(View.GONE);
                    rvGroupedHabits.setVisibility(View.VISIBLE);
                    dashboardAdapter.updateList(grouped);
                }
            });
        });
    }

    private int calculateStreak(Set<String> completedDates) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar today = Calendar.getInstance();
        int streak = 0;

        while (true) {
            String currentDate = sdf.format(today.getTime());
            if (completedDates.contains(currentDate)) {
                streak++;
                today.add(Calendar.DATE, -1);
            } else {
                break;
            }
        }
        return streak;
    }
}
