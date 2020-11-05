package com.example.installer.utils

import android.os.Environment
import com.example.installer.constant.Constant
import com.example.installer.interceptor.CacheInterceptor
import com.example.installer.interceptor.LocalCacheInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable
import java.util.concurrent.TimeUnit

/**
@author zhangtao
@date   2020/11/3

 **/
class RetrofitUtil private constructor() : Serializable {
    companion object {
        /*缓存最大容量为10M*/
        const val MAX_CACHE_SIZE = 10 * 1024 * 1024

        fun getInstance(): RetrofitUtil {
            return SingletonHolder.instance
        }
    }

    private object SingletonHolder {
        val instance: RetrofitUtil = RetrofitUtil()
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
            .baseUrl(Constant.BASE_URL)
            .client(getOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build()
        return retrofit.create(service)
    }


    private fun readResolve(): Any {
        //防止单例对象在反序列化时重新生成对象
        //由于反序列化时会调用readResolve这个钩子方法，只需要把当前的RetrofitUtil对象返回而不是去创建一个新的对象
        return RetrofitUtil
    }

}