package com.example.installer.adapter

import android.util.SparseArray
import android.view.View
import android.widget.Checkable
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView.ViewHolder

/**
@author zhangtao
@date   2020/11/2

 **/
class CommonViewHolder(itemView: View) : ViewHolder(itemView) {
    private val mItemChildViews by lazy {
        SparseArray<View?>()
    }

    fun getView(@IdRes id: Int): View? {
        var childView = mItemChildViews.get(id)
        if (childView == null) {
            childView = itemView.findViewById(id)
            mItemChildViews.put(id, childView)
        }
        return childView
    }

    //TextView
    fun setText(viewId: Int, text: String?): CommonViewHolder? {
        val textView: TextView? = getView(viewId) as TextView
        textView?.text = text
        return this
    }

    fun setTextColor(viewId: Int, @ColorInt color: Int): CommonViewHolder? {
        val textView: TextView? = getView(viewId) as TextView
        textView?.setTextColor(color)
        return this
    }

    /**
     * set view onClickListeners
     */
    fun setOnClickListener(
        listener: View.OnClickListener?,
        @IdRes vararg viewIds: Int
    ): CommonViewHolder? {
        for (id in viewIds) {
            val view = getView(id)
            view?.setOnClickListener(listener)
        }
        return this
    }

    fun setVisible(@IdRes id: Int, visibility: Int) {
        getView(id)?.visibility = visibility
    }

    fun setChecked(@IdRes id: Int, checked: Boolean) {
        val view = getView(id)
        if (view is Checkable)
            view.isChecked = checked
    }
}