package com.example.installer.utils

/**
@author zhangtao
@date   2020/11/3

 **/
class SingleClick {
    companion object{
        private var lastTime: Long = 0

        fun isSingle(defalutTime: Long): Boolean {
            val currentTime = System.currentTimeMillis()
            val isSign = if (currentTime - lastTime <= defalutTime) false else true
            lastTime = currentTime
            return isSign
        }

        fun getLastTime(): Long {
            return lastTime
        }

        fun setLastTime(lastTime: Long) {
            SingleClick.lastTime = lastTime
        }

    }
}