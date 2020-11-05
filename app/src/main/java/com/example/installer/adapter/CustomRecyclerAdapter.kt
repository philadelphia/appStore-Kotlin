package com.example.installer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.installer.R

/**
@author zhangtao
@date   2020/11/3

 **/
abstract class CustomRecyclerAdapter<T>(dataSource: MutableList<T>) :
    RecyclerView.Adapter<CommonViewHolder>() {

    // 当前加载状态，默认为加载完成
    private var loadState = 2
    private val dataSource: MutableList<T>

    companion object {
        // 普通布局
        const val TYPE_ITEM = 1

        // 脚布局
        const val TYPE_FOOTER = 2

        // 正在加载
        const val LOADING = 1

        // 加载完成
        const val LOADING_COMPLETE = 2

        // 加载到底
        const val LOADING_END = 3
    }

    init {
        this.dataSource = dataSource
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        when (viewType) {
            TYPE_ITEM -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(getItemLayoutID(), parent, false)
                return CommonViewHolder(itemView)
            }

            TYPE_FOOTER -> {
                val itemView =
                    LayoutInflater.from(parent.context)
                        .inflate(getFootViewLayoutID(), parent, false)
                return CommonViewHolder(itemView)
            }

            else -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(getItemLayoutID(), parent, false)
                return CommonViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_ITEM -> {
                convert(holder, dataSource[position], position)
            }
            TYPE_FOOTER -> {
                when (loadState) {
                    // 正在加载
                    LOADING -> {
                        holder.setVisible(R.id.progressBar, View.VISIBLE)
                        holder.setVisible(R.id.tv_loadMore, View.VISIBLE)
                        holder.setVisible(R.id.tv_loadEnd, View.GONE)
                    }

                    // 加载完成,footerview 隐藏
                    LOADING_COMPLETE -> {
                        holder.setVisible(R.id.progressBar, View.GONE)
                        holder.setVisible(R.id.tv_loadMore, View.GONE)
                        holder.setVisible(R.id.tv_loadEnd, View.GONE)
                    }

                    // 加载到底
                    LOADING_END -> {
                        holder.setVisible(R.id.progressBar, View.GONE)
                        holder.setVisible(R.id.tv_loadMore, View.GONE)
                        holder.setVisible(R.id.tv_loadEnd, View.VISIBLE)
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (dataSource.isEmpty()) 0 else dataSource.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            (itemCount - 1) -> {
                TYPE_FOOTER
            }
            else -> {
                TYPE_ITEM
            }
        }
    }

    fun setState(loadState: Int) {
        this.loadState = loadState
    }

    fun getState(): Int {
        return loadState
    }

    abstract fun convert(holder: CommonViewHolder, item: T, position: Int)

    abstract fun getItemLayoutID(): Int

    abstract fun getFootViewLayoutID(): Int
}