package com.example.habittrackerapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habit_completions")
public class HabitCompletionEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int habitId;
    public String date;      // Format: yyyy-MM-dd
    public String username;

    public HabitCompletionEntity(int habitId, String date, String username) {
        this.habitId = habitId;
        this.date = date;
        this.username = username;
    }

    // Getters and setters if needed
}
