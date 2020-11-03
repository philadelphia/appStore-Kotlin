package com.example.installer.entity

/**
@author zhangtao
@date   2020/11/3

 **/
class KtResult<T> {
    var code = 0
    var message: String? = null
    val data: ResultBean<T>? = null



     class ResultBean<T> {
        var data: T? = null
    }
}