package com.wanhex.anxinpassword.clouddisk;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wanhex.anxinpassword.settings.SettingsActivity;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BaiduYunSync {

    private static final String TAG = "BaiduYunSync";

    public interface OnBaiduUserInfoRecvListener {
        void onUserNameRecv(String baiduName);
        void onError(String errorMsg);
    }

    public interface OnFileUploadListener {
        void onSuccess();
        void onError(String result);
    }

    public static void getBaiduName(Context context, String accessToken, OnBaiduUserInfoRecvListener onBaiduUserInfoRecvListener) {
        String url = "https://pan.baidu.com/rest/2.0/xpan/nas?method=uinfo&access_token=xxxxxxxx";
        url = url.replace("xxxxxxxx", accessToken);
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
                onBaiduUserInfoRecvListener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String respStr = response.body().string();
                JSONObject jsonObject = (JSONObject) JSON.parse(respStr);

                String baiduName = jsonObject.getString("baidu_name");
                if (baiduName.isEmpty()) {
                    onBaiduUserInfoRecvListener.onError(respStr);
                    return;
                }

                onBaiduUserInfoRecvListener.onUserNameRecv(baiduName);
                BaiduNetDiskSettings.setBaiduName(context, baiduName);

            }
        });

    }

    public static void upload(String accessToken, String srcFilePath, String dstFilePath, OnFileUploadListener onFileUploadListener) {
        try {
            String uploadUrl = "https://c.pcs.baidu.com/rest/2.0/pcs/file?method=upload&access_token=" + accessToken + "&path=" + dstFilePath;
            OkHttpClient okHttpClient = new OkHttpClient();
            // file是要上传的文件 File()
            File file = new File(srcFilePath);
            RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            // 不仅可以支持传文件，还可以在传文件的同时，传参数
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("接收文件的参数名", "passwords", fileBody)
                    .build();
            Request request = new Request.Builder().url(uploadUrl).post(requestBody).build();
            Response response = okHttpClient.newCall(request).execute();
            if(!response.isSuccessful()) {
                // 一般会在这抛个异常
                onFileUploadListener.onError(response.message());
                return;
            }
            String result = response.body().string();
            JSONObject resultJsonObj = JSON.parseObject(result);

            if (!resultJsonObj.getString("fs_id").isEmpty()) {
                onFileUploadListener.onSuccess();
            } else {
                onFileUploadListener.onError(result);
            }

            response.body().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
