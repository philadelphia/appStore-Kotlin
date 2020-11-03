package com.example.installer.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.installer.entity.APKEntity
import com.example.installer.entity.KtResult
import com.example.installer.entity.PackageEntity
import com.example.installer.rx.ResultSubscriber
import rx.Observable
import rx.Subscription
import rx.subscriptions.CompositeSubscription

/**
@author zhangtao
@date   2020/11/3

 **/
class MainViewModel : ViewModel() {
    private val apkList: MutableLiveData<List<APKEntity>> = MutableLiveData()
    private val packageList: MutableLiveData<List<PackageEntity>> = MutableLiveData()
    private val dialogEvent: MutableLiveData<Boolean> = MutableLiveData()
    private val errorEvent: MutableLiveData<String?> = MutableLiveData()
    private val packageListResult = MutableLiveData<Boolean>()
    private val applicationListResult = MutableLiveData<Boolean>()
    private var compositeSubscription: CompositeSubscription

    init {
        compositeSubscription = CompositeSubscription()
    }


    fun getApplicationList() {
        val observable: Observable<KtResult<List<PackageEntity>?>?> =
            MainRepository.getApplicationList()
        val subscribe: Subscription =
            observable.subscribe(object : ResultSubscriber<List<PackageEntity>?>() {
                override fun onStart() {
                    super.onStart()
                    dialogEvent.value = true
                }

                override fun onSuccess(data: List<PackageEntity>?) {
                    dialogEvent.value = false
                    packageList.value = data
                }

                override fun onError(message: String?) {
                    dialogEvent.value = false
                    errorEvent.value = message
                    applicationListResult.value = false;
                }
            })
        compositeSubscription.add(subscribe)
    }


    fun getPackageList(
        system_name: String?,

        application_id: String?,

        version_type: String?,

        pageIndex: Int
    ) {
        var packageList:Observable<KtResult<List<APKEntity>?>?> =
            MainRepository.getPackageList(system_name, application_id, version_type, pageIndex)

        var subscribe = packageList.subscribe(object :
            ResultSubscriber<List<APKEntity>?>() {
            override fun onSuccess(data: List<APKEntity>?) {
                dialogEvent.value = false
                apkList.value = data
            }

            override fun onError(message: String?) {
                dialogEvent.value = false
                errorEvent.value = message
                packageListResult.value = false
            }
        })

        compositeSubscription.add(subscribe);
    }

    fun getApkList(): MutableLiveData<List<APKEntity>> {
        return apkList
    }

    fun getDialogEvent(): MutableLiveData<Boolean> {
        return dialogEvent
    }

    fun getErrorEvent(): MutableLiveData<String?> {
        return errorEvent
    }

    fun getPackageList(): MutableLiveData<List<PackageEntity>> {
        return packageList
    }


    fun getPackageListResult(): MutableLiveData<Boolean> {
        return packageListResult
    }

    fun getApplicationListResult(): MutableLiveData<Boolean> {
        return applicationListResult
    }

    override fun onCleared() {
        super.onCleared()
    }

}