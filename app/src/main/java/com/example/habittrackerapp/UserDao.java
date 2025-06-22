package com.example.habittrackerapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;
import androidx.room.Update;

import java.util.List;


@Dao
public interface UserDao {
    @Insert
    void insertUser(UserEntity user);



    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    UserEntity login(String username, String password);

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    UserEntity getUserByUsername(String username);

  /*  @Query("SELECT * FROM users WHERE username LIKE '%' || :search || '%' AND username != :myUsername")
    List<UserEntity> searchUsersByName(String search, String myUsername);*/

    @Query("SELECT * FROM users WHERE LOWER(username) LIKE LOWER(:search || '%') AND username != :myUsername")
    List<UserEntity> searchUsersByName(String search, String myUsername);







}

