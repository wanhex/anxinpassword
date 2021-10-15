package com.wanhex.anxinpassword.cipher;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class KeyguardVerifyUtil {

    private static final String TAG = "KeyguardVerifyUtil";

    public interface OnKeyguardVerifiedListener {
        void onKeyguardVerifyResult(boolean keyguardVerified);
    }

    private static OnKeyguardVerifiedListener mOnKeyguardVerifiedListener;
    private static ActivityResultLauncher mActivityResultLauncher;

    public static void setOnKeyguardVerifiedListener(ComponentActivity activity, OnKeyguardVerifiedListener onKeyguardVerifiedListener) {
        KeyguardVerifyUtil.mOnKeyguardVerifiedListener = onKeyguardVerifiedListener;
        mActivityResultLauncher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                int resultCode = result.getResultCode();
                if (resultCode != RESULT_OK) {
                    mOnKeyguardVerifiedListener.onKeyguardVerifyResult(false);
                } else {
                    updatePassTime(activity);
                    mOnKeyguardVerifiedListener.onKeyguardVerifyResult(true);
                }

            }
        });
    }

    public static void checkKeyguard(Activity activity) {

        boolean needLaunchKeyguard = false;

        SharedPreferences sp = activity.getSharedPreferences("keyguard_check", Context.MODE_PRIVATE);
        long lastPassTime = sp.getLong("pass_time", -1);
        // first start app
        if (lastPassTime == -1) {
            needLaunchKeyguard = true;
        } else {
            // non first start app.
            long delta = (System.currentTimeMillis() - lastPassTime) / 1000;
            Log.d(TAG, "delta: " + delta);
            if (delta > 60) {
                needLaunchKeyguard = true;
            }
        }

        if (!needLaunchKeyguard) {
            mOnKeyguardVerifiedListener.onKeyguardVerifyResult(true);
            return;
        }

        KeyguardManager keyguardMgr = null;
        keyguardMgr = activity.getSystemService(KeyguardManager.class);
        Intent intent = keyguardMgr.createConfirmDeviceCredentialIntent(null, null);
        if (intent == null) {
            mOnKeyguardVerifiedListener.onKeyguardVerifyResult(true);
            return;
        }

        mActivityResultLauncher.launch(intent);
    }

    public static void updatePassTime(Activity activity) {
        Log.d(TAG, "updatePassTime");
        SharedPreferences sp = activity.getSharedPreferences("keyguard_check", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("pass_time", System.currentTimeMillis());
        editor.commit();
    }
}
