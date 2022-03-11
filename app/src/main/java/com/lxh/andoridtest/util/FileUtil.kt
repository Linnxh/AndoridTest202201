package com.lxh.andoridtest.util

import android.content.Context
import com.lxh.andoridtest.R
import java.io.File

/**
 * @author lxh
 * @date   FileUtil$
 * @describe
 */


object FileUtil {
    /** Use external media if it is available, our app's file directory otherwise */
    fun getOutputDirectory(context: Context): File {
        val appContext = context.applicationContext
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, appContext.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else appContext.filesDir
    }

    fun getNameFromFile(file: File): String {
        val path = file.absolutePath
        return path.substring(path.lastIndexOf("/") + 1, path.length)
    }

    fun getLubanOutFileName(file: File): String {
        val path = file.absolutePath
        var name = path.substring(path.lastIndexOf("/") + 1, path.length)
        return path.substringBefore(name) + name.substringBefore(".") + "_2." + name.substringAfter(".")
    }

}