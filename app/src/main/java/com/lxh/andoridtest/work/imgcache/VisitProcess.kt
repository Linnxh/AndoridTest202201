package com.lxh.andoridtest.work.imgcache

import android.net.Uri
import androidx.annotation.Keep
import java.io.Serializable

/**
 * 拜访流程图片缓存实体
 */
@Keep
data class VisitProcessImgCache(
    var visitClientId: Long,// 拜访流程的id
    var imgdata: ImgData
) : Serializable {
    constructor() : this(0, ImgData())
}

/**
 * 图片参数
 */
@Keep
data class ImgData(
    var storeFront: MutableList<ImagePickData>,// 门头照
    var checkDisplay: MutableList<ImagePickData>,// 陈列检查
    var summary: MutableList<ImagePickData> // 拜访总结
) : Serializable {
    constructor() : this(mutableListOf(), mutableListOf(), mutableListOf())
}

/**
 * 图片信息实体类
 */
@Keep
data class ImagePickData(
    var localPath: String,// 本地路径  // type-visitId-buyerCheckId-buyerUserId-isUpload
    var netWorkPath: String,// 网络路径
    var uri: Uri?, // 图片Uri
    var type: Int // 0：默认图 1：本地图片  2：网络图片地址
) : Serializable {
    constructor() : this("", "", null, 0)
}

/**
 * 保存图片时命名的实体
 */
@Keep
data class ImgSaveInfo(
    var type: Int,// 类型
    var visitClientId: Long, // 拜访id
    var buyerCheckId: Long?, // 陈列检查的id
    var buyerUserId: Long //门店的userId
) : Serializable {
}

/**
 * 门头照更新
 */
@Keep
class StoreFrontImgEvent() : Serializable {
}

/**
 * 拜访图片的更新操作，默认增量更新
 */
@Keep
class UpdateVisitImgReq(
    var type: Int, // 1：门头照 2：陈列检查  3：总结
    var imgUrl: String,  // 网络图片地址
    var visitClientId: Long, // 拜访id
    var buyerCheckId: Long, // 陈列检查id
    var localPath: String,
    var isAll: Int? = 0 // 是否全量更新，默认0：增量   1：全量
) : Serializable {
    constructor() : this(0, "", 0L, 0L, "")
}

/**
 * 新打开app，需要查询一下，当前的的拜访是否结束，或者是否在有效期内（默认10:天，超过自动删除）
 */
@Keep
class GetVisitCacheStatusReq(
    var visitClientIdList: List<Long>, // 缓存中的拜访id
    var days: Int? = 10 // 默认10天
) : Serializable {
    constructor() : this(mutableListOf())
}

/**
 * GetVisitCacheStatusReq 的返回值
 */
@Keep
class VisitCacheStatusResp(
    var visitClientIdList: List<Long>, // 返回有效的id
) : Serializable {
    constructor() : this(mutableListOf())
}







