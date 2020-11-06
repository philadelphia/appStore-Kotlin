package com.example.installer.entity

/**
@author zhangtao
@date   2020/11/3

 **/
class Result<T> {
    var code = 0
    var message: String? = null
    var data: ResultBean<T>? = null

    class ResultBean<T> {
        var data: T? = null
    }
}