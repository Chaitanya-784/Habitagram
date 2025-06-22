package com.example.habittrackerapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class PendingRequestsActivity extends AppCompatActivity {

    private HabitDatabase db;
    private String myUsername;
    private RecyclerView rvPending;
    private PendingRequestsAdapter adapter;
    private List<FriendEntity> pendingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);

        myUsername = getIntent().getStringExtra("username");
        db = HabitDatabase.getInstance(getApplicationContext());

        rvPending = findViewById(R.id.rvPendingRequests);
        adapter = new PendingRequestsAdapter(pendingList, new PendingRequestsAdapter.OnActionClickListener() {
            @Override
            public void onAccept(FriendEntity friend) {
                updateStatus(friend, "accepted");
            }

            @Override
            public void onReject(FriendEntity friend) {
                updateStatus(friend, "rejected");
            }
        });
        rvPending.setLayoutManager(new LinearLayoutManager(this));
        rvPending.setAdapter(adapter);

        loadPendingRequests();
    }

    private void loadPendingRequests() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<FriendEntity> results = db.friendDao().getPendingRequests(myUsername);
            runOnUiThread(() -> {
                pendingList.clear();
                pendingList.addAll(results);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void updateStatus(FriendEntity friend, String status) {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.friendDao().updateFriendStatus(friend.getId(), status);
            runOnUiThread(() -> {
                Toast.makeText(this, "Request " + status, Toast.LENGTH_SHORT).show();
                loadPendingRequests();
            });
        });
    }
}
