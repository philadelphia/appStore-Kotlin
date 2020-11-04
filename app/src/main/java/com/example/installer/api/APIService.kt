package com.example.installer.api

import com.example.installer.entity.PackageEntity
import com.example.installer.entity.Result
import com.example.installer.entity.ProductEntity
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import rx.Observable

/**
@author zhangtao
@date   2020/11/2

 **/
interface APIService {
    /*获取所有APP列表*/
    @Headers("Cache-Control:0")
    @GET("/applicationClient/getApplications")
    fun getProductList(): Observable<Result<List<ProductEntity>>>

    /*获取指定平台的特定APK(release/debug)列表*/
    @Headers("Cache-Control:0")
    @GET("/applicationClient/getApplicationVersions")
    fun getPackageList(
        @Query("system_name") system_name: String?,
        @Query("application_id") application_id: String?,
        @Query("version_type") version_type: String?,
        @Query("page") pageIndex: Int
    ): Observable<Result<List<PackageEntity>>>

}