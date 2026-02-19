package com.example.interpretels.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.interpretels.models.Sign;
import com.example.interpretels.models.SignHistory;  // ← AGREGAR

@Database(entities = {Sign.class, SignHistory.class}, version = 2, exportSchema = false)  // ← CAMBIAR version a 2
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract SignDao signDao();
    public abstract SignHistoryDao signHistoryDao();  // ← AGREGAR

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "sign_language_database"
                    )
                    .fallbackToDestructiveMigration()  // ← AGREGAR (borra datos al actualizar versión)
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}