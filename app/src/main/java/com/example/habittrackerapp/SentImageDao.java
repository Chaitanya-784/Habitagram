package com.example.habittrackerapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SentImageDao {

    @Insert
    void insertImage(SentImageEntity sentImage);

    @Query("SELECT * FROM sent_images WHERE sender = :sender")
    List<SentImageEntity> getImagesBySender(String sender);

    @Query("SELECT * FROM sent_images WHERE receiver = :receiver")
    List<SentImageEntity> getImagesByReceiver(String receiver);
}
