package com.example.installer.service

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.core.app.JobIntentService
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
@author zhangtao
@date   2020/11/2

 **/
class DownloadService : JobIntentService() {


    companion object {
        const val FILE_NAME = "fileName"
        const val DOWNLOAD_PATH = "download_path"
        const val BROADCAST_ACTION = "com.meiliwu.installer.service.BROADCAST"
        const val EXTENDED_DATA_STATUS = "com.meiliwu.installer.service.STATUS"
        private const val JOB_ID = 1

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(
                context,
                DownloadService::class.java, JOB_ID, work
            )
        }
    }

    override fun onHandleWork(intent: Intent) {
        val dataString = intent.dataString
        val fileName = intent.getStringExtra(FILE_NAME)
        val downloadPath = intent.getStringExtra(DOWNLOAD_PATH)
        val downLoadService: DownloadManager =
            getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(dataString))

        //指定APK缓存路径和应用名称，可在SD卡/storage/sdcard0/Download文件夹中查看
        request.setDestinationInExternalPublicDir(downloadPath, fileName)

        //设置网络下载环境为wifi和Mobile环境
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)

        //设置下载文件的mineType。因为下载管理Ui中点击某个已下载完成文件及下载完成点击通知栏提示都会根据mimeType去打开文件，所以我们可以利用这个属性。
        request.setMimeType("application/vnd.android.package-archive")

        //设置通知栏标题
        request.setTitle(fileName)
        request.setDescription("应用正在下载")

        //设置是否允许漫游连接
        request.setAllowedOverRoaming(false)
        //获得唯一下载id
        val requestId: Long = downLoadService.enqueue(request)

        //将id放进Intent
        val localIntent = Intent(BROADCAST_ACTION)
        localIntent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, requestId)
        localIntent.putExtra(FILE_NAME, fileName)
        //查询下载信息
        val query = DownloadManager.Query()
        query.setFilterById(requestId)

        var isGoing = true
        try {
            while (isGoing) {
                val cursor: Cursor = downLoadService.query(query)

                if (cursor.moveToFirst()) {
                    when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            isGoing = false
                            //调用LocalBroadcastManager.sendBroadcast将intent传递回去
                            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)
                        }
                    }
                }
                cursor.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}