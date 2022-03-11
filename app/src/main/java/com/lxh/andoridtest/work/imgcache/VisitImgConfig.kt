package com.egatee.jde.visit.imgcache

import com.lxh.andoridtest.util.SharedPreferencesUtil
import com.lxh.andoridtest.work.imgcache.ImagePickData
import com.lxh.andoridtest.work.imgcache.ImageUpload
import com.lxh.andoridtest.work.imgcache.ImgData
import com.lxh.andoridtest.work.imgcache.VisitProcessImgCache
import java.io.File

object VisitImgConfig {

    var nowUploadPath: String = ""

    // 拜访流程节点图片缓存
    var visitProcessImgCacheList: MutableList<VisitProcessImgCache> = mutableListOf()

    // 1：门头照 2：陈列检查  3：总结
    var IMG_TYPE_STOREFRONT = 1;
    var IMG_TYPE_CHECKDISPLAY = 2;
    var IMG_TYPE_SUMMARY = 3;

    /**
     * 图片上传成功后将图片参数移除
     *  localPath: type-visitId-buyerCheckId-buyerUserId-isUpload
     */
    fun setImgNetWorkUrl(localPath: String, netWorkUrl: String) {
        if (visitProcessImgCacheList.isNullOrEmpty()) {
            return
        }
        val name = localPath.substring(localPath.lastIndexOf("/") + 1, localPath.length)
        val visitId = name.split("-")[1]
        val type = name.split("-")[0].toInt()
        val visitBeanList = visitProcessImgCacheList.filter { a -> a.visitClientId == visitId.toLong() }
        if (visitBeanList.isNullOrEmpty()) {
            return
        }
        when (type) {
            IMG_TYPE_STOREFRONT -> {
                val find = visitBeanList[0].imgdata.storeFront.find { s -> s.localPath == localPath }
                if (find != null) {
                    visitBeanList[0].imgdata.storeFront.remove(find)
                    setPreferenceImgCacheMap(visitProcessImgCacheList, null)
                }
            }
            IMG_TYPE_CHECKDISPLAY -> {
                val find = visitBeanList[0].imgdata.checkDisplay.find { s -> s.localPath == localPath }
                if (find != null) {
                    visitBeanList[0].imgdata.checkDisplay.remove(find)
                    setPreferenceImgCacheMap(visitProcessImgCacheList, null)
                }
            }
            IMG_TYPE_SUMMARY -> {
                val find = visitBeanList[0].imgdata.summary.find { s -> s.localPath == localPath }
                if (find != null) {
                    visitBeanList[0].imgdata.summary.remove(find)
                    setPreferenceImgCacheMap(visitProcessImgCacheList, null)
                }
            }
        }
    }

    /**
     * 设置缓存实体 ===========>>>> 保存图片信息  + 循环异步操作
     * type 1：门头照 2：陈列检查  3：总结
     */
    fun setVisitImgCache(visitId: Long, imgList: MutableList<ImagePickData>, type: Int) {
        /******** 保存  *******/
        if (visitProcessImgCacheList.isNullOrEmpty()) {
            when (type) {
                IMG_TYPE_STOREFRONT -> visitProcessImgCacheList.add(VisitProcessImgCache(visitId, ImgData(imgList, mutableListOf(), mutableListOf())))
                IMG_TYPE_CHECKDISPLAY -> visitProcessImgCacheList.add(VisitProcessImgCache(visitId, ImgData(mutableListOf(), imgList, mutableListOf())))
                IMG_TYPE_SUMMARY -> visitProcessImgCacheList.add(VisitProcessImgCache(visitId, ImgData(mutableListOf(), mutableListOf(), imgList)))
            }
        } else {
            val filter = visitProcessImgCacheList.filter { a -> a.visitClientId == visitId }
            if (!filter.isNullOrEmpty()) {
                // 已有
                when (type) {
                    IMG_TYPE_STOREFRONT -> filter[0].imgdata.storeFront = imgList
                    IMG_TYPE_CHECKDISPLAY -> filter[0].imgdata.checkDisplay = imgList
                    IMG_TYPE_SUMMARY -> filter[0].imgdata.summary = imgList
                }
            } else {
                // 新增
                when (type) {
                    IMG_TYPE_STOREFRONT -> visitProcessImgCacheList.add(VisitProcessImgCache(visitId, ImgData(imgList, mutableListOf(), mutableListOf())))
                    IMG_TYPE_CHECKDISPLAY -> visitProcessImgCacheList.add(VisitProcessImgCache(visitId, ImgData(mutableListOf(), imgList, mutableListOf())))
                    IMG_TYPE_SUMMARY -> visitProcessImgCacheList.add(VisitProcessImgCache(visitId, ImgData(mutableListOf(), mutableListOf(), imgList)))
                }
            }
        }
        setPreferenceImgCacheMap(visitProcessImgCacheList, null)  // 保存操作

        /******** 循环异步操作  *******/
        visitProcessImgCacheList.forEach { a ->
            a.imgdata.storeFront.forEach { b ->
                if (b.type == 1) {
                    ImageUpload.uploadImage(b.localPath)
                }
            }
            a.imgdata.checkDisplay.forEach { b ->
                if (b.type == 1) {
                    ImageUpload.uploadImage(b.localPath)
                }
            }
            a.imgdata.summary.forEach { b ->
                if (b.type == 1) {
                    ImageUpload.uploadImage(b.localPath)
                }
            }
        }
    }

