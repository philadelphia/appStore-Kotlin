package com.example.installer.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.example.installer.R
import com.example.installer.adapter.CommonViewHolder
import com.example.installer.adapter.CustomItemDecoration
import com.example.installer.adapter.BottomSheetRecyclerAdapter
import com.example.installer.adapter.OnRecyclerViewItemClickListener
import com.example.installer.databinding.LayoutBottomSheetDialogBinding
import com.example.installer.entity.ISelectable
import com.google.android.material.bottomsheet.BottomSheetDialog


/**
@author zhangtao
@date   2020/10/24

 **/
class CustomBottomSheetDialog : View {
    private val dataSource: MutableList<ISelectable> = ArrayList()
    private var onItemClickListener: OnItemClickListener? = null
    private var selectItemName: String? = null
    private val binding: LayoutBottomSheetDialogBinding by lazy {
        LayoutBottomSheetDialogBinding.inflate(LayoutInflater.from(context))
    }

    private val bottomSheetDialog: BottomSheetDialog by lazy {
        BottomSheetDialog(context)
    }

    private val mAdapter: BottomSheetRecyclerAdapter<ISelectable> by lazy {
        object : BottomSheetRecyclerAdapter<ISelectable>(dataSource) {

            override fun convert(holder: CommonViewHolder, item: ISelectable, position: Int) {
                holder.setChecked(R.id.tv_packageName, item.getName() == selectItemName)
                holder.setText(R.id.tv_packageName, item.getName())
            }

            override fun getItemLayoutID(): Int {
                return R.layout.layout_bottom_sheet_dialog_item
            }
        }
    }

    init {
        bottomSheetDialog.setContentView(binding.root)
        initAdapter()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    interface OnItemClickListener {
        fun ontItemClick(view: View?, packageEntity: ISelectable?, position: Int)
    }

    private fun initAdapter() {
        binding.recyclerView.layoutManager =
            GridLayoutManager(this@CustomBottomSheetDialog.context, 2)
        binding.recyclerView.addItemDecoration(CustomItemDecoration(2))
        binding.recyclerView.adapter = mAdapter
        binding.recyclerView.addOnItemTouchListener(
            object : OnRecyclerViewItemClickListener(binding.recyclerView) {
                override fun onItemClick(view: View?, position: Int) {
                    val entity = dataSource[position]
                    selectItemName = entity.getName()
                    onItemClickListener!!.ontItemClick(view, entity, position)
                }

                override fun onItemLongClick(view: View?, position: Int) {}
            })

    }

    fun show() {
        if (!bottomSheetDialog.isShowing) {
            bottomSheetDialog.show()
        }
    }

    fun dismiss() {
        if (bottomSheetDialog.isShowing) {
            bottomSheetDialog.dismiss()
        }
    }

    fun showProgressBar(flag: Boolean) {
        if (flag) {
            binding.progressBar.visibility = VISIBLE
        } else {
            binding.progressBar.visibility = GONE
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    fun setDataList(dataList: MutableList<out ISelectable>) {
        showProgressBar(false)
        dataSource.clear()
        dataSource.addAll(dataList)
        mAdapter.notifyDataSetChanged()
    }

}