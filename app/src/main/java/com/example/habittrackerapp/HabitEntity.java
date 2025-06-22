package com.example.habittrackerapp;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habits")
public class HabitEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String time;
    private int xp;
    private boolean isCompleted;
    private String username;

    @ColumnInfo(name = "date")
    private String date;


    // 👉 Constructor including the date
    public HabitEntity(String name, String time, int xp, boolean isCompleted, String username, String date) {
        this.name = name;
        this.time = time;
        this.xp = xp;
        this.isCompleted = isCompleted;
        this.username = username;
        this.date = date;
    }

    // Existing getters and setters...

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // ✅ Getter and Setter for `date`
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
