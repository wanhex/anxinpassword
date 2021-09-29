package com.wanhex.anxinpassword.edit;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wanhex.anxinpassword.MyApp;
import com.wanhex.anxinpassword.R;
import com.wanhex.anxinpassword.db.AppDatabase;
import com.wanhex.anxinpassword.db.Password;

import java.util.Random;

public class PasswordEditActivity extends AppCompatActivity implements TextWatcher {

    private boolean mHasTextChanged;
    private Password mPassword;

    private Menu mMenu;

    private EditText mSiteEt;
    private EditText mUsernameEt;
    private EditText mPasswordEt;
    private EditText mCommentsEt;

    private ImageButton mRandomBtn;
    private ImageButton mCopyBtn;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.password_view);
        setContentView(R.layout.activity_password_edit);

        mSiteEt = findViewById(R.id.et_title);
        mUsernameEt = findViewById(R.id.et_username);
        mPasswordEt = findViewById(R.id.et_passwd);
        mCommentsEt = findViewById(R.id.et_comments);
        mRandomBtn = findViewById(R.id.ib_random_btn);
        mCopyBtn = findViewById(R.id.ib_copy_btn);

        mSiteEt.setEnabled(false);
        mUsernameEt.setEnabled(false);
        mPasswordEt.setEnabled(false);
        mCommentsEt.setEnabled(false);
        mRandomBtn.setVisibility(View.INVISIBLE);

        mPassword = getIntent().getParcelableExtra("password");
        mSiteEt.setText(mPassword.site);
        mUsernameEt.setText(mPassword.username);
        mPasswordEt.setText(mPassword.password);
        mCommentsEt.setText(mPassword.comments);

        mRandomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPasswordEt.setText(genRandomNum());
            }
        });

        mCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(ClipData.newPlainText("", mPassword.password));
                    Toast.makeText(PasswordEditActivity.this, "密码已复制到剪切板", Toast.LENGTH_SHORT).show();
                } catch (Throwable e) {
                    Toast.makeText(PasswordEditActivity.this, "对不起，没复制成功！！", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        mSiteEt.addTextChangedListener(this);
        mUsernameEt.addTextChangedListener(this);
        mPasswordEt.addTextChangedListener(this);
        mCommentsEt.addTextChangedListener(this);
    }

    private String genRandomNum() {
        int maxNum = 36;
        int i;
        int count = 0;
        char[] str0 = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z'};
        char[] str1 = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W'};
        char[] str2 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        char[] str3 = {'!', '@', '#', '$', '%', '^', '&', '*', '(', ')'};

        StringBuilder pwd = new StringBuilder("");
        Random r = new Random();

        for (int j = 0; j < 2; j++) {

            i = Math.abs(r.nextInt(str0.length));
            pwd.append(str0[i]);
            i = Math.abs(r.nextInt(str0.length));
            pwd.append(str0[i]);

            i = Math.abs(r.nextInt(str1.length));
            pwd.append(str1[i]);
            i = Math.abs(r.nextInt(str1.length));
            pwd.append(str1[i]);

            i = Math.abs(r.nextInt(str2.length));
            pwd.append(str2[i]);
            i = Math.abs(r.nextInt(str2.length));
            pwd.append(str2[i]);

            i = Math.abs(r.nextInt(str3.length));
            pwd.append(str3[i]);
            i = Math.abs(r.nextInt(str3.length));
            pwd.append(str3[i]);
        }

        return pwd.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.password_edit, menu);

        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_password:
                mMenu.getItem(0).setVisible(false);
                mMenu.getItem(1).setVisible(true);
                setTitle(R.string.password_edit);

                mSiteEt.setEnabled(true);
                mUsernameEt.setEnabled(true);
                mPasswordEt.setEnabled(true);
                mCommentsEt.setEnabled(true);
                mCopyBtn.setVisibility(View.INVISIBLE);
                mRandomBtn.setVisibility(View.VISIBLE);
                break;
            case R.id.save_password:

                mPassword.site = mSiteEt.getText().toString();
                mPassword.username = mUsernameEt.getText().toString();
                mPassword.password = mPasswordEt.getText().toString();
                mPassword.comments = mCommentsEt.getText().toString();

                mPassword.timeStamp = System.currentTimeMillis();
                mPassword.abbreviatedUserName = mPassword.getAbbreviatedUserName();
                save(mPassword);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mHasTextChanged) {
            new AlertDialog.Builder(this)
                    .setMessage("要放弃本次修改吗？")
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

        if (!mHasTextChanged) {
            finish();
            return;
        }

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
                    appDatabase.passwordDao().update(password);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent();
                        intent.putExtra("password_edit", password);
                        PasswordEditActivity.this.setResult(RESULT_OK, intent);
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