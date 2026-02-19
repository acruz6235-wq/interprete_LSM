package com.example.interpretels.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.interpretels.models.SignHistory;
import java.util.List;

@Dao
public interface SignHistoryDao {

    @Insert
    void insert(SignHistory history);

    @Query("SELECT * FROM sign_history ORDER BY timestamp DESC")
    List<SignHistory> getAllHistory();

    @Query("SELECT * FROM sign_history WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    List<SignHistory> getHistoryBySession(String sessionId);

    @Query("SELECT DISTINCT sessionId FROM sign_history ORDER BY timestamp DESC")
    List<String> getAllSessions();

    @Query("DELETE FROM sign_history")
    void deleteAll();

    @Query("DELETE FROM sign_history WHERE sessionId = :sessionId")
    void deleteSession(String sessionId);
}