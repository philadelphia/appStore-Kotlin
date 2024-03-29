package com.example.installer.entity

import java.text.SimpleDateFormat
import java.util.*

/**
@author zhangtao
@date   2020/10/23

 **/
data class PackageEntity(var id: String?) : ISelectable {
    /**
     * id : 464
     * version_name : 4.9.3
     * application_id : 1
     * application_name : 美丽屋经纪人
     * system_name : android
     * version_describe : null
     * download_url : http://cdn.mse.mlwplus.com/meiliwu/applications/2018/02/02/18/37/mlw_release_V4.9.3_2018_02_02_18.apk
     * plist_url :
     * version_type : 正式
     * if_deleted : 0
     * status : 1
     * create_time : 1517550821
     * update_time : 1517567888
     * update_type : 1
     * uid : 50200d0ba8207c4b80a17744
     * icon_url : http://cdn.mse.mlwplus.com/meiliwu/applications/2018/03/05/18/23/jingjiren.png
     */
    var version_name: String? = null
    var application_id: String? = null
    var application_name: String? = null
        get() {
            return field + "(" + version_name + ")"
        }
    var system_name: String? = null
    var download_url: String? = null
    var version_type: String? = null
    var create_time: String? = null
        get() {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault())
            field?.let { return simpleDateFormat.format(field?.toLong()?.times(1000)) }
            return null
        }


    var icon_url: String? = null
    override fun getID(): String? {
        return id
    }

    override fun getName(): String? {
        return application_name
    }
}