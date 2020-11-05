package com.example.installer.mvvm

import com.example.installer.entity.PackageEntity
import com.example.installer.entity.ProductEntity
import com.example.installer.entity.Result
import com.example.installer.utils.ApiServiceUtil
import com.example.installer.utils.RxsRxSchedulers
import rx.Observable

/**
@author zhangtao
@date   2020/11/3

 **/
class MainRepository {
    companion object {
        fun getProductList(): Observable<Result<List<ProductEntity>>> {
            return ApiServiceUtil.getProductList().compose(RxsRxSchedulers.ioToMain())
        }

        fun getPackageList(
            system_name: String?,

            application_id: String?,

            version_type: String?,

            pageIndex: Int
        ): Observable<Result<List<PackageEntity>>> {
            return ApiServiceUtil.getPackageList(
                system_name,
                application_id,
                version_type,
                pageIndex
            ).compose(RxsRxSchedulers.ioToMain())
        }
    }
}