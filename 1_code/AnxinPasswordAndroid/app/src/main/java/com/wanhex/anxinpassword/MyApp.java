package com.wanhex.anxinpassword;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import androidx.room.Room;

import com.wanhex.anxinpassword.db.AppDatabase;

import net.sqlcipher.database.SupportFactory;

public class MyApp extends Application {

    AppDatabase mPasswordDb;

    @Override
    public void onCreate() {
        super.onCreate();
        SupportFactory factory = new SupportFactory("123456".getBytes());

        mPasswordDb = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "passwords.sqlite").openHelperFactory(factory).build();
    }

    public AppDatabase getPasswordDb() {
        return mPasswordDb;
    }
}
