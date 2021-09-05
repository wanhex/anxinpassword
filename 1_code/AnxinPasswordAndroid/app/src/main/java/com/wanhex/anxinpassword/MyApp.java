package com.wanhex.anxinpassword;

import android.app.Application;

import androidx.room.Room;

import com.wanhex.anxinpassword.db.AppDatabase;

public class MyApp extends Application {

    AppDatabase mPasswordDb;

    @Override
    public void onCreate() {
        super.onCreate();
        mPasswordDb = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "passwords.sqlite").build();
    }

    public AppDatabase getPasswordDb() {
        return mPasswordDb;
    }
}
