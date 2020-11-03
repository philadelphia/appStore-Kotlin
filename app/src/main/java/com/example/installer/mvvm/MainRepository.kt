package com.example.installer.mvvm

import com.example.installer.entity.APKEntity
import com.example.installer.entity.KtResult
import com.example.installer.entity.PackageEntity
import com.example.installer.utils.ApiServiceUtil
import com.example.installer.utils.RxsRxSchedulers
import rx.Observable

/**
@author zhangtao
@date   2020/11/3

 **/
class MainRepository {
    companion object {
        @JvmStatic
        fun getApplicationList(): Observable<KtResult<List<PackageEntity>?>?> {
            return ApiServiceUtil.getApplicationList().compose(RxsRxSchedulers.io_main())
        }

        @JvmStatic
        fun getPackageList(
            system_name: String?,

            application_id: String?,

            version_type: String?,

            pageIndex: Int
        ): Observable<KtResult<List<APKEntity>?>?> {
            return ApiServiceUtil.getPackageList(
                system_name,
                application_id,
                version_type,
                pageIndex
            ).compose(RxsRxSchedulers.io_main())
        }
    }
}