package com.example.installer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

/**
@author zhangtao
@date   2020/11/3

 **/
abstract class KtBottomSheetRecyclerAdapter<T> : RecyclerView.Adapter<CommonViewHolder> {
    private var dataSource: List<T> = ArrayList()

    constructor(dataSource: List<T>) {
        this.dataSource = dataSource
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(getItemLayoutID(), parent, false)
        return CommonViewHolder(itemView)
    }

    override fun onBindViewHolder(@NonNull holder: CommonViewHolder, position: Int) {
        convert(holder, dataSource[position], position)
    }

    override fun getItemCount(): Int {
        return if (dataSource == null) 0 else dataSource.size
    }


    abstract fun convert(holder: CommonViewHolder, item: T, position: Int)

    abstract fun getItemLayoutID(): Int
}