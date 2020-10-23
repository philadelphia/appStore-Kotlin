package com.example.installer.utils;


import com.example.installer.api.ApiService;
import com.example.installer.entity.APKEntity;
import com.example.installer.entity.PackageEntity;
import com.example.installer.entity.Result;

import java.util.List;

import rx.Observable;

/**
 * Author:  ZhangTao
 * Date: 2018/3/30.
 */
public class ApiServiceUtil {
    private static RetrofitUtil retrofitUtil;
    private static ApiService mApiService;

    static {
        retrofitUtil = RetrofitUtil.getInstance();
        mApiService = retrofitUtil.createService(ApiService.class);
    }

    public static Observable<Result<List<PackageEntity>>> getApplicationList() {
        return mApiService.getApplicationList();
    }

    public static Observable<Result<List<APKEntity>>> getPackageList(String system_name, String application_id, String version_type, int pageIndex) {
        return mApiService.getPackageList(system_name, application_id, version_type, pageIndex);
    }
}
