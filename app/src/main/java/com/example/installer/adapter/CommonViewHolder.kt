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

    fun <T : View> getView(@IdRes id: Int): T? {
        var childView = mItemChildViews.get(id)
        if (childView == null) {
            childView = itemView.findViewById(id)
            mItemChildViews.put(id, childView)
        }
        return childView as T
    }

    //TextView
    fun setText(viewId: Int, text: String?): CommonViewHolder? {
        val textView: TextView? = getView(viewId)
        textView?.text = text
        return this
    }

    fun setTextColor(viewId: Int, @ColorInt color: Int): CommonViewHolder? {
        val textView: TextView? = getView(viewId)
        textView?.setTextColor(color)
        return this
    }

    /**
     * set view onClickListeners
     */
    fun setOnClickListener(
        @IdRes vararg viewIds: Int,
        listener: (View) -> Unit
    ): CommonViewHolder? {
        for (id in viewIds) {
            val view = getView<View>(id)
            view?.setOnClickListener(listener)
        }
        return this
    }

    fun setVisible(@IdRes id: Int, visibility: Int) {
        getView<View>(id)?.visibility = visibility
    }

    fun setChecked(@IdRes id: Int, checked: Boolean) {
        val view = getView<View>(id)
        if (view is Checkable)
            view.isChecked = checked
    }
}