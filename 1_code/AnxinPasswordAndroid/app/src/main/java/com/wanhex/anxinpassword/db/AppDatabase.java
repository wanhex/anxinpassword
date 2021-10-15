package com.wanhex.anxinpassword.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Password.class}, version = 1)

public abstract class AppDatabase extends RoomDatabase {
    public abstract PasswordDao passwordDao();
}