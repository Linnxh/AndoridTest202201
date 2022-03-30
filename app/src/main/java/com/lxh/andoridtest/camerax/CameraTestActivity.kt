package com.lxh.andoridtest.camerax

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.lxh.andoridtest.databinding.ActivityCameraTestBinding
import com.lxh.andoridtest.util.FileUtil
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File


/**
 * 拍照功能 ---> 测试页面
 *
 * camera2 代替了 camera
 * camerax 基于 camera2, 提供了相对简洁的代码，适用于简单的拍照和录制视频（camera2可实现更多的自定义功能）
 * 遗留问题
 * TODO: 鲁班压缩不支持 设置输出后的文件名称 可采用其他扩展库进行优化
 * TODO: 可在页面上 增加切换摄像头按钮的操作
 */
class CameraTestActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityCameraTestBinding
    var mImgUri: Uri? = null
    var mFilePath: File? = null

    companion object {
        private const val IMG_PATH: String = "img_path"
        private const val FILE_PATH: String = "file_path"
        fun start(context: Context?, imgUri: Uri? = null, filePath: File? = null) {
            if (context != null) {
                val intent = Intent(context, CameraTestActivity::class.java)
                intent.putExtra(IMG_PATH, imgUri)
                intent.putExtra(FILE_PATH, filePath)
                context.startActivity(intent)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraTestBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val imgUri = intent.getParcelableExtra<Uri>(IMG_PATH)
        val filePath = intent.getSerializableExtra(FILE_PATH)
        if (imgUri != null) {
            mImgUri = imgUri as Uri
            Glide.with(this).load(mImgUri).into(viewBinding.ivUriImg)
        }
        if (filePath != null) {
            mFilePath = filePath as File
            Glide.with(this).load(mFilePath).into(viewBinding.ivFileImg)
            compressImg(mFilePath!!)
        }
        viewBinding.btnTakePhoto.setOnClickListener {
            this.startActivity(Intent(this, CameraXActivity::class.java))
        }

    }

    fun compressImg(file: File) {

        Luban.with(this)
            .load(file)
            .ignoreBy(100)
            .setTargetDir(FileUtil.getOutputDirectory(this).absolutePath)  // 设置压缩后文件存储位置
            .filter { path -> !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif")) }
            .setCompressListener(object : OnCompressListener {
                override fun onStart() {
                }

                override fun onSuccess(file: File) {
                    Log.i("==================", file.absolutePath)
                    Glide.with(this@CameraTestActivity).load(file).into(viewBinding.ivFileImg)
                }

                override fun onError(e: Throwable) {
                    Log.i("===========", file.absolutePath)
                    e.printStackTrace()
                }
            }).launch()

    }

}