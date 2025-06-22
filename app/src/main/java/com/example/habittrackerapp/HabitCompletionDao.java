package com.example.habittrackerapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HabitCompletionDao {

    @Insert
    void insertCompletion(HabitCompletionEntity completion);

    @Query("SELECT * FROM habit_completions WHERE username = :username AND date = :date")
    List<HabitCompletionEntity> getCompletionsByDate(String username, String date);

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND username = :username")
    List<HabitCompletionEntity> getCompletionsForHabit(int habitId, String username);

    @Query("SELECT habits.name FROM habit_completions " +
            "JOIN habits ON habit_completions.habitId = habits.id " +
            "WHERE habit_completions.username = :username AND habit_completions.date = :date")
    List<String> getHabitNamesByDate(String username, String date);

    // ✅ Added: check if a habit is completed on a specific date for a user
    @Query("SELECT EXISTS(SELECT 1 FROM habit_completions WHERE habitId = :habitId AND date = :date AND username = :username)")
    boolean isHabitCompletedOnDate(int habitId, String date, String username);


    @Query("SELECT * FROM habit_completions WHERE habitId = :id AND username = :username AND date = :date LIMIT 1")
    HabitCompletionEntity getCompletion(int id, String username, String date);

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND username = :username AND date = :date")
    void deleteCompletion(int habitId, String username, String date);





}
