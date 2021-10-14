package com.wanhex.anxinpassword.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.wanhex.anxinpassword.cipher.KeyStoreUtil;

public class AppSettings {

    private static final String SETTINGS_FILE_NAME = "app_settings";

    public static void setSyncPassword(Context context, String syncPassword) {
        SharedPreferences sp = context.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);

        byte[] encryptedBytes = new byte[0];
        try {
            encryptedBytes = KeyStoreUtil.encrypt(syncPassword);
            String valueEncrypted = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);

            SharedPreferences.Editor editor = sp.edit();
            editor.putString("sync_password", valueEncrypted);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getSyncPassword(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);

        String value = sp.getString("sync_password", "");
        if (!value.equals("")) {
            try {
                value = new String(KeyStoreUtil.decrypt(value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            value = "non_secure_default_sync_password";
        }

        return value;
    }

    public static boolean isDefaultSyncPassword(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);

        String value = sp.getString("sync_password", "");
        return value.equals("");
    }
}
