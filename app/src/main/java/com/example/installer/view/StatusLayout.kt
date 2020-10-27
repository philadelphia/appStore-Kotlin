package com.example.installer.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.example.installer.R
import com.zhy.autolayout.AutoRelativeLayout

/**
@author zhangtao
@date   2020/10/28

 **/
class StatusLayout : AutoRelativeLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    companion object {
        val StateContent = -1
        val StateLoading = 0
        val StateError = 1
        val StateEmpty = 2
        val StateNoNet = 3
    }

    private val noNetwrokView by lazy {
        LayoutInflater.from(context).inflate(R.layout.viewstatus_no_netwrok, this, false)
    }

    /**
     * 四种状态布局
     */

    private val loadingView by lazy {
        LayoutInflater.from(context).inflate(R.layout.viewstatus_loading, this, false)
    }

    /**
     * 四种状态布局
     */
    private val loadingWrongView by lazy {
        LayoutInflater.from(context).inflate(R.layout.viewstatus_loading_faile, this, false)
    }

    /**
     * 四种状态布局
     */
    private val noDataView by lazy {
        LayoutInflater.from(context).inflate(R.layout.viewstatus_no_data, this, false)
    }


    private var mContentView: View? = null
    private var mShowState = StatusLayout1.StateContent

    /**
     * 显示空视图
     */
    fun showEmptyView() {
        selectView(nowShowView(mShowState), noDataView)
        mShowState = StatusLayout1.StateEmpty
    }

    private fun nowShowView(state: Int): View? {
        var retuView: View? = null
        when (state) {
            StatusLayout1.StateContent -> retuView = mContentView
            StatusLayout1.StateEmpty -> retuView = noDataView
            StatusLayout1.StateError -> retuView = loadingWrongView
            StatusLayout1.StateLoading -> retuView = loadingView
            StatusLayout1.StateNoNet -> retuView = noNetwrokView
        }
        return retuView
    }

    /**
     * 从旧布局选择到新布局，可以考虑做动画
     *
     * @param pOldView
     * @param pNewView
     */
    private fun selectView(pOldView: View?, pNewView: View) {
        if (pOldView === pNewView) {
            return
        }
        setViewVisibility(pOldView, true)
        setViewVisibility(pNewView, true)
        val oldAlpha = AlphaAnimation(1.0f, 0.0f)
        oldAlpha.duration = 500
        val newAlpha = AlphaAnimation(0.0f, 1.0f)
        newAlpha.duration = 500
        pOldView?.animation = oldAlpha
        pNewView.animation = newAlpha
        oldAlpha.startNow()
        newAlpha.startNow()
        oldAlpha.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                setViewVisibility(pOldView, false)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        newAlpha.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                setViewVisibility(pNewView, true)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }


    /***
     * 设置一个控件的状态
     *
     * @param pView 控件
     * @param vis   true，显示，false不显示
     */
    private fun setViewVisibility(pView: View?, vis: Boolean) {
        if (pView != null) {
            pView.visibility = if (vis) VISIBLE else GONE
        }
    }

}