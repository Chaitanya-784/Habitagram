package com.example.habittrackerapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HabitDao {



    @Insert
    void insertHabit(HabitEntity habit);

    @Update
    void updateHabit(HabitEntity habit);

    @Delete
    void deleteHabit(HabitEntity habit);

    @Query("DELETE FROM habits")
    void deleteAllHabits();

    @Query("SELECT * FROM habits")
    LiveData<List<HabitEntity>> getAllHabits();

    @Query("SELECT * FROM habits WHERE username = :username")
    List<HabitEntity> getHabitsByUser(String username);

    @Query("SELECT * FROM habits WHERE username = :username AND isCompleted = 1")
    List<HabitEntity> getCompletedHabitsByUser(String username);


    @Query("SELECT * FROM habits WHERE date = :date AND username = :username")
    List<HabitEntity> getHabitsByDateAndUser(String date, String username);

    // ✅ New method: get habits filtered by username
    @Query("SELECT * FROM habits WHERE username = :username")
    LiveData<List<HabitEntity>> getHabitsByUserLive(String username);

    @Query("SELECT DISTINCT date FROM habits WHERE username = :username ORDER BY date DESC")
    List<String> getAllDatesForUser(String username);

    @Query("SELECT * FROM habits WHERE username = :username AND date = :date")
    List<HabitEntity> getHabitsByUserAndDate(String username, String date);


}
