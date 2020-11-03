package com.example.installer.utils

import android.os.Environment
import com.example.installer.constant.ktConstant
import com.example.installer.interceptor.CacheInterceptor
import com.example.installer.interceptor.LocalCacheInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
@author zhangtao
@date   2020/11/3

 **/
class RetrofitUtil {
    companion object{
        @Volatile
        private var instance: RetrofitUtil? = null

        /*缓存最大容量为10M*/
        const val MAX_CACHE_SIZE = 10 * 1024 * 1024

        @JvmStatic
        fun getInstance(): RetrofitUtil? {
            if (instance == null) {
                synchronized(RetrofitUtil::class.java) {
                    if (instance == null) {
                        instance = RetrofitUtil()
                    }
                }
            }
            return instance
        }

    }

    private fun getOkHttpClient(): OkHttpClient? {
        return OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .addInterceptor(LocalCacheInterceptor())
            .addInterceptor(CacheInterceptor())
            .cache(
                Cache(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    MAX_CACHE_SIZE.toLong()
                )
            )
            .build()
    }

    fun <S> createService(service: Class<S>?): S {
        val retrofit = Retrofit.Builder()
            .baseUrl(ktConstant.BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
        return retrofit.create(service)
    }
}