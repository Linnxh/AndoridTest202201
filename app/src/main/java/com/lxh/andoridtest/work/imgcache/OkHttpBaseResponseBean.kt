package com.egatee.jde.visit.imgcache

import java.io.Serializable

class OkHttpBaseResponseBean<T> : Serializable {

    var code = 0    //1: success
    var msg: String? = null
    var data: T? = null
    var success: Boolean? = null

}