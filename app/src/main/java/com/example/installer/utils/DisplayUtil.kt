package com.example.installer.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import android.view.Window

/**
@author zhangtao
@date   2020/11/3

 **/
class DisplayUtil {
    companion object{
        /**
         * 将px值转换为dip或dp值，保证尺寸大小不变
         *
         *
         * （DisplayMetrics类中属性density）
         *
         * @return
         */
        fun px2dip(context: Context, pxValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        /**
         * 将dip或dp值转换为px值，保证尺寸大小不变
         *
         *
         * （DisplayMetrics类中属性density）
         *
         * @return
         */
        fun dip2px(context: Context, dipValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dipValue * scale + 0.5f).toInt()
        }

        /**
         * 将px值转换为sp值，保证文字大小不变
         *
         *
         * （DisplayMetrics类中属性scaledDensity）
         *
         * @return
         */
        fun px2sp(context: Context, pxValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (pxValue / fontScale + 0.5f).toInt()
        }

        /**
         * 将sp值转换为px值，保证文字大小不变
         *
         *
         * （DisplayMetrics类中属性scaledDensity）
         *
         * @return
         */
        fun sp2px(context: Context, spValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        /**
         * 像素
         * @param context
         */
        fun getWindowWidth(context: Activity): Int {
            val metric = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(metric)
            return metric.widthPixels // 屏幕宽度（像素）
        }

        /**
         * 像素
         * @param context
         */
        fun getWindowHeight(context: Activity): Int {
            val metric = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(metric)
            return metric.heightPixels // 屏幕高度（像素）
        }

        /**
         * 获取设备ContentView的高度
         */
        fun getContentViewHeight(activity: Activity): Int {
            val rectangle = Rect()
            activity.window.findViewById<View>(Window.ID_ANDROID_CONTENT).getDrawingRect(rectangle)
            return rectangle.height()
        }

        /**
         * 获取顶部状态栏的高度
         */
        fun getStatusBarHeight(context: Context): Int {
            val resources = context.resources
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }

        /**
         * 获取底部导航栏的高度
         * @return
         */
        fun getNavigationBarHeight(context: Context): Int {
            val resources = context.resources
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }
    }
}