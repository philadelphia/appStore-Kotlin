package com.example.installer.utils

import com.example.installer.api.APIService
import com.example.installer.entity.APKEntity
import com.example.installer.entity.PackageEntity
import com.example.installer.entity.Result
import rx.Observable

/**
@author zhangtao
@date   2020/11/3

 **/
class ApiServiceUtil {
    companion object {
        private var retrofitUtil: RetrofitUtil? = null
        private var mApiService: APIService? = null

        init {
            retrofitUtil = RetrofitUtil.getInstance()
            mApiService = retrofitUtil!!.createService(APIService::class.java)
        }


        @JvmStatic
        fun getApplicationList(): Observable<Result<List<PackageEntity?>?>?>? {
            return mApiService!!.getApplicationList()
        }

        @JvmStatic
        fun getPackageList(
            system_name: String?,
            application_id: String?,
            version_type: String?,
            pageIndex: Int
        ): Observable<Result<List<APKEntity?>?>?>? {
            return mApiService!!.getPackageList(
                system_name,
                application_id,
                version_type,
                pageIndex
            )
        }
    }
}