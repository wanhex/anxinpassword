package com.wanhex.anxinpassword.clouddisk;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.wanhex.anxinpassword.cipher.KeyStoreUtil;

public class BaiduNetDiskSettings {

    private static final String SETTINGS_FILE_NAME = "baidu_yun_settings";

    public static void setSyncSwitch(Context context, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("sync_switch", value);
        editor.commit();
    }

    public static boolean getSyncSwitch(Context context, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean("sync_switch", defaultValue);
    }

    public static void setBaiduName(Context context, String name) {
        SharedPreferences sp = context.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);

        byte[] encryptedBytes = new byte[0];
        try {
            encryptedBytes = KeyStoreUtil.encrypt(name);
            String baiduNameEncrypted = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);

            SharedPreferences.Editor editor = sp.edit();
            editor.putString("baidu_name", baiduNameEncrypted);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getBaiduName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);

        String baiduName = sp.getString("baidu_name", "未登录");
        if (!baiduName.equals("未登录")) {
            try {
                baiduName = new String(KeyStoreUtil.decrypt(baiduName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return baiduName;
    }

    public static void setAccessToken(Context context, String accessToken) {
        SharedPreferences sp = context.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);

        byte[] encryptedBytes = new byte[0];
        try {
            encryptedBytes = KeyStoreUtil.encrypt(accessToken);
            String accessTokenEncrypted = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);

            SharedPreferences.Editor editor = sp.edit();
            editor.putString("access_token", accessTokenEncrypted);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAccessToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);

        String accessToken = sp.getString("access_token", "");
        if (!accessToken.equals("")) {
            try {
                accessToken = new String(KeyStoreUtil.decrypt(accessToken));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return accessToken;
    }
}
