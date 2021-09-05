package com.wanhex.anxinpassword.add;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.wanhex.anxinpassword.MyApp;
import com.wanhex.anxinpassword.R;
import com.wanhex.anxinpassword.db.AppDatabase;
import com.wanhex.anxinpassword.db.Password;

public class PasswordAddActivity extends AppCompatActivity {

    private Password mPassword;

    private EditText mSiteEt;
    private EditText mUsernameEt;
    private EditText mPasswordEt;
    private EditText mCommentsEt;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.password_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_password:
                mPassword = new Password(
                        mSiteEt.getText().toString(),
                        mUsernameEt.getText().toString(),
                        mPasswordEt.getText().toString(),
                        mCommentsEt.getText().toString()
                );
                save(mPassword);
                break;
            default:
                break;
        }
        return true;
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
                MyApp app = (MyApp)getApplication();
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
}