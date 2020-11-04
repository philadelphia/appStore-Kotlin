package com.example.installer.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.installer.entity.PackageEntity
import com.example.installer.entity.ProductEntity
import com.example.installer.entity.Result
import com.example.installer.rx.ResultSubscriber
import rx.Observable
import rx.Subscription
import rx.subscriptions.CompositeSubscription

/**
@author zhangtao
@date   2020/11/3

 **/
class MainViewModel : ViewModel() {
    private val packageList: MutableLiveData<List<PackageEntity>?> = MutableLiveData()
    private val productList: MutableLiveData<List<ProductEntity>?> = MutableLiveData()
    private val dialogEvent: MutableLiveData<Boolean> = MutableLiveData()
    private val errorEvent: MutableLiveData<String?> = MutableLiveData()
    private val packageListResult = MutableLiveData<Boolean>()
    private val applicationListResult = MutableLiveData<Boolean>()
    private val compositeSubscription: CompositeSubscription by lazy {
        CompositeSubscription()
    }

    fun getProductList(){
        val observable: Observable<Result<List<ProductEntity>>> =
            MainRepository.getProductList()
        val subscribe: Subscription =
            observable.subscribe(object : ResultSubscriber<List<ProductEntity>>() {
                override fun onStart() {
                    super.onStart()
                    dialogEvent.value = true
                }

                override fun onSuccess(data: List<ProductEntity>?) {
                    dialogEvent.value = false
                    productList.value = data
                }

                override fun onError(message: String?) {
                    dialogEvent.value = false
                    errorEvent.value = message
                    applicationListResult.value = false;
                }
            })
        compositeSubscription.add(subscribe)
    }


    fun getProductList(
        system_name: String?,

        application_id: String?,

        version_type: String?,

        pageIndex: Int
    ) {
        var observable: Observable<Result<List<PackageEntity>>> =
            MainRepository.getPackageList(system_name, application_id, version_type, pageIndex)

        var subscribe = observable.subscribe(object :
            ResultSubscriber<List<PackageEntity>>() {
            override fun onSuccess(data: List<PackageEntity>?) {
                dialogEvent.value = false
                packageList.value = data
            }

            override fun onError(message: String?) {
                dialogEvent.value = false
                errorEvent.value = message
                packageListResult.value = false
            }
        })

        compositeSubscription.add(subscribe);
    }

    fun getPackageListLiveData(): MutableLiveData<List<PackageEntity>?> {
        return packageList
    }

    fun getDialogEvent(): MutableLiveData<Boolean> {
        return dialogEvent
    }

    fun getErrorEvent(): MutableLiveData<String?> {
        return errorEvent
    }

    fun getProductListLiveData(): MutableLiveData<List<ProductEntity>?> {
        return productList
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