package com.example.installer.utils

import android.content.Context
import android.net.ConnectivityManager
import com.example.installer.App

/**
@author zhangtao
@date   2020/11/3

 **/
class NetWorkUtil {
    companion object{

        /**
         * 检测网络是否连接
         *
         * @return
         */
        fun isNetConnected(): Boolean {
            val cm =
                App.instance.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm != null) {
                val infoList = cm.allNetworkInfo
                if (infoList != null) {
                    for (ni in infoList) {
                        if (ni.isConnected) {
                            return true
                        }
                    }
                }
            }
            return false
        }

        /**
         * 检测wifi是否连接
         *
         * @return
         */
        fun isWifiConnected(): Boolean {
            val cm = App.instance.applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm != null) {
                val networkInfo = cm.activeNetworkInfo
                if (networkInfo != null
                    && networkInfo.type == ConnectivityManager.TYPE_WIFI
                ) {
                    return true
                }
            }
            return false
        }

        /**
         * 检测3G是否连接
         *
         * @return
         */
        fun is3gConnected(): Boolean {
            val cm = App.instance.applicationContext
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm != null) {
                val networkInfo = cm.activeNetworkInfo
                if (networkInfo != null
                    && networkInfo.type == ConnectivityManager.TYPE_MOBILE
                ) {
                    return true
                }
            }
            return false
        }

    }
}