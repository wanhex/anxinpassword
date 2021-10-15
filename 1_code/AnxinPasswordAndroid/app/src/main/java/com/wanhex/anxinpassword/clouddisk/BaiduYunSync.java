package com.wanhex.anxinpassword.clouddisk;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wanhex.anxinpassword.MyApp;
import com.wanhex.anxinpassword.cipher.AESEncrypt;
import com.wanhex.anxinpassword.db.AppDatabase;
import com.wanhex.anxinpassword.db.Password;
import com.wanhex.anxinpassword.settings.AppSettings;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    public interface OnFileDownListener {
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
            if (!response.isSuccessful()) {
                // 一般会在这抛个异常
                file.delete();
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
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void download(Context context, String accessToken, String dstFilePath, OnFileDownListener onFileDownListener) {

        String url = "https://pan.baidu.com/rest/2.0/xpan/file?method=list&dir=/apps/安心密码&order=time&start=0&limit=1&web=web&folder=0&access_token=xxxxxxxx&desc=1";
        url = url.replace("xxxxxxxx", accessToken);

        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                onFileDownListener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String respStr = response.body().string();

                JSONObject jsonObject = (JSONObject) JSON.parse(respStr);
                //{"errno":0,"guid_info":"","list":[{"tkbind_id":0,"owner_type":0,"category":1,"real_category":"","fs_id":825650716744180,"server_mtime":1634193901,"oper_id":0,"server_ctime":1634193901,"thumbs":{"icon":"https:\/\/thumbnail0.baidupcs.com\/thumbnail\/938073dcdh7d33f7a2a3efd415b67b66?fid=923454652-250528-825650716744180&rt=pr&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-IIIciWw%2fppTmFcLE7wywg62qg7M%3d&expires=8h&chkbd=0&chkv=0&dp-logid=8953608799200592126&dp-callid=0&time=1634194800&size=c60_u60&quality=100&vuk=923454652&ft=video","url3":"https:\/\/thumbnail0.baidupcs.com\/thumbnail\/938073dcdh7d33f7a2a3efd415b67b66?fid=923454652-250528-825650716744180&rt=pr&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-IIIciWw%2fppTmFcLE7wywg62qg7M%3d&expires=8h&chkbd=0&chkv=0&dp-logid=8953608799200592126&dp-callid=0&time=1634194800&size=c850_u580&quality=100&vuk=923454652&ft=video","url2":"https:\/\/thumbnail0.baidupcs.com\/thumbnail\/938073dcdh7d33f7a2a3efd415b67b66?fid=923454652-250528-825650716744180&rt=pr&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-IIIciWw%2fppTmFcLE7wywg62qg7M%3d&expires=8h&chkbd=0&chkv=0&dp-logid=8953608799200592126&dp-callid=0&time=1634194800&size=c360_u270&quality=100&vuk=923454652&ft=video","url1":"https:\/\/thumbnail0.baidupcs.com\/thumbnail\/938073dcdh7d33f7a2a3efd415b67b66?fid=923454652-250528-825650716744180&rt=pr&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-IIIciWw%2fppTmFcLE7wywg62qg7M%3d&expires=8h&chkbd=0&chkv=0&dp-logid=8953608799200592126&dp-callid=0&time=1634194800&size=c140_u90&quality=100&vuk=923454652&ft=video"},"wpfile":0,"local_mtime":1634193901,"size":300,"extent_tinyint7":0,"path":"\/apps\/\u5b89\u5fc3\u5bc6\u7801\/passwords_2021_10_14_14_44_59_default_sync_passwd.dat","share":0,"server_atime":0,"pl":0,"local_ctime":1634193901,"server_filename":"passwords_2021_10_14_14_44_59_default_sync_passwd.dat","md5":"938073dcdh7d33f7a2a3efd415b67b66","owner_id":0,"unlist":0,"isdir":0}],"request_id":8953608799200592126,"guid":0}
                if (jsonObject.getInteger("errno") != 0) {
                    onFileDownListener.onError(respStr);
                    return;
                }

                JSONArray fileListJson = jsonObject.getJSONArray("list");
                if (fileListJson.size() == 0) {
                    onFileDownListener.onError("百度云盘中未发现备份记录!!!");
                    return;
                }

                JSONObject fileJsonObj = fileListJson.getJSONObject(0);
                String fsId = fileJsonObj.getString("fs_id");
                getFileMetaData(context, accessToken, fsId, dstFilePath, onFileDownListener);

            }
        });
    }

    public static void getFileMetaData(Context context, String accessToken, String fsId, String dstFilePath, OnFileDownListener onFileDownListener) {
        String url = "http://pan.baidu.com/rest/2.0/xpan/multimedia?access_token=xxxxxxxx&method=filemetas&fsids=[fsiddddddddddddddddddd]&thumb=1&dlink=1&extra=1";
        url = url.replace("xxxxxxxx", accessToken);
        url = url.replace("fsiddddddddddddddddddd", fsId);

        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                onFileDownListener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String respStr = response.body().string();

                JSONObject jsonObject = (JSONObject) JSON.parse(respStr);
                //{"errmsg":"succ","errno":0,"list":[{"category":1,"dlink":"
                // https://d.pcs.baidu.com/file/938073dcdh7d33f7a2a3efd415b67b66?fid=923454652-250528-825650716744180\u0026rt=pr\u0026sign=FDtAERV-DCb740ccc5511e5e8fedcff06b081203-jLx9SfWB2OizbGgewxvrZiNR%2F5Y%3D\u0026expires=8h\u0026chkbd=0\u0026chkv=2\u0026dp-logid=391512313784003295\u0026dp-callid=0\u0026dstime=1634196018\u0026r=439675629\u0026origin_appid=24962975\u0026file_type=0
                // ","filename":"passwords_2021_10_14_14_44_59_default_sync_passwd.dat","fs_id":825650716744180,"isdir":0,"md5":"938073dcdh7d33f7a2a3efd415b67b66","oper_id":0,"path":"/apps/安心密码/passwords_2021_10_14_14_44_59_default_sync_passwd.dat","server_ctime":1634193901,"server_mtime":1634193901,"size":300,"thumbs":{"icon":"https://thumbnail0.baidupcs.com/thumbnail/938073dcdh7d33f7a2a3efd415b67b66?fid=923454652-250528-825650716744180\u0026rt=pr\u0026sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-IIIciWw%2FppTmFcLE7wywg62qg7M%3D\u0026expires=8h\u0026chkbd=0\u0026chkv=0\u0026dp-logid=391512203844069487\u0026dp-callid=0\u0026time=1634194800\u0026size=c60_u60\u0026quality=100\u0026vuk=923454652\u0026ft=video","url1":"https://thumbnail0.baidupcs.com/thumbnail/938073dcdh7d33f7a2a3efd415b67b66?fid=923454652-250528-825650716744180\u0026rt=pr\u0026sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-IIIciWw%2FppTmFcLE7wywg62qg7M%3D\u0026expires=8h\u0026chkbd=0\u0026chkv=0\u0026dp-logid=391512203844069487\u0026dp-callid=0\u0026time=1634194800\u0026size=c140_u90\u0026quality=100\u0026vuk=923454652\u0026ft=video","url2":"https://thumbnail0.baidupcs.com/thumbnail/938073dcdh7d33f7a2a3efd415b67b66?fid=923454652-250528-825650716744180\u0026rt=pr\u0026sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-IIIciWw%2FppTmFcLE7wywg62qg7M%3D\u0026expires=8h\u0026chkbd=0\u0026chkv=0\u0026dp-logid=391512203844069487\u0026dp-callid=0\u0026time=1634194800\u0026size=c360_u270\u0026quality=100\u0026vuk=923454652\u0026ft=video","url3":"https://thumbnail0.baidupcs.com/thumbnail/938073dcdh7d33f7a2a3efd415b67b66?fid=923454652-250528-825650716744180\u0026rt=pr\u0026sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-IIIciWw%2FppTmFcLE7wywg62qg7M%3D\u0026expires=8h\u0026chkbd=0\u0026chkv=0\u0026dp-logid=391512203844069487\u0026dp-callid=0\u0026time=1634194800\u0026size=c850_u580\u0026quality=100\u0026vuk=923454652\u0026ft=video","url4":"https://thumbnail0.baidupcs.com/thumbnail/938073dcdh7d33f7a2a3efd415b67b66?fid=923454652-250528-825650716744180\u0026rt=pr\u0026sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-IIIciWw%2FppTmFcLE7wywg62qg7M%3D\u0026expires=8h\u0026chkbd=0\u0026chkv=0\u0026dp-logid=391512203844069487\u0026dp-callid=0\u0026time=1634194800\u0026size=c165_u165\u0026quality=100\u0026vuk=923454652\u0026ft=video"}}],"names":{},"request_id":"8953812940778447202"}

                if (jsonObject.getInteger("errno") != 0) {
                    onFileDownListener.onError(respStr);
                    return;
                }

                JSONArray fileListJson = jsonObject.getJSONArray("list");
                if (fileListJson.size() == 0) {
                    onFileDownListener.onError("百度云盘中未发现备份记录!!!!");
                    return;
                }

                JSONObject fileJsonObj = fileListJson.getJSONObject(0);
                String dlink = fileJsonObj.getString("dlink");
                String fileName = fileJsonObj.getString("filename");
                downLoadByDLink(context, accessToken, dlink, fileName, dstFilePath, onFileDownListener);
            }
        });
    }

    public static void downLoadByDLink(Context context, String accessToken, String dlink, String fileName, String dstFilePath, OnFileDownListener onFileDownListener) {
        String url = dlink;
        url += "&access_token=" + accessToken;

        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                onFileDownListener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String respStr = response.body().string();

                // {"error_code":31045,"error_msg":"user not exists","request_id":8953955110448315753}
                if (respStr.contains("error_code")) {
                    onFileDownListener.onError(respStr);
                    return;
                }

                String syncPassword = AppSettings.getSyncPassword(context);

                String passwords = AESEncrypt.decrypt(respStr, syncPassword);

                List<Password> passwordList = JSON.parseArray(passwords, Password.class);

                MyApp app = (MyApp) ((Activity) context).getApplication();
                AppDatabase appDatabase = app.getPasswordDb();
                List<Password> passwordListInDb = appDatabase.passwordDao().getAll();

                assert passwordList != null;
                for (Password password : passwordList) {
                    boolean foundInDb = false;
                    for (Password passwordInDb : passwordListInDb) {
                        if (password.site.equals(passwordInDb.site)
                                && password.username.equals(passwordInDb.username)
                                && password.password.equals(passwordInDb.password)
                                && password.comments.equals(passwordInDb.comments)) {
                            foundInDb = true;
                        }
                    }
                    if (!foundInDb) {
                        try {
                            appDatabase.passwordDao().insert(password);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                onFileDownListener.onSuccess();
            }
        });
    }
}
