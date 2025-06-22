package com.example.habittrackerapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import android.widget.ImageButton;


public class AddFriendsActivity extends AppCompatActivity {

    private EditText etSearchUser;
    private ImageButton btnSearch;

    private RecyclerView rvUserResults;
    private UserSearchAdapter adapter;
    private HabitDatabase db;
    private String myUsername;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        db = HabitDatabase.getInstance(getApplicationContext());
        myUsername = getIntent().getStringExtra("username");

        etSearchUser = findViewById(R.id.etSearchUser);
        btnSearch = findViewById(R.id.btnSearch);
        rvUserResults = findViewById(R.id.rvUserResults);
        adapter = new UserSearchAdapter(new ArrayList<>(), this::addFriend);

        rvUserResults.setLayoutManager(new LinearLayoutManager(this));
        rvUserResults.setAdapter(adapter);

        // 🔍 Button-based search
        btnSearch.setOnClickListener(v -> {
            String query = etSearchUser.getText().toString().trim();
            if (!query.isEmpty()) {
                searchUsers(query);
            } else {
                Toast.makeText(this, "Enter username to search", Toast.LENGTH_SHORT).show();
            }
        });

        // ⌨️ Live search with TextWatcher
        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    searchUsers(query);
                }
            }
        });
    }

    private void searchUsers(String query) {
        Log.d("SearchDebug", "Searching for: " + query);
        Executors.newSingleThreadExecutor().execute(() -> {
            List<UserEntity> results = db.userDao().searchUsersByName(query, myUsername);
            Log.d("SearchDebug", "Results size: " + results.size());
            for (UserEntity user : results) {
                Log.d("SearchDebug", "Found user: " + user.getUsername());
            }
            runOnUiThread(() -> adapter.updateList(results));
        });
    }

    private void addFriend(UserEntity friend) {
        Executors.newSingleThreadExecutor().execute(() -> {
            FriendEntity newFriend = new FriendEntity(myUsername, friend.getUsername(), "pending");
            db.friendDao().insertFriend(newFriend);
            runOnUiThread(() ->
                    Toast.makeText(this, "Friend request sent to " + friend.getUsername(), Toast.LENGTH_SHORT).show()
            );
        });
    }
}
