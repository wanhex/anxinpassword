package com.wanhex.anxinpassword;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.room.Room;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.wanhex.anxinpassword.cipher.CipherUtil;
import com.wanhex.anxinpassword.cipher.RandomUntil;
import com.wanhex.anxinpassword.db.AppDatabase;

import net.sqlcipher.database.SupportFactory;

public class MyApp extends Application {

    AppDatabase mPasswordDb;

    @Override
    public void onCreate() {
        super.onCreate();

        byte[] mainDbPasswordBytes = CipherUtil.getMainDbPassword(getApplicationContext());
        SupportFactory factory = new SupportFactory(mainDbPasswordBytes);

        mPasswordDb = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "passwords.sqlite").openHelperFactory(factory).build();

        Bugly.init(getApplicationContext(), "49d54a1304", true);
    }

    public AppDatabase getPasswordDb() {
        return mPasswordDb;
    }
}
