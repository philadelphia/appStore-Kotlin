object ThirdParty {
    object Version {
        const val gsonVersion = "2.8.2"
        const val retrofitVersion = "2.3.0"
        const val okHttpVersion = "3.12.0"
        const val okHttpLoggingInterceptor = "3.9.1"
        const val rxPermissionsVersion = "0.9.3@aar"
        const val rxJavaVersion = "1.3.6"
        const val rxAndroidVersion = "1.2.1"
        const val autoLayoutVersion = "1.4.5"
        const val fileDownloaderVersion = "1.7.3"
        const val glideVersion = "4.10.0"

    }

    const val retrofit = "com.squareup.retrofit2:retrofit:${Version.retrofitVersion}"
    const val retrofitConverters =
        "com.squareup.retrofit2:retrofit-converters:${Version.retrofitVersion}"
    const val retrofitAdapters =
        "com.squareup.retrofit2:retrofit-adapters:${Version.retrofitVersion}"
    const val converterGson = "com.squareup.retrofit2:converter-gson:${Version.retrofitVersion}"
    const val adapterRxjava = "com.squareup.retrofit2:adapter-rxjava:${Version.retrofitVersion}"
    const val okHttp = "com.squareup.okhttp3:okhttp:${Version.okHttpVersion}"
    const val okHttpLoggingInterceptor =
        "com.squareup.okhttp3:logging-interceptor:${Version.okHttpLoggingInterceptor}"
    const val rxPermissions =
        "com.tbruyelle.rxpermissions:rxpermissions:${Version.rxPermissionsVersion}"
    const val gson = "com.google.code.gson:gson:${Version.gsonVersion}"

    const val rxJava = "io.reactivex:rxjava:${Version.rxJavaVersion}"
    const val rxAndroid = "io.reactivex:rxandroid:${Version.rxAndroidVersion}"

    const val autoLayout = "com.zhy:autolayout:${Version.autoLayoutVersion}"
    const val fileDownloader =
        "com.liulishuo.filedownloader:library:${Version.fileDownloaderVersion}"
    const val glide = "com.github.bumptech.glide:glide:${Version.glideVersion}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Version.glideVersion}"
}