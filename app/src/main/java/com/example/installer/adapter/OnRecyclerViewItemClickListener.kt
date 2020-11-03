package com.example.installer.adapter

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener

/**
@author zhangtao
@date   2020/11/3

 **/
abstract class OnRecyclerViewItemClickListener : OnItemTouchListener {
    private var gestureDetectorCompat: GestureDetectorCompat? = null
    private var recyclerView: RecyclerView

    constructor(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        gestureDetectorCompat = GestureDetectorCompat(
            recyclerView.context,
            ItemTouchHelperGestureListener()
        )
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        gestureDetectorCompat!!.onTouchEvent(e)
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        gestureDetectorCompat!!.onTouchEvent(e)
    }


    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        TODO("Not yet implemented")
    }

    abstract fun onItemClick(view: View?, position: Int)

    abstract fun onItemLongClick(view: View?, position: Int)


    inner class ItemTouchHelperGestureListener : SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return false
        }

        override fun onShowPress(e: MotionEvent) {}
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val view: View? = recyclerView!!.findChildViewUnder(e.x, e.y)
            if (view != null) {
                onItemClick(view, recyclerView!!.getChildAdapterPosition(view))
            }
            return true
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            return false
        }

        override fun onLongPress(e: MotionEvent) {
            val view: View? = recyclerView!!.findChildViewUnder(e.x, e.y)
            if (view != null) {
                onItemLongClick(view, recyclerView.getChildAdapterPosition(view))
            }
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            return false
        }
    }
}