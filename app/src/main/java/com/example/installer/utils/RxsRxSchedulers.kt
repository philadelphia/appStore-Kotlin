package com.example.installer.utils

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
@author zhangtao
@date   2020/11/3

 **/
class RxsRxSchedulers {
    companion object {
        fun <T> ioToMain(): Observable.Transformer<T, T>? {
            return Observable.Transformer { tObservable ->
                tObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            }
        }
    }
}