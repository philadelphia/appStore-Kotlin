package com.example.installer.entity

/**
@author zhangtao
@date   2020/10/27

 **/
data class ProductEntity(var id: String?, var application_name: String?) : ISelectable {

    override fun getID(): String? {
        return id
    }

    override fun getName(): String? {
        return application_name
    }
}