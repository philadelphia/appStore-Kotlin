package com.example.installer.interceptor

import com.example.installer.utils.NetWorkUtil
import com.example.installer.utils.StringUtil
import okhttp3.Interceptor
import okhttp3.Response

/**
@author zhangtao
@date   2020/11/3

 **/
class CacheInterceptor :Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val maxAge = request.header("Cache-Control")

        val respBuilder = response.newBuilder()
        respBuilder.removeHeader("Pragma").removeHeader("Cache-Control")

        if (NetWorkUtil.isNetConnected()) {
            if (!StringUtil.isEmpty(maxAge)) {
                respBuilder.header("Cache-Control", "max-age=$maxAge")
            } else {
                respBuilder.header("Cache-Control", "max-age=" + 60 * 60 * 3)
            }
        } else {
            respBuilder.header(
                "Cache-Control",
                "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7
            )
        }

        return respBuilder.build()
    }
}