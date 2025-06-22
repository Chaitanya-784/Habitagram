package com.example.habittrackerapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FriendDao {

    @Insert
    void insertFriend(FriendEntity friend);

    @Query("SELECT friendUsername FROM friends WHERE myUsername = :username")
    List<String> getFriendsOfUser(String username);

    @Query("SELECT * FROM friends WHERE myUsername = :username")
    List<FriendEntity> getFriendsByOwner(String username);

    @Query("SELECT * FROM friends WHERE friendUsername = :myUsername AND status = 'pending'")
    List<FriendEntity> getPendingRequests(String myUsername);

    @Query("UPDATE friends SET status = :status WHERE id = :id")
    void updateFriendStatus(int id, String status);

    @Query("SELECT * FROM friends WHERE myUsername = :username AND status = 'accepted'")
    List<FriendEntity> getAcceptedFriends(String username);

}
