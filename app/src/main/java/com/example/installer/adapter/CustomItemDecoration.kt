package com.example.installer.adapter

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.installer.utils.DisplayUtil

/**
@author zhangtao
@date   2020/11/3

 **/
class CustomItemDecoration : ItemDecoration {
    private var spanCount = 0
    private val recyclerViewHorizontalPadding = 15
    private val recyclerViewVerticalPadding = 15
    private val itemHorizontalPadding = 15
    private val itemVerticalPadding = 10


    constructor(spanCount: Int) {
        this.spanCount = spanCount
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val currentPosition = parent.getChildLayoutPosition(view!!)
        if (currentPosition % spanCount == 0) {
            //第一列
            outRect.left =
                DisplayUtil.dip2px(parent.context, recyclerViewHorizontalPadding.toFloat())
            outRect.right = DisplayUtil.dip2px(parent.context, itemHorizontalPadding.toFloat()) / 2
        } else if (currentPosition % spanCount == spanCount - 1) {
            //最后一列
            outRect.left = DisplayUtil.dip2px(parent.context, itemHorizontalPadding.toFloat()) / 2
            outRect.right =
                DisplayUtil.dip2px(parent.context, recyclerViewHorizontalPadding.toFloat())
        } else {
            outRect.left = DisplayUtil.dip2px(parent.context, itemHorizontalPadding.toFloat()) / 2
            outRect.right = DisplayUtil.dip2px(parent.context, itemHorizontalPadding.toFloat()) / 2
        }
        if (isFirstRow(parent, currentPosition)) {
            outRect.top = DisplayUtil.dip2px(parent.context, recyclerViewVerticalPadding.toFloat())
            outRect.bottom = DisplayUtil.dip2px(parent.context, itemVerticalPadding.toFloat()) / 2
        } else if (isLastRow(parent, currentPosition)) {
            outRect.top = DisplayUtil.dip2px(parent.context, itemVerticalPadding.toFloat()) / 2
            outRect.bottom =
                DisplayUtil.dip2px(parent.context, recyclerViewVerticalPadding.toFloat())
        } else {
            outRect.top = DisplayUtil.dip2px(parent.context, itemVerticalPadding.toFloat()) / 2
            outRect.bottom = DisplayUtil.dip2px(parent.context, itemVerticalPadding.toFloat()) / 2
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
    }

    private fun isFirstRow(parent: RecyclerView, position: Int): Boolean {
        return (position - position % spanCount) / spanCount == 0
    }

    private fun isLastRow(parent: RecyclerView, position: Int): Boolean {
        val childCount = parent.adapter!!.itemCount
        return position >= childCount - childCount % spanCount
    }

    private fun getRowIndex(parent: RecyclerView, position: Int): Int {
        return (position - position % spanCount) / spanCount
    }
}