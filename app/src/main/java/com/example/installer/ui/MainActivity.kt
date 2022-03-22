package com.example.installer.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.installer.R
import com.example.installer.adapter.CommonViewHolder
import com.example.installer.adapter.CustomRecyclerAdapter
import com.example.installer.databinding.ActivityMainBinding
import com.example.installer.entity.BuildType
import com.example.installer.entity.ISelectable
import com.example.installer.entity.PackageEntity
import com.example.installer.entity.ProductEntity
import com.example.installer.mvvm.MainViewModel
import com.example.installer.receiver.MyReceiver
import com.example.installer.service.DownloadService
import com.example.installer.utils.CustomRecyclerOnScrollListener
import com.example.installer.utils.NetWorkUtil
import com.example.installer.view.CustomBottomSheetDialog
import com.tbruyelle.rxpermissions.RxPermissions


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener,
    CustomBottomSheetDialog.OnItemClickListener {
    private var mBuildType: String? = null
    private var mApplicationID: String? = null
    private var pageIndex = 1

    private val defaultSystemType = "android"
    private val mProductList = mutableListOf<ProductEntity>()
    private val mPackageList = mutableListOf<PackageEntity>()
    private val mBuildTypeList = mutableListOf<BuildType>()

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(MainActivity@this).get(MainViewModel::class.java)
    }
    private lateinit var adapter: CustomRecyclerAdapter<PackageEntity>

    private val packageBottomSheetDialog: CustomBottomSheetDialog by lazy {
        CustomBottomSheetDialog(this)
    }

    private val buildTypeBottomSheetDialog: CustomBottomSheetDialog by lazy {
        CustomBottomSheetDialog(this)
    }


    private val receiver: MyReceiver by lazy {
        MyReceiver()
    }
    private var isLoadCompleted = false

    companion object {
        val DOWNLOAD_PATH: String = Environment.DIRECTORY_DOWNLOADS + "/installer_app"
        const val DEFAULT_PAGE_SIZE = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.swipeRefreshLayout.setOnRefreshListener(this)
        initData()
        initAdapter()
        requestPermissions()

        binding.filterBuildType.setOnClickListener { showBuildTypeDialog(mBuildTypeList) }
        binding.filterPackageName.setOnClickListener { showApkNameDialog(mProductList) }

        //请求数据
        binding.swipeRefreshLayout.isRefreshing = true
        binding.swipeRefreshLayout.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.secodePrimaryColor),
            ContextCompat.getColor(this, R.color.primaryColor)
        )

        binding.filterBuildType.setTitle("正式/测试")
        binding.filterPackageName.setTitle("应用")

        if (!NetWorkUtil.isNetConnected()) {
            binding.statusLayout.showNoNetView()
            binding.statusLayout.setNoNetClick { v ->
                if (NetWorkUtil.isNetConnected()) {
                    getData()
                } else {
                    openWiFiSetting(v.context)
                }
            }
            return
        }

        getData()
    }

    private fun getData() {
        mainViewModel.getProductList()
        mainViewModel.getProductList(
            defaultSystemType,
            mApplicationID,
            mBuildType,
            pageIndex
        )
    }

    private fun showBuildTypeDialog(buildTypes: MutableList<BuildType>) {
        buildTypeBottomSheetDialog.setDataList(buildTypes)
        buildTypeBottomSheetDialog.show()
        buildTypeBottomSheetDialog.setOnItemClickListener(this)
    }

    private fun showApkNameDialog(dataSource: MutableList<ProductEntity>) {
        packageBottomSheetDialog.setDataList(dataSource)
        packageBottomSheetDialog.show()
        packageBottomSheetDialog.setOnItemClickListener(this)
        if (mProductList.size == 0) {
            packageBottomSheetDialog.showProgressBar(true)
            mainViewModel.getProductList()
        }
    }

    private fun doFilter(application_id: String?, version_type: String?) {
        binding.swipeRefreshLayout.isRefreshing = true
        pageIndex = 1
        isLoadCompleted = false
        if (!NetWorkUtil.isNetConnected()) {
            binding.statusLayout.showNoNetView()
            return
        }
        mPackageList.clear()
        adapter.notifyDataSetChanged()
        this.mBuildType = version_type
        this.mApplicationID = application_id
        mainViewModel.getProductList(
            defaultSystemType,
            application_id,
            version_type,
            pageIndex
        )
    }

    private fun initData() {
        mBuildTypeList.add(BuildType("全部"))
        mBuildTypeList.add(BuildType("正式"))
        mBuildTypeList.add(BuildType("测试"))

        mainViewModel.getDialogEvent().observe(this) {
            binding.swipeRefreshLayout.isRefreshing = it
        }

        mainViewModel.getErrorEvent().observe(this) {
            onError(it)
        }

        mainViewModel.getPackageListLiveData().observe(this) {
            onLoadPackageListSuccess(it)
        }

        mainViewModel.getPackageListResult().observe(this) {
            if (!it && pageIndex == 1) {
                onLoadApplicationListFailed()
                showErrorView()
            }
        }

        mainViewModel.getProductListLiveData().observe(this) {
            onLoadProductListSuccess(it)
        }

        mainViewModel.getApplicationListResult().observe(this) {
            if (!it) {
                onLoadPackageListFailed()
            }
        }

    }

    private fun openWiFiSetting(context: Context) {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        if (context !is Activity) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun requestPermissions() {
        val rxPermissions = RxPermissions(this)
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe { granted ->
                if (!granted!!) {
                    val builder = AlertDialog.Builder(this@MainActivity)
                        .setTitle("提示")
                        .setMessage("请务必给予存储权限，以便您的使用")
                        .setPositiveButton(
                            "确定"
                        ) { _, _ -> requestPermissions() }
                    builder.create().show()
                }
            }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(DownloadService.BROADCAST_ACTION)
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)

    }

    private fun initAdapter() {
        adapter = object : CustomRecyclerAdapter<PackageEntity>(mPackageList) {
            override fun convert(holder: CommonViewHolder, item: PackageEntity, position: Int) {
                with(item) {
                    holder.getView<ImageView>(R.id.img_icon)?.let {
                        Glide.with(holder.itemView.context)
                            .load(icon_url)
                            .into(it)
                    }

                    holder.setText(R.id.tv_packageName, application_name)
                    holder.setText(R.id.tv_timeStamp, create_time)

                    if ("测试" == version_type) {
                        holder.setVisible(R.id.tv_isDebugVersion, View.VISIBLE)
                        holder.setText(R.id.tv_isDebugVersion, version_type)
                    } else {
                        holder.setVisible(R.id.tv_isDebugVersion, View.INVISIBLE)
                    }

                    holder.setOnClickListener(R.id.btn_downLoad) {
                        val toast: Toast = Toast.makeText(
                            this@MainActivity,
                            application_name + version_name + version_type + "正在下载，请稍后.....",
                            Toast.LENGTH_LONG
                        )
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        val nameList: List<String>? = download_url?.split("/")
                        downLoadAPK(download_url, nameList?.get(nameList.size - 1))
                    }
                }
            }

            override fun getItemLayoutID(): Int {
                return R.layout.layout_recyclerview_package_item
            }

            override fun getFootViewLayoutID(): Int {
                return R.layout.layout_footer_view
            }
        }

        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter
//        binding.recyclerView.setOnTouchListener { _, _ -> binding.swipeRefreshLayout.isRefreshing }
        binding.recyclerView.addOnScrollListener(
            object : CustomRecyclerOnScrollListener() {
                override fun onLoadMore() {
                    if (adapter.getState() != CustomRecyclerAdapter.LOADING_END && !isLoadCompleted) {
                        adapter.setState(CustomRecyclerAdapter.LOADING)
                        mainViewModel.getProductList(
                            defaultSystemType,
                            mApplicationID,
                            mBuildType,
                            pageIndex
                        )
                    }
                }
            })
    }

    private fun downLoadAPK(url: String?, fileName: String?) {
        val serviceIntent = Intent(this, DownloadService::class.java)
        serviceIntent.data = Uri.parse(url)
        serviceIntent.putExtra(DownloadService.FILE_NAME, fileName)
        serviceIntent.putExtra(DownloadService.DOWNLOAD_PATH, DOWNLOAD_PATH)
        DownloadService.enqueueWork(this, serviceIntent)
    }

    private fun onLoadProductListSuccess(dataSource: List<ProductEntity>?) {
        binding.swipeRefreshLayout.isRefreshing = false
        mProductList.clear()
        val packageEntity = ProductEntity("0",null)
        packageEntity.application_name = "全部"
        mProductList.add(0, packageEntity)
        dataSource?.let {
            mProductList.addAll(it)
        }
    }

    private fun onLoadApplicationListFailed() {
        packageBottomSheetDialog.showProgressBar(false)
    }

    private fun onLoadPackageListSuccess(dataSource: List<PackageEntity>?) {
        binding.swipeRefreshLayout.isRefreshing = false
        dataSource?.let {
            if (dataSource.size < DEFAULT_PAGE_SIZE) {
                adapter.setState(CustomRecyclerAdapter.LOADING_END)
                isLoadCompleted = true
            } else {
                adapter.setState(CustomRecyclerAdapter.LOADING_COMPLETE)
                pageIndex++
            }

            mPackageList.addAll(dataSource)
            adapter.notifyItemRangeInserted(mPackageList.size, dataSource.size)
        }


        if (mPackageList.isEmpty()) {
            showEmptyView()
        } else {
            showContentView()
        }

    }

    private fun onLoadPackageListFailed() {
        adapter.setState(CustomRecyclerAdapter.LOADING_COMPLETE)
        if (mProductList.size == 0) {
            binding.statusLayout.showEmptyView()
        }
        binding.statusLayout.setEmptyClick {
            mainViewModel.getProductList(
                defaultSystemType,
                mApplicationID,
                mBuildType,
                pageIndex
            )
        }
    }

    private fun showContentView() {
        binding.statusLayout.showContentView()
    }

    private fun showErrorView() {
        binding.statusLayout.showErrorView()
        if (binding.swipeRefreshLayout.isRefreshing) {
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.statusLayout.setErrorClick {
            binding.swipeRefreshLayout.isRefreshing = true
            mainViewModel.getProductList(
                defaultSystemType,
                mApplicationID,
                mBuildType,
                pageIndex
            )
        }
    }

    private fun showEmptyView() {
        binding.statusLayout.showEmptyView()
        if (binding.swipeRefreshLayout.isRefreshing) {
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun onError(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onRefresh() {
        mPackageList.clear()
        adapter.notifyItemMoved(0, mPackageList.size)
        pageIndex = 1
        mainViewModel.getProductList(
            defaultSystemType,
            mApplicationID,
            mBuildType,
            pageIndex
        )
    }

    override fun ontItemClick(view: View?, entity: ISelectable?, position: Int) {
        if (entity is ProductEntity) {
            if (position == 0) {
                mApplicationID = null
                binding.filterPackageName.setHighLight(false)
            } else {
                mApplicationID = entity.getID()
                binding.filterPackageName.setHighLight(true)
            }
            binding.filterPackageName.setTitle(entity.getName())
            doFilter(mApplicationID, mBuildType)
            packageBottomSheetDialog.dismiss()

        } else if (entity is BuildType) {
            mPackageList.clear()
            if (position == 0) {
                mBuildType = null
                binding.filterBuildType.setHighLight(false)
            } else {
                mBuildType = entity.buildType
                binding.filterBuildType.setHighLight(true)
            }
            binding.filterBuildType.setTitle(entity.buildType)
            doFilter(mApplicationID, mBuildType)
            buildTypeBottomSheetDialog.dismiss()
        }
    }
}