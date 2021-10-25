package com.wanhex.anxinpassword.cipher;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

public class DBPasswordUtil {

    public static byte[] getMainDbPassword(Context context) {
        SharedPreferences sp = context.getSharedPreferences("cipher", Context.MODE_PRIVATE);
        if (!sp.contains("main_db_passwd")) {
            String dbPassword = RandomUtil.getNumLargeSmallLetter(128);
            try {
                KeyStoreUtil.createKey();
                byte[] encryptedBytes = KeyStoreUtil.encrypt(dbPassword);
                dbPassword = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            SharedPreferences.Editor editor = sp.edit();
            editor.putString("main_db_passwd", dbPassword);
            editor.commit();
        }

        String encryptedPassword = sp.getString("main_db_passwd", "");
        try {
            return KeyStoreUtil.decrypt(encryptedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return encryptedPassword.getBytes();
        }
    }

}
