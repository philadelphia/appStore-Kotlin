package com.example.installer.entity

/**
@author zhangtao
@date   2020/10/27

 **/
class PackageEntity : ISelectable {
    private var id: String? = null
    var application_name: String? = null
    private var create_time: String? = null
    private var update_time: String? = null
    private var icon_url: String? = null
    override fun getID(): String? {
        return id
    }

    override fun getName(): String? {
        return application_name
    }
}