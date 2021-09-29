package com.wanhex.anxinpassword;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.wanhex.anxinpassword.add.PasswordAddActivity;
import com.wanhex.anxinpassword.databinding.ActivityMainBinding;
import com.wanhex.anxinpassword.db.AppDatabase;
import com.wanhex.anxinpassword.db.Password;
import com.wanhex.anxinpassword.edit.PasswordEditActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("anxinpassword");
    }

    private ActivityMainBinding binding;
    private ActivityResultLauncher mActivityResultLauncher;
    private Handler mHandler = new Handler();

    private List<Password> mPasswordList = new ArrayList<>();
    PasswordAdapter mAdapter;

    private RecyclerView mPasswordListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPasswordListView = binding.passwordList;

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mPasswordListView.setLayoutManager(layoutManager);
        mAdapter = new PasswordAdapter(mPasswordList);
        mPasswordListView.setAdapter(mAdapter);

        loadPasswords();

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
                    return;
                }
                Password passwordEdit = (Password) data.getExtras().get("password_edit");
                if (passwordEdit != null) {
                    for (int i=0; i<mPasswordList.size(); i++) {
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
                }

            }
        });

    }

    private void loadPasswords() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                MyApp app = (MyApp)getApplication();
                AppDatabase appDatabase = app.getPasswordDb();
                List<Password> passwordList = appDatabase.passwordDao().getAll();
                mPasswordList.addAll(passwordList);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
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
        switch(item.getItemId()){
            case R.id.add_password:
                mActivityResultLauncher.launch(new Intent(this, PasswordAddActivity.class));
                break;
            default:
                break;
        }
        return true;
    }

    public native String stringFromJNI();
}