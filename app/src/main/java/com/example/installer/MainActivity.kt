package com.example.installer

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
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.installer.adapter.CommonViewHolder
import com.example.installer.adapter.CustomRecyclerAdapter
import com.example.installer.databinding.ActivityMainBinding
import com.example.installer.entity.APKEntity
import com.example.installer.entity.BuildType
import com.example.installer.entity.ISelectable
import com.example.installer.entity.PackageEntity
import com.example.installer.mvvm.MainViewModel
import com.example.installer.service.KtDownloadService
import com.example.installer.utils.CustomRecyclerOnScrollListener
import com.example.installer.utils.NetWorkUtil
import com.example.installer.view.CustomBottomSheetDialog
import com.tbruyelle.rxpermissions.RxPermissions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener,
    CustomBottomSheetDialog.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private val defaultSystemType = "android"
    private var mBuildType: String? = null
    private var mApplicationID: String? = null
    private var pageIndex = 1
    private val DEFAULT_PAGE_SIZE = 20
    private val mPkgList: MutableList<PackageEntity> = ArrayList()
    private val mApplicationList: MutableList<APKEntity> = ArrayList()
    private val mBuildTypeList: MutableList<BuildType> = ArrayList()
    private var adapter: CustomRecyclerAdapter<APKEntity>? = null

    private val packageBottomSheetDialog: CustomBottomSheetDialog by lazy {
        CustomBottomSheetDialog(this)
    }

    private val buildTypeBottomSheetDialog: CustomBottomSheetDialog by lazy {
        CustomBottomSheetDialog(this)
    }

    private lateinit var receiver: MyReceiver

    companion object {
        val DOWNLOAD_PATH: String = Environment.DIRECTORY_DOWNLOADS + "/installer_app"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = MainViewModel()
        binding.swipeRefreshLayout.setOnRefreshListener(this)
        initData()
        requestPermissions()
        initAdapter()

        binding.filterBuildType.setOnClickListener { showBuildTypeDialog(mBuildTypeList) }

        binding.filterPackageName.setOnClickListener({
            showApkNameDialog(mPkgList)
        })

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
        mainViewModel.getApplicationList()
        mainViewModel.getPackageList(
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

    private fun showApkNameDialog(dataSource: MutableList<PackageEntity>) {
        packageBottomSheetDialog.setDataList(dataSource)
        packageBottomSheetDialog.show()
        packageBottomSheetDialog.setOnItemClickListener(this)
        if (mPkgList.size == 0) {
            packageBottomSheetDialog.showProgressBar(true)
            mainViewModel.getApplicationList()
        }
    }

    private fun doFilter(application_id: String?, version_type: String?) {
        binding.swipeRefreshLayout.isRefreshing = true
        pageIndex = 1
        if (!NetWorkUtil.isNetConnected()) {
            binding.statusLayout.showNoNetView()
            return
        }
        mApplicationList.clear()
        adapter?.notifyDataSetChanged()
        this.mBuildType = version_type
        this.mApplicationID = application_id
        mainViewModel.getPackageList(
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

        mainViewModel.getDialogEvent().observe(this, Observer { aBoolean ->
            binding.swipeRefreshLayout.isRefreshing = aBoolean
        })

        mainViewModel.getErrorEvent().observe(this, Observer { errosMessage ->
            onError(errosMessage)
        })

        mainViewModel.getApkList()
            .observe(this@MainActivity, Observer { packageList ->
                onLoadPackageListSuccess(packageList)
            })

        mainViewModel.getPackageListResult().observe(this, Observer { aBoolean ->
            if (!aBoolean) {
                onLoadApplicationListFailed()
            }
        })

        mainViewModel.getPackageList().observe(this, Observer { packageList ->
            onLoadApplicationListSuccess(packageList)
        })

        mainViewModel.getApplicationListResult().observe(
            this, Observer { aBoolean ->
                if (!aBoolean) {
                    onLoadPackageListFailed()
                }
            })

    }

    private fun openWiFiSetting(context: Context?) {
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        if (context is Activity) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context?.startActivity(intent)
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
                        ) { dialog, which -> requestPermissions() }
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
        receiver = MyReceiver()
        val intentFilter = IntentFilter(KtDownloadService.BROADCAST_ACTION)
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)

    }

    private fun initAdapter() {
        adapter = object : CustomRecyclerAdapter<APKEntity>(mApplicationList) {
            override fun convert(holder: CommonViewHolder, item: APKEntity, position: Int) {
                Glide.with(holder.itemView.context)
                    .load(item.icon_url)
                    .into(holder.getView(R.id.img_icon) as ImageView)

                holder.setText(
                    R.id.tv_packageName,
                    item.application_name.toString() + "(" + item.version_name + ")"
                )

                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
                val crateTime =
                    simpleDateFormat.format(java.lang.Long.valueOf(item.create_time) * 1000)

                holder.setText(R.id.tv_timeStamp, crateTime)

                if (!TextUtils.isEmpty(item.version_type) && item.version_type.equals("测试")) {
                    holder.setVisible(R.id.tv_isDebugVersion, View.VISIBLE)
                    holder.setText(R.id.tv_isDebugVersion, item.version_type)
                } else {
                    holder.setVisible(R.id.tv_isDebugVersion, View.INVISIBLE)
                }

                holder.setOnClickListener(View.OnClickListener {
                    val toast: Toast = Toast.makeText(
                        this@MainActivity,
                        item.application_name + item.version_name + item.version_type
                            .toString() + "正在下载，请稍后.....",
                        Toast.LENGTH_LONG
                    )
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    val nameList: List<String>? = item.download_url?.split("/")
                    downLoadAPK(item.download_url, nameList?.get(nameList?.size - 1))
                }, R.id.btn_downLoad)
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
        binding.recyclerView.setOnTouchListener { v, event -> binding.swipeRefreshLayout.isRefreshing }
        binding.recyclerView.addOnScrollListener(object : CustomRecyclerOnScrollListener() {
            override fun onLoadMore() {
                if (adapter?.getState() != CustomRecyclerAdapter.LOADING_END) {
                    adapter?.setState(CustomRecyclerAdapter.LOADING)
                    mainViewModel.getPackageList(
                        defaultSystemType,
                        mApplicationID,
                        mBuildType,
                        pageIndex
                    )
                }

            }

            override fun setFlag(flag: Boolean) {

            }
        })

    }

    private fun downLoadAPK(url: String?, fileName: String?) {
        val serviceIntent = Intent(this, KtDownloadService.javaClass)
        serviceIntent.data = Uri.parse(url)
        serviceIntent.putExtra(KtDownloadService.FILE_NAME, fileName)
        serviceIntent.putExtra(KtDownloadService.DOWNLOAD_PATH, DOWNLOAD_PATH)
        KtDownloadService.enqueueWork(this, serviceIntent)
    }

    private fun onLoadApplicationListSuccess(dataSource: Iterable<PackageEntity>) {
        binding.swipeRefreshLayout.isRefreshing = false
        mPkgList.clear()
        val packageEntity = PackageEntity()
        packageEntity.application_name = "全部"
        mPkgList.add(0, packageEntity)
        mPkgList.addAll(dataSource)
    }

    private fun onLoadApplicationListFailed() {
        packageBottomSheetDialog.showProgressBar(false)
    }

    private fun onLoadPackageListSuccess(dataSource: List<APKEntity>) {
        showContentView()
        binding.swipeRefreshLayout.isRefreshing = false
        if (dataSource.size < DEFAULT_PAGE_SIZE) {
            adapter?.setState(CustomRecyclerAdapter.LOADING_END)
            CustomRecyclerOnScrollListener.flag = false
        } else {
            adapter?.setState(CustomRecyclerAdapter.LOADING_COMPLETE)
            pageIndex++
        }

        mApplicationList.addAll(dataSource)
        if (mApplicationList?.isEmpty()) {
            showEmptyView()
        } else {
            showContentView()
        }
        adapter?.notifyItemRangeInserted(mApplicationList.size, dataSource.size)

    }

    private fun onLoadPackageListFailed() {
        adapter!!.setState(CustomRecyclerAdapter.LOADING_COMPLETE)
        if (mPkgList?.size == 0) {
            binding.statusLayout.showEmptyView()
        }
        binding.statusLayout.setEmptyClick {
            mainViewModel.getPackageList(
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
            binding.statusLayout.showContentView()
            binding.swipeRefreshLayout.isRefreshing = true
            mainViewModel.getPackageList(
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

    private fun checkNetWork() {
        if (!NetWorkUtil.isNetConnected()) {
            val builder = AlertDialog.Builder(this)
                .setTitle("alert")
                .setMessage("当前网络不可用，请检查网络设置")
                .setNegativeButton(
                    "取消"
                ) { dialogInterface, i -> dialogInterface.cancel() }
                .setPositiveButton(
                    "确定"
                ) { dialogInterface, i ->
                    val intent = Intent()
                    intent.action = Settings.ACTION_SETTINGS
                    startActivity(intent)
                }
            val alertDialog = builder.create()
            alertDialog.show()
            alertDialog.setCanceledOnTouchOutside(false)
        }
    }

    private fun onError(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onRefresh() {
        mApplicationList.clear()
        adapter!!.notifyItemMoved(0, mApplicationList.size)
        pageIndex = 1
        mainViewModel.getPackageList(
            defaultSystemType,
            mApplicationID,
            mBuildType,
            pageIndex
        )
    }

    override fun ontItemClick(view: View?, entity: ISelectable?, position: Int) {
        if (entity is PackageEntity) {
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
            mApplicationList.clear()
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