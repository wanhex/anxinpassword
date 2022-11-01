package com.wanhex.anxinpassword;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.wanhex.anxinpassword.add.PasswordAddActivity;
import com.wanhex.anxinpassword.cipher.AESUtil;
import com.wanhex.anxinpassword.cipher.KeyguardVerifyUtil;
import com.wanhex.anxinpassword.clouddisk.BaiduNetDiskSettings;
import com.wanhex.anxinpassword.clouddisk.BaiduYunSync;
import com.wanhex.anxinpassword.databinding.ActivityMainBinding;
import com.wanhex.anxinpassword.db.AppDatabase;
import com.wanhex.anxinpassword.db.Password;
import com.wanhex.anxinpassword.edit.PasswordEditActivity;
import com.wanhex.anxinpassword.settings.AppSettings;
import com.wanhex.anxinpassword.settings.SettingsActivity;

import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    static {
//        System.loadLibrary("anxinpassword");
//    }

    private ActivityMainBinding binding;
    private ActivityResultLauncher mActivityResultLauncher;
    private Handler mHandler = new Handler();

    private List<Password> mPasswordList = new ArrayList<>();
    private PasswordAdapter mAdapter;

    private RecyclerView mPasswordListView;
    private boolean mKeyguardVerified;

    private ImageView mEmptyIv;
    private TextView mEmptyTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPasswordListView = binding.passwordList;
        mEmptyIv = binding.ivEmpty;
        mEmptyTv = binding.tvEmpty;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPasswordListView.setLayoutManager(layoutManager);
        mAdapter = new PasswordAdapter(mPasswordList);
        mPasswordListView.setAdapter(mAdapter);

        loadPasswords();

        mAdapter.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("要删除 " + mPasswordList.get(i).site + " 吗？")
                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        super.run();
                                        MyApp app = (MyApp) getApplication();
                                        AppDatabase appDatabase = app.getPasswordDb();
                                        appDatabase.passwordDao().delete(mPasswordList.get(i));

                                        mHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                mPasswordList.remove(i);
                                                mAdapter.notifyDataSetChanged();

                                                if (mPasswordList.size() == 0) {
                                                    mEmptyIv.setVisibility(View.VISIBLE);
                                                    mEmptyTv.setVisibility(View.VISIBLE);
                                                } else {
                                                    mEmptyIv.setVisibility(View.GONE);
                                                    mEmptyTv.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    }
                                }.start();

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                return false;
            }
        });

        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PasswordEditActivity.class);
                intent.putExtra("password", mPasswordList.get(position));
                mActivityResultLauncher.launch(intent);
            }
        });

        mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                Intent data = result.getData();
                int resultCode = result.getResultCode();
                if (resultCode != RESULT_OK) {
                    return;
                }

                Password passwordNew = (Password) data.getExtras().get("password_new");
                if (passwordNew != null) {
                    mPasswordList.add(0, passwordNew);
                    mAdapter.notifyDataSetChanged();
                    syncToBaiduYun();

                    mEmptyIv.setVisibility(View.GONE);
                    mEmptyTv.setVisibility(View.GONE);
                    return;
                }
                Password passwordEdit = (Password) data.getExtras().get("password_edit");
                if (passwordEdit != null) {
                    for (int i = 0; i < mPasswordList.size(); i++) {
                        Password passwordInList = mPasswordList.get(i);
                        if (passwordInList.id == passwordEdit.id) {
                            passwordInList.site = passwordEdit.site;
                            passwordInList.username = passwordEdit.username;
                            passwordInList.abbreviatedUserName = passwordEdit.abbreviatedUserName;
                            passwordInList.password = passwordEdit.password;
                            passwordInList.comments = passwordEdit.comments;

                            mAdapter.notifyItemChanged(i);
                            break;
                        }
                    }

                    syncToBaiduYun();
                }

                boolean passwordsSynced = data.getExtras().getBoolean("passwords_synced", false);
                if (passwordsSynced) {
                    loadPasswords();
                }

            }
        });

        KeyguardVerifyUtil.setOnKeyguardVerifiedListener(this, new KeyguardVerifyUtil.OnKeyguardVerifiedListener() {
            @Override
            public void onKeyguardVerifyResult(boolean keyguardVerified) {
                mKeyguardVerified = keyguardVerified;
                if (keyguardVerified) {
                    KeyguardVerifyUtil.updatePassTime(MainActivity.this);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        KeyguardVerifyUtil.checkKeyguard(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mKeyguardVerified) {
            KeyguardVerifyUtil.updatePassTime(this);
        }
    }

    private void loadPasswords() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                MyApp app = (MyApp) getApplication();
                AppDatabase appDatabase = app.getPasswordDb();
                List<Password> passwordList = appDatabase.passwordDao().getAll();
                mPasswordList.clear();
                mPasswordList.addAll(passwordList);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        if (mPasswordList.size() == 0) {
                            mEmptyIv.setVisibility(View.VISIBLE);
                            mEmptyTv.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyIv.setVisibility(View.GONE);
                            mEmptyTv.setVisibility(View.GONE);
                        }
                    }
                });

            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_password:
                mActivityResultLauncher.launch(new Intent(this, PasswordAddActivity.class));
                break;
            case R.id.settings:
                mActivityResultLauncher.launch(new Intent(this, SettingsActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    private void syncToBaiduYun() {

        if (!BaiduNetDiskSettings.getSyncSwitch(this, true)) {
            return;
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                MyApp app = (MyApp) getApplication();
                AppDatabase appDatabase = app.getPasswordDb();
                List<Password> passwordList = appDatabase.passwordDao().getAll();
                String passwordsJsonStr = JSON.toJSONString(passwordList);

                String syncPassword = AppSettings.getSyncPassword(MainActivity.this);

                String passwordsJsonEncStr = AESUtil.encrypt(passwordsJsonStr, syncPassword);
                try {
                    FileOutputStream fos = openFileOutput(".passwords.dat", Context.MODE_PRIVATE);
                    fos.write(passwordsJsonEncStr.getBytes());
                    fos.flush();
                    fos.close();

                    String accessToken = BaiduNetDiskSettings.getAccessToken(MainActivity.this);
                    if (accessToken.isEmpty()) {
                        return;
                    }

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");//定义格式，不显示毫秒
                    Timestamp now = new Timestamp(System.currentTimeMillis());//获取系统当前时间
                    String dateTimeStr = simpleDateFormat.format(now);

                    boolean isDefaultSyncPassword = AppSettings.isDefaultSyncPassword(MainActivity.this);
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
                                    Toast.makeText(MainActivity.this, "同步本地密码列表到百度云盘成功!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(String result) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public native String stringFromJNI();
}