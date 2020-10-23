package com.example.installer.mvp

import android.util.Log
import com.example.installer.entity.Result
import retrofit2.HttpException
import rx.Subscriber

/**
@author zhangtao
@date   2020/10/23

 **/
abstract class ResultSubscriber<T>() : Subscriber<Result<T>>() {
    private val TAG = "ResultSubscriber"
    override fun onError(e: Throwable?) {
        when {
            e is HttpException -> {
                Log.i(TAG, "onError: http exception")
                onError(e.message)
            }

        }
    }

    override fun onNext(result: Result<T>?) {
        if (result?.code == 0) {
//            onSuccess(result?.data?.data)
        } else {
            onError(result?.message)
        }
    }


    abstract fun onSuccess(t: T)
    abstract fun onError(message: String?)
}