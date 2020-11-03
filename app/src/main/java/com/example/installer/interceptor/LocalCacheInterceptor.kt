package com.example.installer.interceptor

import android.util.Log
import com.example.installer.utils.NetWorkUtil
import com.example.installer.utils.StringUtil
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

/**
@author zhangtao
@date   2020/11/3

 **/
class LocalCacheInterceptor: Interceptor {
    private val TAG = "LocalCacheInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        if (!NetWorkUtil.isNetConnected()) {
            request = request.newBuilder()
                .cacheControl(CacheControl.FORCE_CACHE)
                .build()
        } else {
            val maxAge = request.header("Cache-Control")
            if (!StringUtil.isEmpty(maxAge) && "0" == maxAge) {
                request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build()
            }
            Log.i(TAG, "intercept: " + request.url())
        }


        return chain.proceed(request)
    }
}