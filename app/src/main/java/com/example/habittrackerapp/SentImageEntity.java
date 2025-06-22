package com.example.habittrackerapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sent_images")
public class SentImageEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String sender;
    private String receiver;
    private String imageUrl; // Store image URL from Firebase

    // Constructor
    public SentImageEntity(String sender, String receiver, String imageUrl) {
        this.sender = sender;
        this.receiver = receiver;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
