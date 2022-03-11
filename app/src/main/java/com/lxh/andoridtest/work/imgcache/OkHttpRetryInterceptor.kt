//package com.egatee.jde.visit.imgcache
//
//import com.egatee.jde.tool.Utils
//import okhttp3.Interceptor
//import kotlin.Throws
//import okhttp3.Request
//import okhttp3.Response
//import java.io.IOException
//
///**
// * @author lxh
// * @date OkHttpRetryInterceptor$
// * @describe
// */
//class OkHttpRetryInterceptor(private val mMaxRetryCount: Int, private val mRetryInterval: Long) : Interceptor {
//    @Throws(IOException::class)
//    override fun intercept(chain: Interceptor.Chain): Response? {
//        val request = chain.request()
//        var response = doRequest(chain, request)
//        var retryNum = 1
//        if (response?.isSuccessful == true) {
//            return response
//        }
//        while (Utils.networkIsAvailable() && (response == null || !response.isSuccessful) && retryNum <= mMaxRetryCount) {
//
//            try {
//                Thread.sleep(mRetryInterval)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//            retryNum++
//            response?.close()
//            response = doRequest(chain, request)
//        }
//        return response
//    }
//
//    private fun doRequest(chain: Interceptor.Chain, request: Request): Response? {
//        try {
//            return chain.proceed(request)
//        } catch (e: IOException) {
//            e.printStackTrace()
//            throw e
//        }
//    }
//}