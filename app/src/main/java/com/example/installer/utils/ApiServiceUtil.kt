package com.example.installer.utils

import com.example.installer.api.APIService
import com.example.installer.entity.PackageEntity
import com.example.installer.entity.ProductEntity
import com.example.installer.entity.Result
import rx.Observable

/**
@author zhangtao
@date   2020/11/3

 **/
class ApiServiceUtil {
    companion object {
        private val mApiService by lazy {
            RetrofitUtil.getInstance().createService(APIService::class.java)
        }

        fun getProductList(): Observable<Result<List<ProductEntity>>> {
            return mApiService.getProductList()
        }

        fun getPackageList(
            system_name: String?,
            application_id: String?,
            version_type: String?,
            pageIndex: Int
        ): Observable<Result<List<PackageEntity>>> {
            return mApiService.getPackageList(
                system_name,
                application_id,
                version_type,
                pageIndex
            )
        }
    }
}