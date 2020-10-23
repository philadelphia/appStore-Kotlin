package com.example.installer

import android.Manifest
import android.app.AlertDialog
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
import com.example.installer.mvp.MvpContract.IView
import com.example.installer.mvp.MyPresenter
import com.example.installer.service.DownloadService
import com.example.installer.utils.EndlessRecyclerOnScrollListener
import com.example.installer.utils.NetWorkUtil
import com.example.installer.view.CustomBottomSheetDialog
import com.tbruyelle.rxpermissions.RxPermissions
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), IView, SwipeRefreshLayout.OnRefreshListener,
    CustomBottomSheetDialog.OnItemClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var presenter: MyPresenter
    private val defaultSystemType = "android"
    private var mBuildType: String? = null
    private var mApplicationID: String? = null
    private var pageIndex = 1
    private val DEFAULT_PAGE_SIZE = 20
    private var dataListSize = 0
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
        presenter = MyPresenter(this)
        binding.swipeRefreshLayout.setOnRefreshListener(this)
        initData()
        requestPermissions()
        registerReceiver()
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

        presenter.getApplicationList()
        presenter.getPackageList(
            defaultSystemType,
            mApplicationID,
            mBuildType,
            pageIndex
        )

        binding.filterBuildType.setTitle("正式/测试")
        binding.filterPackageName.setTitle("应用")
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
            presenter.getApplicationList()
        }
    }

    private fun doFilter(application_id: String?, version_type: String?) {
        binding.swipeRefreshLayout.isRefreshing = true
        pageIndex = 1
//        var sameApplication: Boolean = mBuildType?.equals(application_id)
//        var sameBuildType: Boolean = mApplicationID?.equals(version_type)
//        if (sameApplication && sameBuildType)
        this.mBuildType = version_type
        this.mApplicationID = application_id
        presenter.getPackageList(
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

    private fun registerReceiver() {
        receiver = MyReceiver();
        val intentFilter = IntentFilter(DownloadService.BROADCAST_ACTION)
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
    }

    private fun initAdapter() {
        adapter = object : CustomRecyclerAdapter<APKEntity>(mApplicationList) {
            override fun convert(holder: CommonViewHolder, apkEntity: APKEntity, position: Int) {
                Glide.with(holder.itemView.context)
                    .load(apkEntity.icon_url)
                    .into(holder.getView(R.id.img_icon) as ImageView)

                holder.setText(
                    R.id.tv_packageName,
                    apkEntity.application_name.toString() + "(" + apkEntity.version_name + ")"
                )

                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
                val crateTime =
                    simpleDateFormat.format(java.lang.Long.valueOf(apkEntity.create_time) * 1000)

                holder.setText(R.id.tv_timeStamp, crateTime)

                if (!TextUtils.isEmpty(apkEntity.version_type) && apkEntity.version_type.equals("测试")) {
                    holder.setVisible(R.id.tv_isDebugVersion, View.VISIBLE)
                    holder.setText(R.id.tv_isDebugVersion, apkEntity.version_type)
                } else {
                    holder.setVisible(R.id.tv_isDebugVersion, View.INVISIBLE)
                }

                holder.setOnClickListener(View.OnClickListener {
                    val toast: Toast = Toast.makeText(
                        this@MainActivity,
                        apkEntity.application_name + apkEntity.version_name + apkEntity.version_type
                            .toString() + "正在下载，请稍后.....",
                        Toast.LENGTH_LONG
                    )
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    val nameList: List<String>? = apkEntity.download_url?.split("/")
                    downLoadAPK(apkEntity.download_url, nameList?.get(nameList?.size - 1))
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
        binding.recyclerView.addOnScrollListener(object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore() {
                if (adapter?.loadState != CustomRecyclerAdapter.LOADING_END) {
                    adapter?.setLoadState(CustomRecyclerAdapter.LOADING)
                    presenter.getPackageList(
                        defaultSystemType,
                        mApplicationID,
                        mBuildType,
                        pageIndex
                    )
                }

            }

            override fun setFlag(flag: Boolean) {
                if (mApplicationList.size < dataListSize) {
                    EndlessRecyclerOnScrollListener.flag = false
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

    override fun onLoadApplicationListSuccess(dataSource: Iterable<PackageEntity>) {
        binding.swipeRefreshLayout.isRefreshing = false
        mPkgList.clear()
        val packageEntity = PackageEntity()
        packageEntity.application_name = "全部"
        mPkgList.add(0, packageEntity)
        mPkgList.addAll(dataSource)
    }

    override fun onLoadApplicationListFailed() {
        packageBottomSheetDialog.showProgressBar(false)
    }

    override fun onLoadPackageListSuccess(dataSource: List<APKEntity>) {
        binding.swipeRefreshLayout.isRefreshing = false
        if (dataSource.size < DEFAULT_PAGE_SIZE) {
            adapter?.setLoadState(CustomRecyclerAdapter.LOADING_END)
        } else {
            adapter?.setLoadState(CustomRecyclerAdapter.LOADING_COMPLETE)
            pageIndex++
        }

        mApplicationList.addAll(dataSource)
        adapter?.notifyItemRangeInserted(mApplicationList.size, dataSource.size)

    }

    override fun onLoadPackageListFailed() {
        adapter!!.setLoadState(CustomRecyclerAdapter.LOADING_COMPLETE)
        binding.statusLayout.setEmptyClick {
            presenter.getPackageList(
                defaultSystemType,
                mApplicationID,
                mBuildType,
                pageIndex
            )
        }
    }

    override fun notifyDataSize(count: Int) {
        dataListSize = count
    }

    override fun showContentView() {
        binding.statusLayout.showContentView()
    }

    override fun showErrorView() {
        binding.statusLayout.showErrorView()
        if (binding.swipeRefreshLayout.isRefreshing) {
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.statusLayout.setErrorClick {
            binding.statusLayout.showContentView()
            binding.swipeRefreshLayout.isRefreshing = true
            presenter.getPackageList(
                defaultSystemType,
                mApplicationID,
                mBuildType,
                pageIndex
            )
        }
    }

    override fun showEmptyView() {
        binding.statusLayout.showEmptyView()
        if (binding.swipeRefreshLayout.isRefreshing) {
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun checkNetWork() {
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

    override fun onError(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onRefresh() {
        mApplicationList.clear()
        adapter!!.notifyItemMoved(0, mApplicationList.size)
        pageIndex = 1
        presenter.getPackageList(
            defaultSystemType,
            mApplicationID,
            mBuildType,
            pageIndex
        )
    }

    override fun ontItemClick(view: View?, entity: ISelectable?, position: Int) {
        mApplicationList.clear()
        if (entity is PackageEntity) {
            if (position == 0) {
                mApplicationID = null
                binding.filterPackageName.isHighlight = false
            } else {
                mApplicationID = entity.getID()
                binding.filterPackageName.isHighlight = true
            }
            binding.filterPackageName.setTitle(entity.getName())
            doFilter(mApplicationID, mBuildType)
            packageBottomSheetDialog.dismiss()

        } else if (entity is BuildType) {
            mApplicationList.clear()
            if (position == 0) {
                mBuildType = null
                binding.filterBuildType.isHighlight = false
            } else {
                mBuildType = entity.name
                binding.filterBuildType.isHighlight = true
            }
            binding.filterBuildType.setTitle(entity.name)
            doFilter(mApplicationID, mBuildType)
            buildTypeBottomSheetDialog.dismiss()
        }
    }
}