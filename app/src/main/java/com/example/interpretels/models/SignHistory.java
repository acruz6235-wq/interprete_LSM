package com.example.interpretels.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sign_history")
public class SignHistory {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String signName;        // Nombre de la seña ("A", "Hola", etc.)
    private int count;              // Contador de repeticiones
    private long timestamp;         // Fecha/hora (en milisegundos)
    private String sessionId;       // ID de la sesión (para agrupar conversaciones)

    // Constructor
    public SignHistory(String signName, int count, long timestamp, String sessionId) {
        this.signName = signName;
        this.count = count;
        this.timestamp = timestamp;
        this.sessionId = sessionId;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}