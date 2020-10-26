package com.example.installer.mvp;


import com.example.installer.entity.APKEntity;
import com.example.installer.entity.PackageEntity;
import com.example.installer.entity.Result;

import java.util.List;

import rx.Observable;


/**
 * Author Tao.ZT.Zhang
 * Date   2017/10/30
 */

public class MvpContract {
    public interface IView {
        void onLoadApplicationListSuccess(Iterable<PackageEntity> dataSource);

        void onLoadApplicationListFailed();

        void onLoadPackageListSuccess(List<APKEntity> dataSource);

        void onLoadPackageListFailed();

        void notifyDataSize(int count);

        void showContentView();

        void showErrorView();

        void showEmptyView();

        void checkNetWork();

        void onError(String message);

    }

    public interface IModel {
        Observable<Result<List<PackageEntity>>> getApplicationList();

        Observable<Result<List<APKEntity>>> getPackageList(String system_name, String application_id, String version_type, int pageIndex);
    }
}
