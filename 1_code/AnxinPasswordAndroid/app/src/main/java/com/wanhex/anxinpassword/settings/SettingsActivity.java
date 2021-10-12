package com.wanhex.anxinpassword.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.oauth.sdk.auth.AuthInfo;
import com.baidu.oauth.sdk.auth.BdOauthSdk;
import com.baidu.oauth.sdk.auth.BdSsoHandler;
import com.baidu.oauth.sdk.callback.BdOauthCallback;
import com.baidu.oauth.sdk.dto.BdOauthDTO;
import com.baidu.oauth.sdk.result.BdOauthResult;
import com.wanhex.anxinpassword.R;
import com.wanhex.anxinpassword.db.Password;

import java.util.UUID;

public class SettingsActivity extends AppCompatActivity {

    private TextView mAboutTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.settings);
        setContentView(R.layout.activity_settings);

        mAboutTv = findViewById(R.id.tv_about);

        String about = AppUtils.getAppName(this);
        about += " v";
        about += AppUtils.getVersionName(this);
        mAboutTv.setHint(about);


        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                Intent data = result.getData();
                int resultCode = result.getResultCode();
                if (resultCode != RESULT_OK) {
                    return;
                }

                Log.e("xxxxx", "access_token= " + data.getStringExtra("access_token"));
                Toast.makeText(SettingsActivity.this, "access_token= " + data.getStringExtra("access_token"), Toast.LENGTH_SHORT).show();


            }
        });
    }

    ActivityResultLauncher activityResultLauncher;

    public void onBaiduYunBtnClicked(View view) {
        activityResultLauncher.launch(new Intent(this, BaiduOAuthActivity.class));

    }

    public void onAboutBtnClicked(View view) {
    }

}