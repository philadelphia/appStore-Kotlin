package com.example.installer.mvp;

import com.example.installer.entity.APKEntity;
import com.example.installer.entity.PackageEntity;
import com.example.installer.entity.Result;
import com.example.installer.utils.ApiServiceUtil;
import com.example.installer.utils.RxsRxSchedulers;

import java.util.List;

import rx.Observable;


/**
 * Author Tao.ZT.Zhang
 * Date   2017/10/30
 */

public class Model implements MvpContract.IModel {
    @Override
    public Observable<Result<List<PackageEntity>>> getApplicationList() {
        return ApiServiceUtil.getApplicationList().compose(RxsRxSchedulers.io_main());
    }

    @Override
    public Observable<Result<List<APKEntity>>> getPackageList(String system_name, String application_id, String version_type, int pageIndex) {
        return ApiServiceUtil.getPackageList(system_name, application_id, version_type, pageIndex).compose(RxsRxSchedulers.io_main());
    }

}
