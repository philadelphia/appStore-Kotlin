package com.example.installer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.example.installer.BuildConfig
import com.example.installer.ui.MainActivity
import com.example.installer.service.DownloadService
import java.io.File

/**
@author zhangtao
@date   2020/10/23

 **/
class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val fileName = intent.getStringExtra(DownloadService.FILE_NAME)
        installAPK(context, fileName)
    }

    private fun installAPK(context: Context, fileName: String?) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val file = File(context.getExternalFilesDir(MainActivity.DOWNLOAD_PATH), fileName!!)
        val uri: Uri
        //在Android7.0(Android N)及以上版本
        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".fileProvider",
                file
            ) //通过FileProvider创建一个content类型的Uri
        } else {
            Uri.fromFile(file)
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }
}