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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.wanhex.anxinpassword.add.PasswordAddActivity;
import com.wanhex.anxinpassword.cipher.AESEncrypt;
import com.wanhex.anxinpassword.cipher.KeyStoreUtil;
import com.wanhex.anxinpassword.clouddisk.BaiduNetDiskSettings;
import com.wanhex.anxinpassword.clouddisk.BaiduOAuthActivity;
import com.wanhex.anxinpassword.clouddisk.BaiduYunSync;
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

    private RelativeLayout mRestoreRLyt;
    private ImageView mNavRight3Iv;
    private ProgressBar mRestorePb;

    private ActivityResultLauncher mActivityResultLauncher;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.settings);
        setContentView(R.layout.activity_settings);

        mAboutTv = findViewById(R.id.tv_about);
        mBaiduYunNameTv = findViewById(R.id.tv_baidu_yun_account);
        mSwitchBtn = findViewById(R.id.btn_auto_backup_switch);

        mRestoreRLyt = findViewById(R.id.rlyt_restore);
        mNavRight3Iv = findViewById(R.id.iv_nav_right3);
        mRestorePb = findViewById(R.id.progress_bar);

        boolean syncSwitch = BaiduNetDiskSettings.getSyncSwitch(this, true);
        mSwitchBtn.setChecked(syncSwitch);

        mSwitchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                BaiduNetDiskSettings.setSyncSwitch(SettingsActivity.this, b);
                if (b) {
                    String baiduName = BaiduNetDiskSettings.getBaiduName(SettingsActivity.this);
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

        String baiduName = BaiduNetDiskSettings.getBaiduName(SettingsActivity.this);
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

                BaiduYunSync.getBaiduName(SettingsActivity.this, accessToken, new BaiduYunSync.OnBaiduUserInfoRecvListener() {
                    @Override
                    public void onUserNameRecv(String baiduName) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                String baiduName = BaiduNetDiskSettings.getBaiduName(SettingsActivity.this);
                                mBaiduYunNameTv.setText(baiduName);
                                syncToBaiduYun();
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMsg) {
                        Toast.makeText(SettingsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });

                BaiduNetDiskSettings.setAccessToken(SettingsActivity.this, accessToken);
            }
        });
    }

    private void syncToBaiduYun() {

        if (!BaiduNetDiskSettings.getSyncSwitch(this, true)) {
            return;
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                MyApp app = (MyApp)getApplication();
                AppDatabase appDatabase = app.getPasswordDb();
                List<Password> passwordList = appDatabase.passwordDao().getAll();
                String passwordsJsonStr = JSON.toJSONString(passwordList);

                String syncPassword = AppSettings.getSyncPassword(SettingsActivity.this);

                String passwordsJsonEncStr = AESEncrypt.encrypt(passwordsJsonStr, syncPassword);
                try {
                    FileOutputStream fos = openFileOutput(".passwords.dat", Context.MODE_PRIVATE);
                    fos.write(passwordsJsonEncStr.getBytes());
                    fos.flush();
                    fos.close();

                    String accessToken = BaiduNetDiskSettings.getAccessToken(SettingsActivity.this);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");//定义格式，不显示毫秒
                    Timestamp now = new Timestamp(System.currentTimeMillis());//获取系统当前时间
                    String dateTimeStr = simpleDateFormat.format(now);

                    boolean isDefaultSyncPassword = AppSettings.isDefaultSyncPassword(SettingsActivity.this);
                    String passwordSuffix = "_custom_sync_passwd";
                    if (isDefaultSyncPassword) {
                        passwordSuffix = "_default_sync_passwd";
                    }

                    BaiduYunSync.upload(accessToken, getFilesDir().getAbsolutePath() + "/.passwords.dat", "/apps/安心密码/passwords_" + dateTimeStr + passwordSuffix + ".dat", new BaiduYunSync.OnFileUploadListener() {
                        @Override
                        public void onSuccess() {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SettingsActivity.this, "同步本地密码列表到百度云盘成功!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(String result) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SettingsActivity.this, result, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } catch (Exception e ) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void onBaiduYunAccountSetBtnClicked(View view) {
        mActivityResultLauncher.launch(new Intent(this, BaiduOAuthActivity.class));

    }

    public void onAboutBtnClicked(View view) {
    }

    public void onRestoreBtnClicked(View view) {
        mRestoreRLyt.setEnabled(false);
        mNavRight3Iv.setVisibility(View.INVISIBLE);
        mRestorePb.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                super.run();

                String accessToken = BaiduNetDiskSettings.getAccessToken(SettingsActivity.this);
                BaiduYunSync.download(SettingsActivity.this, accessToken, "xx", new BaiduYunSync.OnFileDownListener() {
                    @Override
                    public void onSuccess() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mRestoreRLyt.setEnabled(true);
                                Toast.makeText(SettingsActivity.this, "数据还原（百度云盘->本机）成功!", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent();
                                intent.putExtra("passwords_synced", true);
                                SettingsActivity.this.setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onError(String result) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mRestoreRLyt.setEnabled(true);
                                Toast.makeText(SettingsActivity.this, result, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }.start();
    }

    public void onSyncPasswordBtnClicked(View view) {
        Toast.makeText(this, "开发中...", Toast.LENGTH_SHORT).show();
    }
}