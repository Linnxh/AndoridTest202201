package com.lxh.andoridtest.work.imgcache
import com.egatee.jde.visit.imgcache.OkHttpBaseResponseBean
import com.egatee.jde.visit.imgcache.VisitImgConfig
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit


private val mExecutor = Executors.newSingleThreadScheduledExecutor()
private var mFuture: ScheduledFuture<*>? = null

private fun getLooper() {
//创建并开启定时任务
    mFuture = mExecutor.scheduleWithFixedDelay({
        //定时任务
    }, 1, 1, TimeUnit.MINUTES)
}


private fun closeLooper() {
//关闭定时任务
    mFuture?.run {
        if (!isCancelled) cancel(true)
        null
    }
}


class ImageUpload {
    companion object {
        fun uploadImage(localPath: String) {
            val list = ArrayList<File>()
            list.add(File(localPath))
            val name = localPath.substring(localPath.lastIndexOf("/") + 1, localPath.length)
            val nameList = name.split("-").toMutableList()
            val map = mapOf("type" to nameList[0], "visitClientId" to nameList[1], "localPath" to localPath)
//            OkHttpUtils.mOkHttpUtils?.async_uploadFileAndParams(JdeConfig.apiUrl + "/api/salesman/visitDynamicLog/updateVisitImgNew",
//                map, File(localPath), object : OkHttpUtils.OkHttpCallback {
//                    override fun onResponse(response: String) {
//                        val respData: OkHttpBaseResponseBean<*> = JSON.parseObject(response, OkHttpBaseResponseBean::class.java)
//                        if (respData.code != 1) {
//                        }
//                        VisitImgConfig.setImgNetWorkUrl(localPath, respData.data.toString())
//                        EventBus.getDefault().post(StoreFrontImgEvent())
//                        Log.i("lxh", "图片回调结果=============$respData==本机地址==$localPath")
//
//                    }
//
//                    override fun onError(e: IOException) {
//                        Log.i("lxh", "onError=============$e")
//
//                    }
//
//                }

//            )

        }

    }

}






