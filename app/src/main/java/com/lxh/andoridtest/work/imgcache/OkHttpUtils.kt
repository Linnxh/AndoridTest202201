//package com.egatee.jde.visit.imgcache
//
//
//import android.content.Context
//import android.os.Handler
//import android.text.TextUtils
//import android.util.Log
//import com.egatee.jde.configs.JdeConfig.client
//import com.egatee.jde.configs.JdeConfig.country
//import com.egatee.jde.configs.JdeConfig.dataFilterType
//import com.egatee.jde.configs.JdeConfig.userKey
//import com.egatee.jde.configs.StringConstant
//import com.egatee.jde.tool.LocaleUtils.EN_US
//import com.egatee.jde.tool.LocaleUtils.FR_FR
//import com.egatee.jde.tool.SharedPreferencesUtil.getString
//import okhttp3.*
//import okio.*
//import java.io.File
//import java.io.IOException
//import java.util.concurrent.TimeUnit
//
//
///**
// *   implementation 'com.squareup.okhttp3:okhttp:4.1.0' //  //网络请求框架 https://github.com/square/okhttp
// *   implementation 'com.squareup.okio:okio:2.3.0' //i/0 流处理 https://github.com/square/okio
// * 使用方式:
// * 1. 先初始化 OkHttpUtils（context）
// * 2. 直接使用 mOkHttpUtils.{你想要使用的get 、 post 或 文件上传方法 }
// *       如 var strJson = mOkHttpUtils.get_Sync("http://t.weather.sojson.com/api/weather/city/101030100")
//
// *
// * OkHttp再次封装 https://github.com/zskingking/OkHttpUtils
// *  * 1.OkhttpClient为网络请求的一个中心，它会管理连接池、缓存、SocketFactory、代理
// *   、各种超时时间、DNS、请求执行结果的分发等许多内容。
// * 2.Request：Request是一个HTTP请求体，比如请求方法GET/POST、URL、Header、Body
// *   请求的换粗策略等。
// * 3.Call：通过OkhttpClient和Request来创建Call，Call是一个Task，它会执行网络请求
// *   并且获得响应。这个Task可以通过execute()同步执行，阻塞至请求成功。也可以通过
// *   enqueue()异步执行，会将Call放入一个异步执行队列，由ExecutorService后台执行。
// */
//class OkHttpUtils(context: Context) {
//    companion object {
//
//        var mOkHttpUtils: OkHttpUtils? = null
//        fun getInstance(context: Context): OkHttpUtils? {
//            if (mOkHttpUtils == null) {
//                synchronized(OkHttpUtils::class.java) {
//                    if (mOkHttpUtils == null) {
//                        mOkHttpUtils = OkHttpUtils(context)
//                    }
//                }
//            }
//            return mOkHttpUtils
//        }
//
//        // 自定义委托实现单例,只能修改这个值一次.
////        var mOkHttpUtils: OkHttpUtils by DelegatesExt.notNullSingleValue<OkHttpUtils>();
//    }
//
//    init {
//        //初始化okhttp对象
//        mOkHttpUtils = into(context)
//    }
//
//    @Volatile
//    private lateinit var mOkHttpClient: OkHttpClient
//    private lateinit var mHandler: Handler
//
//    /**
//     * 初始化okhttp对象
//     * @param context: Context
//     * @return OkHttpUtils
//     */
//    private fun into(context: Context): OkHttpUtils {
//        //缓存的文件夹
//        val fileCache = File(context.getExternalCacheDir(), "response")
//        val cacheSize: Long = 10 * 1024 * 1024//缓存大小为10M
//        val cache = Cache(fileCache, cacheSize)
//        //进行OkHttpClient的一些设置
//        mOkHttpClient = OkHttpClient.Builder()
//            .connectTimeout(120, TimeUnit.SECONDS)
//            .readTimeout(60, TimeUnit.SECONDS)
//            .writeTimeout(60, TimeUnit.SECONDS)
//            .cache(cache)
//            .addInterceptor(DefaultContentTypeInterceptor("application/json"))
//            .addInterceptor(OkHttpRetryInterceptor(3, 1000))
//            .build()
//        mHandler = Handler()
//        return this
//    }
//
//
//    /**
//     * 异步文件参数混合上传
//     * @param  url: String
//     * @param params: Map<String, String>?
//     * @param  file: File
//     * @param  callback: OkHttpCallback 响应回调
//     * @param   progressListener: ProgressRequestBody 进度回调
//     */
//    open fun async_uploadFileAndParams(url: String, params: Map<String, String>?, file: File, callback: OkHttpCallback) {
//        val requestBody = RequestBody.//表示任意二进制流
//        create(MediaType.parse("image/png"), file)
//        //因为是文件参数混合上传，所以要分开构建
//        val builder = MultipartBody.Builder()
//        builder.setType(MultipartBody.FORM)
//        if (params != null) {
//            for ((key, value) in params) {
//                builder.addFormDataPart(key, value)
//            }
//        }
//        val multipartBody = builder
//            //key需要服务器提供，相当于键值对的键
//            .addFormDataPart("file", file.getName(), requestBody)
//            .build()
////        val countingRequestBody = ProgressRequestBody(multipartBody, progressListener)
//        val request = Request.Builder()
//            .url(url)
//            .post(multipartBody)
//            .build()
//        val call = mOkHttpClient.newCall(request)
//
//        call.enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                e.printStackTrace()
//                Log.e("OkhttpUtils", e.message.toString())
//                mHandler.post(Runnable { callback.onError(e) })
//            }
//
//            @Throws(IOException::class)
//            override fun onResponse(call: Call, response: Response) {
//                if (response.isSuccessful) {
//                    val string = response.body()?.string()
//                    System.out.println("===========" + string.toString())
//                    mHandler.post(Runnable { callback.onResponse(string ?: "") })
//                } else {
//                }
//            }
//        })
//
//    }
//
//    /**
//     * 参数添加到表单中
//     * @param param: Map<String, String>?
//     * @return RequestBody
//     */
//    fun buildParams(param: Map<String, String>?): RequestBody {
//        var params: Map<String, String>? = param
//        if (params == null) {
//            params = HashMap<String, String>()
//        }
//        val builder = FormBody.Builder()
//        for (entry in params.entries) {
//            val key = entry.key
//            var value: String? = entry.value
//            if (value == null) {
//                value = ""
//            }
//            builder.add(key, value)
//        }
//        return builder.build()
//    }
//
//    /**
//     * 根据tag取消单个请求
//     * 最终的取消时通过拦截器RetryAndFollowUpInterceptor进行的
//     * @param call: Call
//     */
//    open fun cancel(call: Call) {
//        //queuedCalls()代表所有准备运行的异步任务
//        for (dispatcherCal1 in mOkHttpClient.dispatcher().queuedCalls()) {
//            if (call.request().tag()!!.equals(call.request().tag())) {
//                call.cancel()
//            }
//        }
//        //runningCalls()代表所有正在运行的任务(包括同步和异步)
//        for (dispatcherCal1 in mOkHttpClient.dispatcher().runningCalls()) {
//            if (call.request().tag()!!.equals(call.request().tag())) {
//                call.cancel()
//            }
//        }
//    }
//
//    /**
//     * 取消全部请求
//     */
//    open fun cancelAll() {
//        mOkHttpClient.dispatcher().cancelAll()
//    }
//
//    interface OkHttpCallback {
//        fun onResponse(response: String)
//        fun onError(e: IOException)
//    }
//
//
//    /*文件加载监听*/
//    class ProgressRequestBody(
//        body: RequestBody,
//        listener: ProgressListener
//    ) : RequestBody() {
//        private var mListener: ProgressListener? = listener
//        private var mBody: RequestBody? = body
//        private var mProgressSink: ProgressSink? = null
//        private var mBufferedSink: BufferedSink? = null
//
//        override fun contentType(): MediaType? {
//            return mBody!!.contentType();
//        }
//
//        override fun writeTo(sink: BufferedSink) {
//            //将Sink重新构造
//            mProgressSink = ProgressSink(sink)
//            if (mBufferedSink == null) {
//                //创建输出流体系
//                mBufferedSink = Okio.buffer(mProgressSink);
////                mBufferedSink = mProgressSink!!.write()
////               mBufferedSink =  mProgressSink.
//                //   报错
//            }
//            //进行流输出操作
//            mBody!!.writeTo(mBufferedSink!!);
//            mBufferedSink!!.flush(); }
//
//        override fun contentLength(): Long {
//            try {
//                return mBody!!.contentLength()
//            } catch (e: IOException) {
//                return -1
//            }
//
//        }
//
//        internal inner class ProgressSink(delegate: Sink) : ForwardingSink(delegate) {
//            var byteWrite: Long = 0//当前写入的字节
//
//            @Throws(IOException::class)
//            override fun write(source: Buffer, byteCount: Long) {
//                //必须执行父类方法，否则无法上传
//                super.write(source, byteCount)
//                byteWrite += byteCount
//                if (mListener != null) {
//                    //更新进度
//                    mListener!!.onProgress(byteWrite, contentLength())
//                }
//            }
//        }
//
//        interface ProgressListener {
//            fun onProgress(byteWrite: Long, contentLength: Long)
//        }
//    }
//
//
//    //自定义拦截器，设置header
//    class DefaultContentTypeInterceptor(private val contentType: String) : Interceptor {
//        @Throws(IOException::class)
//        override fun intercept(chain: Interceptor.Chain): Response {
//            var authorizetaon = ""
//            authorizetaon = if (TextUtils.isEmpty(userKey)) {
//                StringConstant.LOGIN_AND_REGISTER_AUTHORIZETAON
//            } else {
//                StringConstant.LOGIN_AFTER_AUTHORIZETAON + userKey
//            }
//            var language = getString(StringConstant.JDE_LANGUAGE, EN_US)
//            language = if (language == EN_US) {
//                EN_US + ",en"
//            } else if (language == FR_FR) {
//                FR_FR + ",fr"
//            } else {
//                EN_US + ",en"
//            }
//            val originalRequest = chain.request()
//            val requestWithUserAgent = originalRequest
//                .newBuilder()
//                .header("Content-Type", contentType)
//                .header("SYSTEM_TYPE", StringConstant.HEAD_SYSTEM_TYPE)
//                .header("access_token", userKey ?: "")
//                .header("Authorization", authorizetaon)
//                .header("COUNTRY_ID", client)
//                .header("COUNTRY_CODE", country)
//                .header("Accept-Language", language)
//                .header("DATA_FILTER_TYPE", dataFilterType.toString() + "")
//                .build()
//            return chain.proceed(requestWithUserAgent)
//        }
//    }
//
//
//}
//
