package com.example.installer.rx

import android.util.Log
import com.example.installer.entity.Result
import retrofit2.HttpException
import rx.Subscriber

/**
@author zhangtao
@date   2020/10/23

 **/
abstract class ResultSubscriber<T> : Subscriber<Result<T>>() {
    companion object {
        private const val TAG = "ResultSubscriber"
    }

    override fun onError(e: Throwable) {
        when {
            (e is HttpException) -> {
                Log.i(TAG, "onError: http exception")
            }
            else -> {
                Log.i(TAG, "onError: 其他 错误")
            }
        }
        onError(e.message)
    }

    override fun onNext(result: Result<T>) {
        if (result.code == 0) {
            onSuccess(result.data?.data)
        } else {
            onError(result.message)
        }
    }

    override fun onCompleted() {}

    abstract fun onSuccess(data: T?)
    abstract fun onError(message: String?)
}