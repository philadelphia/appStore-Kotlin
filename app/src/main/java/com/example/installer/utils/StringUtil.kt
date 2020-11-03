package com.example.installer.utils

/**
@author zhangtao
@date   2020/11/3

 **/
class StringUtil {
    companion object {
        fun isEmpty(str: String?): Boolean {
            return str?.trim()?.length == 0 || str.equals("null", true)
        }

        fun isNotEmpty(str: String?): Boolean {
            return !isEmpty(str)
        }
    }
}