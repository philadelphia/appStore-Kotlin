package com.example.installer

import android.app.Application
import com.liulishuo.filedownloader.FileDownloader

/**
@author zhangtao
@date   2020/10/28

 **/
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        FileDownloader.setup(this)
    }

    companion object {
        lateinit var instance: App
    }


}