package com.wanhex.anxinpassword.add;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.wanhex.anxinpassword.MyApp;
import com.wanhex.anxinpassword.R;
import com.wanhex.anxinpassword.cipher.KeyguardVerifyUtil;
import com.wanhex.anxinpassword.cipher.RandomUtil;
import com.wanhex.anxinpassword.db.AppDatabase;
import com.wanhex.anxinpassword.db.Password;

public class PasswordAddActivity extends AppCompatActivity implements TextWatcher {

    private boolean mHasTextChanged;
    private Password mPassword;
    private boolean mKeyguardVerified;

    private EditText mSiteEt;
    private EditText mUsernameEt;
    private EditText mPasswordEt;
    private EditText mCommentsEt;

    private ImageButton mRandomBtn;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.password_add);
        setContentView(R.layout.activity_password_add);

        mSiteEt = findViewById(R.id.et_title);
        mUsernameEt = findViewById(R.id.et_username);
        mPasswordEt = findViewById(R.id.et_passwd);
        mCommentsEt = findViewById(R.id.et_comments);
        mRandomBtn = findViewById(R.id.ib_random_btn);

        mRandomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordEt.setText(RandomUtil.getNumLargeSmallLetter(8));
            }
        });

        mSiteEt.addTextChangedListener(this);
        mUsernameEt.addTextChangedListener(this);
        mPasswordEt.addTextChangedListener(this);
        mCommentsEt.addTextChangedListener(this);

        mKeyguardVerified = true;
        KeyguardVerifyUtil.setOnKeyguardVerifiedListener(this, new KeyguardVerifyUtil.OnKeyguardVerifiedListener() {
            @Override
            public void onKeyguardVerifyResult(boolean keyguardVerified) {
                mKeyguardVerified = keyguardVerified;
                if (keyguardVerified) {
                    KeyguardVerifyUtil.updatePassTime(PasswordAddActivity.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.password_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_password) {
            mPassword = new Password(
                    mSiteEt.getText().toString(),
                    mUsernameEt.getText().toString(),
                    mPasswordEt.getText().toString(),
                    mCommentsEt.getText().toString()
            );
            mPassword.timeStamp = System.currentTimeMillis();
            mPassword.abbreviatedUserName = mPassword.getAbbreviatedUserName();
            save(mPassword);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mHasTextChanged) {
            new AlertDialog.Builder(this)
                    .setMessage("要放弃本次新增的密码吗？")
                    .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
        } else {
            super.onBackPressed();
        }
    }

    private void save(Password password) {

        boolean isSomethingEmpty = false;
        if (password.password.trim().isEmpty()) {
            mPasswordEt.setError("密码不能为空");
            mPasswordEt.requestFocus();
            isSomethingEmpty = true;
        }
        if (password.username.trim().isEmpty()) {
            mUsernameEt.setError("用户名不能为空");
            mUsernameEt.requestFocus();
            isSomethingEmpty = true;
        }
        if (password.site.trim().isEmpty()) {
            mSiteEt.setError("标题不能为空");
            mSiteEt.requestFocus();
            isSomethingEmpty = true;
        }
        if (isSomethingEmpty) {
            return;
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                MyApp app = (MyApp) getApplication();
                AppDatabase appDatabase = app.getPasswordDb();
                try {
                    appDatabase.passwordDao().insert(password);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent();
                        intent.putExtra("password_new", password);
                        PasswordAddActivity.this.setResult(RESULT_OK, intent);
                        finish();
                    }
                });
            }
        }.start();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mHasTextChanged = true;
    }
}