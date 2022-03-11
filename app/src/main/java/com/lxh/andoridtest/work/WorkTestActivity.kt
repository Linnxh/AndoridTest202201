package com.lxh.andoridtest.work

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.lxh.andoridtest.databinding.ActivityWorkTestBinding
import java.util.concurrent.TimeUnit

/**
 * @author lxh
 * @date   WorkTestActivity$
 * @describe
 */


class WorkTestActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityWorkTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityWorkTestBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        viewBinding.btnStartWork.setOnClickListener {

            val request: WorkRequest = OneTimeWorkRequestBuilder<ComparssImgWork>()
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR, // 重试以后的延长时间以什么方式增长
                    OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .setInputData(workDataOf(ComparssImgWork.KEY_TEST_DATA to "into data")) // 方法一:设置workManager 的值（简化）
                .setInputData(Data.Builder().let { data ->  // 方法二:设置workManager 的值
                    data.putString(ComparssImgWork.KEY_TEST_DATA, "into data")
                    data.build()
                })
                .build()

            // 通过 WorkManager 的 getWorkInfoByXXXLiveData() 获取 WorkRequest 的 LiveData<WorkInfo> 对象
            WorkManager.getInstance(applicationContext)
                .getWorkInfoByIdLiveData(request.id)
                .observe(this, object : Observer<WorkInfo> {
                    override fun onChanged(t: WorkInfo?) {
                        if (t?.state == WorkInfo.State.SUCCEEDED) {
                            for ((key, value) in t.outputData!!.keyValueMap) {
                                Log.d("lxh", "Out Data $key ---- $value")
                            }
                        }
                        if (t?.state == WorkInfo.State.FAILED) {
                            for ((key, value) in t.outputData!!.keyValueMap) {
                                Log.d("lxh", "Out Data222 $key ---- $value")
                            }
                        }
                    }

                })
            WorkManager.getInstance(this).enqueue(request)
        }
    }

}