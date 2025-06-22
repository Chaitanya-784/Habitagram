package com.example.habittrackerapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "friends")
public class FriendEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String myUsername;

    private String friendUsername;

    private String status;


    // Constructor
    public FriendEntity(String myUsername, String friendUsername,String status) {
        this.myUsername = myUsername;
        this.friendUsername = friendUsername;
        this.status = status;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getMyUsername() { return myUsername; }
    public void setMyUsername(String myUsername) { this.myUsername = myUsername; }

    public String getFriendUsername() { return friendUsername; }
    public void setFriendUsername(String friendUsername) { this.friendUsername = friendUsername; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
