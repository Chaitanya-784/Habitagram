package com.example.habittrackerapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class MyFriendsActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 101;
    private RecyclerView rvFriends;
    private Button btnCapture;

    private ImageView ivFriendPhoto;
    private FriendAdapter adapter;
    private HabitDatabase db;
    private String myUsername;
    private List<FriendEntity> myFriends = new ArrayList<>();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);

        myUsername = getIntent().getStringExtra("username");
        db = HabitDatabase.getInstance(getApplicationContext());

        rvFriends = findViewById(R.id.rvMyFriends);
        btnCapture = findViewById(R.id.btnCaptureHabit);

        ivFriendPhoto = findViewById(R.id.ivFriendPhoto);

        adapter = new FriendAdapter(myFriends);
        rvFriends.setLayoutManager(new LinearLayoutManager(this));
        rvFriends.setAdapter(adapter);

        loadFriends();

        btnCapture.setOnClickListener(v -> {
            if (adapter.getSelectedFriends().isEmpty()) {
                Toast.makeText(this, "Select at least one friend", Toast.LENGTH_SHORT).show();
            } else {
                launchCamera();
            }
        });
    }

    private void loadFriends() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<FriendEntity> results = db.friendDao().getFriendsByOwner(myUsername);
            runOnUiThread(() -> {
                myFriends.clear();
                myFriends.addAll(results);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void launchCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQUEST_CAMERA);
            } else {
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK && data != null) {
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            sendImageToSelectedFriends(bmp);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera(); // Retry opening the camera
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendImageToSelectedFriends(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        byte[] image = stream.toByteArray();

        for (FriendEntity f : adapter.getSelectedFriends()) {
            // Only upload if the friend has accepted the request
            if ("accepted".equals(f.getStatus())) {
                // Create a reference to Firebase Storage
                StorageReference imageRef = storageRef.child("images/" + myUsername + "_" + f.getFriendUsername() + ".jpg");

                UploadTask uploadTask = imageRef.putBytes(image);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    // Get download URL after successful upload
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();  // Firebase image URL
                        storeImageUrlInDB(f, imageUrl);  // Save the URL to the database
                    });
                }).addOnFailureListener(exception -> {
                    Toast.makeText(this, "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }
        Toast.makeText(this, "Photo sent to selected friends!", Toast.LENGTH_SHORT).show();
    }
    private void storeImageUrlInDB(FriendEntity friend, String imageUrl) {
        SentImageEntity sentImage = new SentImageEntity(myUsername, friend.getFriendUsername(), imageUrl);
        Executors.newSingleThreadExecutor().execute(() -> {
            db.sentImageDao().insertImage(sentImage);  // Save the image URL in the database
        });
    }
    private void loadImagesForAcceptedFriends() {
        List<FriendEntity> acceptedFriends = db.friendDao().getFriendsByOwner(myUsername);
        List<SentImageEntity> images = new ArrayList<>();

        // Filter for accepted friends
        for (FriendEntity friend : acceptedFriends) {
            if ("accepted".equals(friend.getStatus())) {
                // Retrieve images sent by the accepted friend
                List<SentImageEntity> imageList = db.sentImageDao().getImagesByReceiver(friend.getFriendUsername());
                images.addAll(imageList);
            }
        }

        // Display images
        for (SentImageEntity sentImage : images) {
            Glide.with(this)
                    .load(sentImage.getImageUrl())  // Use the Firebase URL stored
                    .into(ivFriendPhoto);  // Display image
        }
    }

}