    /**
     * 进入页面，追加本地缓存已有的图片
     */
    fun initFirstShowLocalImgCache(visitId: Long, imagePickDataList: MutableList<ImagePickData>, type: Int): MutableList<ImagePickData> {
        var thisImgList = imagePickDataList.distinct().toMutableList()
        if (visitProcessImgCacheList.isNullOrEmpty()) {
            when (type) {
                IMG_TYPE_STOREFRONT -> visitProcessImgCacheList.add(VisitProcessImgCache(visitId, ImgData(thisImgList, mutableListOf(), mutableListOf())))
                IMG_TYPE_CHECKDISPLAY -> visitProcessImgCacheList.add(VisitProcessImgCache(visitId, ImgData(mutableListOf(), thisImgList, mutableListOf())))
                IMG_TYPE_SUMMARY -> visitProcessImgCacheList.add(VisitProcessImgCache(visitId, ImgData(mutableListOf(), mutableListOf(), thisImgList)))
            }
            return thisImgList
        }
        val find = visitProcessImgCacheList.find { a -> a.visitClientId == visitId } ?: return thisImgList
        when (type) {
            IMG_TYPE_STOREFRONT -> {
                if (find.imgdata.storeFront.isNullOrEmpty()) {
                    return thisImgList
                }
                thisImgList.addAll(find.imgdata.storeFront)
                thisImgList = thisImgList.distinct().toMutableList() // 服务器图片的字段 + 本地的缓存地址 ，须去重
                if (thisImgList.size > 6) { // 最多6张 （极少情况：updateVisitImgNew 掉用成功后，app进程死掉，本地缓存未删除）
                    thisImgList = thisImgList.subList(0, 6).toMutableList()
                }
            }
            IMG_TYPE_CHECKDISPLAY -> {
                if (find.imgdata.checkDisplay.isNullOrEmpty()) {
                    return thisImgList
                }
                thisImgList.addAll(find.imgdata.checkDisplay)
                thisImgList = thisImgList.distinct().toMutableList()
                if (thisImgList.size > 6) { // 最多6张
                    thisImgList = thisImgList.subList(0, 6).toMutableList()
                }
            }
            IMG_TYPE_SUMMARY -> {
                if (find.imgdata.summary.isNullOrEmpty()) {
                    return thisImgList
                }
                thisImgList.addAll(find.imgdata.summary)
                thisImgList = thisImgList.distinct().toMutableList()
                if (thisImgList.size > 1) { // 最多1张
                    thisImgList = thisImgList.subList(0, 1).toMutableList()
                }
            }
        }
        return thisImgList
    }

    /**
     * 只保存本地图片,去除掉为空的参数(图片上传成功后，（setImgNetWorkUrl）将VisitProcessImgCacheMap.imgdata.storeFront移除，未移除掉实体为空的情况)
     */
    fun retainDataList(list: MutableList<VisitProcessImgCache>): MutableList<VisitProcessImgCache> {
        var relist: MutableList<VisitProcessImgCache> = mutableListOf()
        // 只保存本地图片
        list.forEach { a ->
            a.imgdata.storeFront = a.imgdata.storeFront.filter { b -> b.type == 1 && File(b.localPath).exists() }.distinct().toMutableList() // lxh File(b.localPath).exists(),TECNO CAMON CX 手机拍照后本机路径自动删除，原因未知
            a.imgdata.checkDisplay = a.imgdata.checkDisplay.filter { b -> b.type == 1 && File(b.localPath).exists() }.distinct().toMutableList()
            a.imgdata.summary = a.imgdata.summary.filter { b -> b.type == 1 && File(b.localPath).exists() }.distinct().toMutableList()
            if (!a.imgdata.storeFront.isNullOrEmpty() || !a.imgdata.checkDisplay.isNullOrEmpty() || !a.imgdata.summary.isNullOrEmpty()) {
                relist.add(a)
            }
        }
        return relist
    }


    /**
     * application 初始化信息
     */
    fun initApplicationImgCache() {
        val list = SharedPreferencesUtil.getDataList("key_img_cache", VisitProcessImgCache::class.java)
        if (list != null) {
            visitProcessImgCacheList = list
            visitProcessImgCacheList.forEach { a ->
                a.imgdata.storeFront.forEach { b ->
                    if (b.type == 1) {
                        ImageUpload.uploadImage(b.localPath)
                    }
                }
                a.imgdata.checkDisplay.forEach { c ->
                    if (c.type == 1) {
                        ImageUpload.uploadImage(c.localPath)
                    }
                }
                a.imgdata.summary.forEach { c ->
                    if (c.type == 1) {
                        ImageUpload.uploadImage(c.localPath)
                    }
                }
            }
        }
    }

    /**
     * 保存imgcache集合
     */
    fun setPreferenceImgCacheMap(list: MutableList<VisitProcessImgCache>, nowData: ImagePickData?) {
        if (!list.isNullOrEmpty()) {
            var retainList = retainDataList(list) // 只保存本地图片,去除掉为空的参数
            SharedPreferencesUtil.saveList("key_img_cache", retainList)
        }
    }
}