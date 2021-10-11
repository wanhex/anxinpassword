package com.wanhex.anxinpassword.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.oauth.sdk.auth.AuthInfo;
import com.baidu.oauth.sdk.auth.BdOauthSdk;
import com.baidu.oauth.sdk.auth.BdSsoHandler;
import com.baidu.oauth.sdk.callback.BdOauthCallback;
import com.baidu.oauth.sdk.dto.BdOauthDTO;
import com.baidu.oauth.sdk.result.BdOauthResult;
import com.wanhex.anxinpassword.R;

import java.util.UUID;

public class SettingsActivity extends AppCompatActivity {

    private TextView mAboutTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.settings);
        setContentView(R.layout.activity_settings);

        mAboutTv = findViewById(R.id.tv_about);

        String about = AppUtils.getAppName(this);
        about += " v";
        about += AppUtils.getVersionName(this);
        mAboutTv.setHint(about);

        initSdk();
    }

    /**
     * 初始化SDK
     */
    private void initSdk() {
        String redirectUrl = "https://passport.baidu.com";
        String scope = "basic";
        String appKey = "XXIlWUS6GYbkDIG0OAkGzhEt";
        AuthInfo authInfo = new AuthInfo(this, appKey, redirectUrl, scope);
        BdOauthSdk.init(authInfo);
        authInfo.isDebug(true);
    }

    BdSsoHandler bdSsoHandler;
    public void onBaiduYunBtnClicked(View view) {
//        String appKey = "Ahbwqkr2m3kQzX3tUD280OOS6Zg33q6H";
//        String redirectUrl = "oob";
//        String scope = "basic,netdisk";
//        AuthInfo authInfo = new AuthInfo(this, appKey, redirectUrl, scope);

        bdSsoHandler = new BdSsoHandler(this);

        BdOauthDTO bdOauthDTO = new BdOauthDTO();
        bdOauthDTO.oauthType = BdOauthDTO.OAUTH_TYPE_BOTH;
        bdOauthDTO.state = UUID.randomUUID().toString();

        bdSsoHandler.authorize(bdOauthDTO, new BdOauthCallback() {
            @Override
            public void onSuccess(BdOauthResult result) {

                Toast.makeText(SettingsActivity.this,
                        "res_code: " + result.getResultCode() + " code = " + result.getCode() + " state = " + result.getState(),
                        Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(BdOauthResult result) {
                Toast.makeText(SettingsActivity.this, "result code = " + result.getResultCode() + " msg = " + result.getResultMsg(),
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void onAboutBtnClicked(View view) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  因为sdk通过startActivityForResult启动授权页面，所以需要产品线在调用activity接收返回
        if (bdSsoHandler != null) {
            bdSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}