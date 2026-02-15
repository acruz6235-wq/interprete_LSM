package com.example.interpretels.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.interpretels.models.Sign;
import java.util.List;

@Dao
public interface SignDao {

    @Insert
    void insert(Sign sign);

    @Insert
    void insertAll(List<Sign> signs);

    @Query("SELECT * FROM signs WHERE categoria = :categoria ORDER BY nombre ASC")
    List<Sign> getSignsByCategory(String categoria);

    @Query("SELECT * FROM signs ORDER BY nombre ASC")
    List<Sign> getAllSigns();

    @Query("SELECT COUNT(*) FROM signs")
    int getSignCount();

    @Query("DELETE FROM signs")
    void deleteAll();
}