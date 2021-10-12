package com.wanhex.anxinpassword.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.oauth.sdk.auth.AuthInfo;
import com.baidu.oauth.sdk.auth.BdOauthSdk;
import com.baidu.oauth.sdk.auth.BdSsoHandler;
import com.baidu.oauth.sdk.callback.BdOauthCallback;
import com.baidu.oauth.sdk.dto.BdOauthDTO;
import com.baidu.oauth.sdk.result.BdOauthResult;
import com.wanhex.anxinpassword.R;
import com.wanhex.anxinpassword.cipher.KeyStoreUtil;
import com.wanhex.anxinpassword.db.Password;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private TextView mAboutTv;
    private TextView mBaiduYunNameTv;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.settings);
        setContentView(R.layout.activity_settings);

        mAboutTv = findViewById(R.id.tv_about);
        mBaiduYunNameTv = findViewById(R.id.tv_title);

        String about = AppUtils.getAppName(this);
        about += " v";
        about += AppUtils.getVersionName(this);
        mAboutTv.setHint(about);


        SharedPreferences sp = getSharedPreferences("baidu_yun", Context.MODE_PRIVATE);
        String baiduName = sp.getString("baidu_name", "未登录");
        if (!baiduName.equals("未登录")) {
            try {
                baiduName = new String(KeyStoreUtil.decrypt(baiduName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mBaiduYunNameTv.setText(baiduName);

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                Intent data = result.getData();
                int resultCode = result.getResultCode();
                if (resultCode != RESULT_OK) {
                    return;
                }

                Toast.makeText(SettingsActivity.this, "登录成功!!!", Toast.LENGTH_SHORT).show();

                String accessToken = data.getStringExtra("access_token");

                String url = "https://pan.baidu.com/rest/2.0/xpan/nas?method=uinfo&access_token=xxxxxxxx";
                url = url.replace("xxxxxxxx", accessToken);
                OkHttpClient okHttpClient = new OkHttpClient();
                final Request request = new Request.Builder()
                        .url(url)
                        .get()//默认就是GET请求，可以不写
                        .build();
                Call call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: ");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String respStr = response.body().string();
                        Log.d(TAG, "onResponse: " + respStr);

                        JSONObject jsonObject = (JSONObject) JSON.parse(respStr);

                        String baiduName = jsonObject.getString("baidu_name");

                        SharedPreferences sp = getSharedPreferences("baidu_yun", Context.MODE_PRIVATE);
                        byte[] encryptedBytes = new byte[0];
                        try {
                            encryptedBytes = KeyStoreUtil.encrypt(baiduName);
                            String baiduNameEncrypted = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);

                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("baidu_name", baiduNameEncrypted);
                            editor.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String finalBaiduName = baiduName;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mBaiduYunNameTv.setHint(finalBaiduName);
                            }
                        });

                    }
                });


                SharedPreferences sp = getSharedPreferences("baidu_yun", Context.MODE_PRIVATE);
                byte[] encryptedBytes = new byte[0];
                try {
                    encryptedBytes = KeyStoreUtil.encrypt(accessToken);
                    accessToken = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("access_token", accessToken);
                    editor.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }


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