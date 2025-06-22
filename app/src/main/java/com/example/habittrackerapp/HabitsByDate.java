package com.example.habittrackerapp;

import java.util.List;

public class HabitsByDate {
    private String date;
    private List<HabitEntity> habits;

    public HabitsByDate(String date, List<HabitEntity> habits) {
        this.date = date;
        this.habits = habits;
    }

    public String getDate() {
        return date;
    }

    public List<HabitEntity> getHabits() {
        return habits;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHabits(List<HabitEntity> habits) {
        this.habits = habits;
    }
}
