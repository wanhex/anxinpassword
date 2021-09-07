package com.wanhex.anxinpassword.add;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.wanhex.anxinpassword.MyApp;
import com.wanhex.anxinpassword.R;
import com.wanhex.anxinpassword.db.AppDatabase;
import com.wanhex.anxinpassword.db.Password;

import java.util.Random;

public class PasswordAddActivity extends AppCompatActivity {

    private Password mPassword;

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
                mPasswordEt.setText(genRandomNum());
            }
        });
    }

    private String genRandomNum(){
        int  maxNum = 36;
        int i;
        int count = 0;
        char[] str0 = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z'};
        char[] str1 = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W'};
        char[] str2 = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        char[] str3 = { '!', '@', '#', '$', '%', '^', '&', '*', '(', ')' };

        StringBuilder pwd = new StringBuilder("");
        Random r = new Random();

        for (int j=0; j<2; j++) {

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
                mPassword.timeStamp = System.currentTimeMillis();
                mPassword.abbreviatedUserName = mPassword.getAbbreviatedUserName();
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