package com.wanhex.anxinpassword.add;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.wanhex.anxinpassword.R;

public class PasswordAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.password_add);
        setContentView(R.layout.activity_password_add);
    }
}