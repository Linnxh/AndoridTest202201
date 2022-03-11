//package com.lxh.andoridtest.work.imgcache;
//
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.egatee.jde.base.BaseResponseBean;
//import com.egatee.jde.configs.JdeConfig;
//import com.egatee.jde.configs.StringConstant;
//import com.egatee.jde.tool.LocaleUtils;
//import com.egatee.jde.tool.SharedPreferencesUtil;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Cookie;
//import okhttp3.Headers;
//import okhttp3.HttpUrl;
//import okhttp3.Interceptor;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//import okio.Buffer;
//
//public class HttpNetwork {
//    private static final String TAG = "HttpNetwork";
//    public static final String USER_AGENT = "User-Agent";
//    private static final boolean DEBUG = false;
//    public static OkHttpClient client = null;
//    public final static HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();
//
//    public static OkHttpClient getClient() {
//        return client;
//    }
//
//    static {
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.connectTimeout(120, TimeUnit.SECONDS);
//        builder.writeTimeout(120, TimeUnit.SECONDS);
//        builder.readTimeout(120, TimeUnit.SECONDS);
////        builder.addInterceptor(new RetryIntercepter(3));
//        builder.addInterceptor(new DefaultContentTypeInterceptor("application/json"));
//        if (DEBUG) {
//            builder.addInterceptor(new LogInterceptor());
//        }
//        client = builder.build();
//        if (DEBUG) {
//            int connectTimeoutMillis = client.connectTimeoutMillis();
//            int writeTimeoutMillis = client.writeTimeoutMillis();
//            int readTimeoutMillis = client.readTimeoutMillis();
//            Log.i(TAG, "connectTimeoutMillis: " + connectTimeoutMillis);
//            Log.i(TAG, "writeTimeoutMillis: " + writeTimeoutMillis);
//            Log.i(TAG, "readTimeoutMillis: " + readTimeoutMillis);
//        }
//    }
//
//    //多个文件上传,Filelist
//    public static void getFilesRequest(String url, List<File> files, Map<String, String> maps, HttpCallback callback) {
//        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//        if (maps == null) {
//            if (files.size() > 0) {
//                for (int i = 0; i < files.size(); i++) {
//                    builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\";filename=\"file.jpg\""),
//                            RequestBody.create(MediaType.parse("image/png"), files.get(i))
//                    ).build();
//                }
//            }
//        } else {
//            for (String key : maps.keySet()) {
//                String str = maps.get(key);
//                builder.addFormDataPart(key, str);
//            }
//            if (files.size() > 0) {
//                for (int j = 0; j < files.size(); j++) {
//                    long fileSize = files.get(j).length();
//                    builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\";filename=\"file.jpg\";filesize=" + fileSize),
//                            RequestBody.create(MediaType.parse("image/png"), files.get(j))
//                    );
//                }
//            }
//        }
//        RequestBody body = builder.build();
//        Request request = null;
//        try {
//            request = new Request.Builder()
////                    .header("os", "android;")
//                    .url(url)
//                    .post(body)
//                    .build();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        if (request != null) {
//            Call call = client.newCall(request);
//            call.enqueue(getResponseCallback(callback));
//        }
//    }
//
//    public static void asyncPostJson(String url, String json, HttpCallback callback) {
//        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
//        Request request = null;
//        try {
//            request = new Request.Builder()
//                    .url(url)
//                    .post(requestBody)
//                    .build();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (request != null) {
//            Call call = client.newCall(request);
//            //call.cancel();
//            call.enqueue(getResponseCallback(callback));
//        }
//    }
//
//    private static Callback getResponseCallback(final HttpCallback callback) {
//        return new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                if (callback != null) {
//                    try {
//                        callback.onFailure(-1, e);
//                    } catch (Exception exc) {
//                        exc.printStackTrace();
//                    }
//                }
//                e.printStackTrace();
//                if (call != null) {
//                    boolean isCanceled = call.isCanceled();
//                    if (!isCanceled) {
//                        HttpUrl url = call.request().url();
//                        Log.e(TAG, "http fail,url:" + url);
//                    }
//                }
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (!response.isSuccessful()) {// 请求失败
//                    if (callback != null) {
//                        try {
//                            callback.onFailure(response.code(), null);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (call != null) {
//                        boolean isCanceled = call.isCanceled();
//                        if (!isCanceled) {
//                            HttpUrl url = call.request().url();
//                        }
//                    }
//                } else {// 请求成功
//                    String bodyStr = response.body().string();
//                    if (DEBUG) {
//                        Log.i(TAG, bodyStr);
//                        if (call != null) {
//                            HttpUrl url = call.request().url();
//                            Log.d(TAG, "http ok,url:" + url + ":" + bodyStr);
//                        }
//                    }
//                    if (callback != null) {
//                        try {
//                            OkHttpBaseResponseBean responseBean = com.alibaba.fastjson.JSON.parseObject(bodyStr, OkHttpBaseResponseBean.class);
//                            BaseResponseBean responseBeanss = com.alibaba.fastjson.JSON.parseObject(bodyStr, BaseResponseBean.class);
//                            if (responseBean.getCode() == -1 || responseBean.getCode() == 500) {
//                            } else {
//                                callback.onSuccess(bodyStr);
//                            }
//                        } catch (Exception exc) {
//                            exc.printStackTrace();
//
//                        }
//                    }
//                }
//
//
//            }
//        };
//    }
//
//    public static class LogInterceptor implements Interceptor {
//        @Override
//        public Response intercept(Chain chain) throws IOException {
//            long t1 = System.nanoTime();
//            Response response = chain.proceed(chain.request());
//            long t2 = System.nanoTime();
//            okhttp3.MediaType mediaType = response.body().contentType();
//            String content = response.body().string();
//            String oldParamsJson = "";
//            if (response.request().body() != null) {
//                Buffer buffer = null;
//                try {
//                    buffer = new Buffer();
//                    response.request().body().writeTo(buffer);
//                    oldParamsJson = buffer.readUtf8();
//                    if (buffer != null) {
//                        buffer.close();
//                    }
//                } catch (Exception e) {
//                    if (buffer != null) {
//                        buffer.close();
//                    }
//                    e.printStackTrace();
//                } finally {
//                    if (buffer != null) {
//                        buffer.close();
//                    }
//                }
//            }
//            if (DEBUG && !mediaType.toString().contains("text/html") && !mediaType.toString().contains("image"))
//                Log.e("debug", String.format("%s%n%s%n%s%n%s%s%n%s",
//                        "请求地址:" + response.request().url()
//                        , "请求耗时:" + (t2 - t1) / 1e6d
//                        , "请求参数:" + oldParamsJson
//                        , "请求头部:" + response.request().headers().toString()
//                        , "请求类型:" + mediaType
//                        , "回调内容:" + content));
//            return response.newBuilder()
//                    .body(okhttp3.ResponseBody.create(mediaType, content))
//                    .build();
//        }
//    }
//
//    //自定义拦截器，设置header
//    public static class DefaultContentTypeInterceptor implements Interceptor {
//        public int maxRetry = 3;//最大重试次数
//        private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）
//        private String contentType;
//
//        public DefaultContentTypeInterceptor(String contentType) {
//            this.contentType = contentType;
//        }
//
//        public Response intercept(Interceptor.Chain chain) throws IOException {
//
//
//            String authorizetaon = "";
//            if (TextUtils.isEmpty(JdeConfig.INSTANCE.getUserKey())) {
//                authorizetaon = StringConstant.LOGIN_AND_REGISTER_AUTHORIZETAON;
//            } else {
//                authorizetaon = StringConstant.LOGIN_AFTER_AUTHORIZETAON + JdeConfig.INSTANCE.getUserKey();
//            }
//            String language = SharedPreferencesUtil.INSTANCE.getString(StringConstant.JDE_LANGUAGE, LocaleUtils.INSTANCE.getEN_US());
//            if (language.equals(LocaleUtils.INSTANCE.getEN_US())) {
//                language = LocaleUtils.INSTANCE.getEN_US() + (",en");
//            } else if (language.equals(LocaleUtils.INSTANCE.getFR_FR())) {
//                language = LocaleUtils.INSTANCE.getFR_FR() + (",fr");
//            } else {
//                language = LocaleUtils.INSTANCE.getEN_US() + (",en");
//            }
//
//            System.out.println("retryNum=" + retryNum);
//            Request originalRequest = chain.request();
//            Request requestWithUserAgent = originalRequest
//                    .newBuilder()
//                    .header("Content-Type", contentType)
//                    .header("SYSTEM_TYPE", StringConstant.HEAD_SYSTEM_TYPE)
//                    .header("access_token", JdeConfig.INSTANCE.getUserKey())
//                    .header("Authorization", authorizetaon)
//                    .header("COUNTRY_ID", JdeConfig.INSTANCE.getClient())
//                    .header("COUNTRY_CODE", JdeConfig.INSTANCE.getCountry())
//                    .header("Accept-Language", language)
//                    .header("DATA_FILTER_TYPE", JdeConfig.INSTANCE.getDataFilterType() + "")
//                    .build();
//            Response response = chain.proceed(requestWithUserAgent);
//            while (!response.isSuccessful() && retryNum < maxRetry) {
//                retryNum++;
//                System.out.println("retryNum=" + retryNum);
//                response = chain.proceed(originalRequest);
//            }
//            return response;
//
////            return chain.proceed(requestWithUserAgent);
//        }
//    }
//
//    public interface HttpCallback {
//        void onFailure(int errorCode, Exception e);
//
//        void onSuccess(String body);
//    }
//
//}
