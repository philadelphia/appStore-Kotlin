package com.example.installer.interceptor;

import android.util.Log;

import com.example.installer.utils.NetWorkUtil;
import com.example.installer.utils.StringUtil;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Author Chaney
 * Date   2017/6/15 下午4:25.
 * EMail  qiuzhenhuan.hi@gmail.com
 */

public class LocalCacheInterceptor implements Interceptor {
    private static final String TAG = "REQUEST";
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if(!NetWorkUtil.isNetConnected()){
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }else {
            String maxAge = request.header("Cache-Control");
            if(!StringUtil.isEmpty(maxAge) && "0".equals(maxAge)){
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build();
            }
            Log.i(TAG, "intercept: " + request.url());
        }


        return chain.proceed(request);
    }
}
