package com.example.installer.utils

import android.content.Context
import android.os.Environment
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule

/**
@author zhangtao
@date   2020/11/3

 **/
@GlideModule
class MyGlideModule : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        //获取内存的默认配置

        //内存缓存相关,默认是24m
        val memoryCacheSizeBytes = 1024 * 1024 * 20 // 20mb
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes.toLong()))


        //设置磁盘缓存及其路径
        //
        val MAX_CACHE_SIZE = 100 * 1024 * 1024
        val CACHE_FILE_NAME = "imgCache"
        builder.setDiskCache(
            ExternalCacheDiskCacheFactory(
                context,
                CACHE_FILE_NAME,
                MAX_CACHE_SIZE
            )
        )
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            val downloadDirectoryPath =
                Environment.getExternalStorageDirectory().absolutePath + "/" +
                        CACHE_FILE_NAME
            //路径---->sdcard/imgCache
            builder.setDiskCache(DiskLruCacheFactory(downloadDirectoryPath, MAX_CACHE_SIZE as Long))
        } else {
            //路径---->/sdcard/Android/data/<application package>/cache/imgCache
            builder.setDiskCache(
                ExternalCacheDiskCacheFactory(
                    context,
                    CACHE_FILE_NAME,
                    MAX_CACHE_SIZE
                )
            )
        }
    }

    override fun isManifestParsingEnabled(): Boolean {
        //不使用清单配置的方式,减少初始化时间
        return false
    }
}