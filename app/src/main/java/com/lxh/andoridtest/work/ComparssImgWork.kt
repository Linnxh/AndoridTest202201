/*
 * Copyright (C) 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lxh.andoridtest.work

import android.content.Context
import android.util.Log
import androidx.work.*
import com.egatee.jde.visit.imgcache.VisitImgConfig

/**
 * Worker job to refresh titles from the network while the app is in the background.
 *
 * WorkManager is a library used to enqueue work that is guaranteed to execute after its constraints
 * are met. It can run work even when the app is in the background, or not running.
 */
class ComparssImgWork(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    companion object {
        val KEY_TEST_DATA = "key_data"
        val KEY_RETRY_COUNT_DATA = "key_retry_count_data"

    }

    /**
     * Refresh the title from the network using [TitleRepository]
     *
     * WorkManager will call this method from a background thread. It may be called even
     * after our app has been terminated by the operating system, in which case [WorkManager] will
     * start just enough to run this [Worker].
     */
    override suspend fun doWork(): Result {
        val retryCount = runAttemptCount
        Log.d("lxh", "retryCount " + retryCount)

        if (retryCount > 3) {
            return Result.failure(workDataOf(KEY_TEST_DATA to "fail out data"))
        }
        try {

            var aa = 1 / 0
            /******** 循环异步操作  *******/
            VisitImgConfig.visitProcessImgCacheList.forEach { a ->
                a.imgdata.storeFront.forEach { b ->
                    if (b.type == 1) {
                        // TODO: lxh 假设如下方法 就是 上传图片 + 拜访信息到服务器
                        val localPath = b.localPath
//                        val result = RetrofitClient.iNetApi.saveHeadImage(localPath)
//                        if (result.code == 1) {
                            VisitImgConfig.setImgNetWorkUrl(localPath, "")
//                        }
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return Result.retry()
        }

        // get data in workManager
        val getData = inputData.getString(KEY_TEST_DATA)
        Log.d("lxh", "getData $getData")
        // set data to workManager
        val data = workDataOf(KEY_TEST_DATA to "out data")
        return Result.success(data)
    }


    class Factory() : WorkerFactory() {
        override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
            return ComparssImgWork(appContext, workerParameters)
        }

    }
}