package com.example.installer.entity

/**
@author zhangtao
@date   2020/10/27

 **/
class ProductEntity : ISelectable {
    private var id: String? = null
    var application_name: String? = null
    override fun getID(): String? {
        return id
    }

    override fun getName(): String? {
        return application_name
    }
}