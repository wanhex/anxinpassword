package com.wanhex.anxinpassword;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.wanhex.anxinpassword.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("anxinpassword");
    }

    private ActivityMainBinding binding;

    private List<Password> mPasswordList = new ArrayList<>();

    private RecyclerView mPasswordListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPasswordListView = binding.passwordList;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPasswordListView.setLayoutManager(layoutManager);
        PasswordAdapter adapter = new PasswordAdapter(mPasswordList);
        mPasswordListView.setAdapter(adapter);

        mPasswordList.add(new Password());
        mPasswordList.add(new Password());

        adapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public native String stringFromJNI();
}