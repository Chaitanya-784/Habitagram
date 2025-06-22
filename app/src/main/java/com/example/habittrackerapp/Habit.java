package com.example.habittrackerapp;

public class Habit {
    private String name;
    private String time;
    private int xp;
    private boolean isCompleted;

    public Habit(String name, String time, int xp) {
        this.name = name;
        this.time = time;
        this.xp = xp;
        this.isCompleted = false;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public int getXp() {
        return xp;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    // 🔧 Add these setters for editing
    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
