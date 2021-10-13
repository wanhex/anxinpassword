package com.wanhex.anxinpassword.clouddisk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.wanhex.anxinpassword.R;

public class BaiduOAuthActivity extends AppCompatActivity {

    WebView mWebView;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.baidu_login);
        setContentView(R.layout.activity_baidu_oauth);

        mWebView = findViewById(R.id.wv);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;// 返回false
            }
        });
        WebSettings webSettings = mWebView.getSettings();
        // 让WebView能够执行javaScript
        webSettings.setJavaScriptEnabled(true);
        // 让JavaScript可以自动打开windows
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 设置缓存
        webSettings.setAppCacheEnabled(true);
        // 设置缓存模式,一共有四种模式
        webSettings.setCacheMode(webSettings.LOAD_CACHE_ELSE_NETWORK);
        // 设置缓存路径
//        webSettings.setAppCachePath("");
        // 支持缩放(适配到当前屏幕)
        webSettings.setSupportZoom(true);
        // 将图片调整到合适的大小
        webSettings.setUseWideViewPort(true);
        // 支持内容重新布局,一共有四种方式
        // 默认的是NARROW_COLUMNS
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 设置可以被显示的屏幕控制
        webSettings.setDisplayZoomControls(true);
        // 设置默认字体大小
//        webSettings.setDefaultFontSize(12);


        onBaiduYunBtnClicked(null);
    }

    Runnable mDetectLoginResultRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("xxx", mWebView.getUrl());
            String currentUrl = mWebView.getUrl();
            if (currentUrl.contains("login_success")) {
                Intent intent = new Intent();
                String[] params = currentUrl.split("&");
                for (int i=0; i<params.length; i++) {
                    if (params[i].contains("access_token")) {
                        intent.putExtra("access_token", params[i].substring(13));
                    }
                }
                setResult(RESULT_OK, intent);
                BaiduOAuthActivity.this.finish();
            } else {
                mHandler.postDelayed(mDetectLoginResultRunnable, 500);
            }
        }
    };

    public void onBaiduYunBtnClicked(View view) {

        mWebView.setVisibility(View.VISIBLE);
        mWebView.loadUrl("https://openapi.baidu.com/oauth/2.0/authorize?response_type=token&display=mobile&client_id=Ahbwqkr2m3kQzX3tUD280OOS6Zg33q6H&redirect_uri=oob&scope=netdisk");

        mHandler.postDelayed(mDetectLoginResultRunnable, 200);
    }
}