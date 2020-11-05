package com.example.installer.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.example.installer.R
import com.example.installer.databinding.ViewFilterTabItemBinding

/**
@author zhangtao
@date   2020/10/28

 **/
class FilterTabItemView : RelativeLayout {
    private lateinit var binding: ViewFilterTabItemBinding
    private var isHighlight = false

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        initView(context)
    }

    constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributes,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        binding = ViewFilterTabItemBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setTitle(title: String?) {
        binding.tvFilterTitle.text = title
    }

    fun setHighLight(highLight: Boolean) {
        this.isHighlight = highLight
        if (isHighlight) {
            binding.tvFilterTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.titleTextSelectedColor
                )
            )
            binding.tvFilterTitle.compoundDrawables[2].level = 1
        } else {
            binding.tvFilterTitle.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.titleTextDefaultColor
                )
            )
            binding.tvFilterTitle.compoundDrawables[2].level = 0
        }
    }
}