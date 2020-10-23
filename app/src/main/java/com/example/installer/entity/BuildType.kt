package com.example.installer.entity

/**
@author zhangtao
@date   2020/10/24

 **/
data class BuildType(val buildTypes: String?) : ISelectable {
    private var buildType: String? = null
    init {
        this.buildType = buildTypes
    }
    override fun getID(): String {
        return "0"
    }

    override fun getName(): String? {
        return buildType;
    }

}