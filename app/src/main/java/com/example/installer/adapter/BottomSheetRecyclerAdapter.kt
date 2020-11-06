package com.example.installer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView

/**
@author zhangtao
@date   2020/11/3

 **/
abstract class BottomSheetRecyclerAdapter<T>(dataSource: List<T>) :
    RecyclerView.Adapter<CommonViewHolder>() {
    private var dataSource: List<T> = dataSource

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): CommonViewHolder {
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