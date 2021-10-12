package com.wanhex.anxinpassword.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanhex.anxinpassword.MyApp;
import com.wanhex.anxinpassword.R;
import com.wanhex.anxinpassword.cipher.AESEncrypt;
import com.wanhex.anxinpassword.cipher.KeyStoreUtil;
import com.wanhex.anxinpassword.db.AppDatabase;
import com.wanhex.anxinpassword.db.Password;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";

    private TextView mAboutTv;
    private TextView mBaiduYunNameTv;
    private Switch mSwitchBtn;

    private ActivityResultLauncher mActivityResultLauncher;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.settings);
        setContentView(R.layout.activity_settings);

        mAboutTv = findViewById(R.id.tv_about);
        mBaiduYunNameTv = findViewById(R.id.tv_title);
        mSwitchBtn = findViewById(R.id.btn_switch);

        boolean syncSwitch = AppSettings.getBoolean(this, "sync_switch", true);
        mSwitchBtn.setChecked(syncSwitch);

        mSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppSettings.setBoolean(SettingsActivity.this, "sync_switch", b);
                if (b) {
                    SharedPreferences sp = getSharedPreferences("baidu_yun", Context.MODE_PRIVATE);
                    String baiduName = sp.getString("baidu_name", "未登录");
                    if (!baiduName.equals("未登录")) {
                        syncToBaiduYun();
                    }
                }
            }
        });

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

        mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                Intent data = result.getData();
                int resultCode = result.getResultCode();
                if (resultCode != RESULT_OK) {
                    return;
                }

                Toast.makeText(SettingsActivity.this, "登录成功!!!", Toast.LENGTH_SHORT).show();

                String accessToken = data.getStringExtra("access_token");
                Log.d(TAG, accessToken);

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
                                syncToBaiduYun();
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

    private void syncToBaiduYun() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                MyApp app = (MyApp)getApplication();
                AppDatabase appDatabase = app.getPasswordDb();
                List<Password> passwordList = appDatabase.passwordDao().getAll();
                String passwordsJsonStr = JSON.toJSONString(passwordList);
                String passwordsJsonEncStr = AESEncrypt.encrypt(passwordsJsonStr, "983u4948u9u4t");
                try {
                    FileOutputStream fos = openFileOutput(".passwords.dat", Context.MODE_PRIVATE);
                    fos.write(passwordsJsonEncStr.getBytes());
                    fos.flush();
                    fos.close();

                    SharedPreferences sp = getSharedPreferences("baidu_yun", Context.MODE_PRIVATE);
                    String accessTokenEnc = sp.getString("access_token", "");
                    String accessTokenDec = new String(KeyStoreUtil.decrypt(accessTokenEnc));
                    Log.d(TAG, accessTokenDec);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");//定义格式，不显示毫秒
                    Timestamp now = new Timestamp(System.currentTimeMillis());//获取系统当前时间
                    String str = df.format(now);
                    String uploadUrl = "https://c.pcs.baidu.com/rest/2.0/pcs/file?method=upload&access_token=" + accessTokenDec + "&path=/apps/安心密码/passwords_" + str + ".dat";
                    OkHttpClient okHttpClient = new OkHttpClient();
                    // file是要上传的文件 File()
                    File file = new File(getFilesDir().getAbsolutePath() + "/.passwords.dat");
                    RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                    // 不仅可以支持传文件，还可以在传文件的同时，传参数
                    MultipartBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("接收文件的参数名", "passwords", fileBody)
                            .build();
                    Request request = new Request.Builder().url(uploadUrl).post(requestBody).build();
                    Response response = okHttpClient.newCall(request).execute();
                    if(!response.isSuccessful()) {
                        // 一般会在这抛个异常
                    }
                    String result = response.body().string();
                    Log.d(TAG, result);
                    JSONObject resultJsonObj = JSON.parseObject(result);
                    Log.d(TAG, resultJsonObj.toString());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (!resultJsonObj.getString("fs_id").isEmpty()) {
                                Toast.makeText(SettingsActivity.this, "同步本地密码列表到百度云盘成功!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SettingsActivity.this, result, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    response.body().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        }.start();
    }

    public void onBaiduYunBtnClicked(View view) {
        mActivityResultLauncher.launch(new Intent(this, BaiduOAuthActivity.class));

    }

    public void onAboutBtnClicked(View view) {
    }

    public void onRestoreBtnClicked(View view) {
    }
}