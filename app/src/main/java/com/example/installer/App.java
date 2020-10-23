package com.example.installer;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.example.installer.rx.ResponseErrorListener;
import com.liulishuo.filedownloader.FileDownloader;

/**
 * Author:  ZhangTao
 * Date: 2018/3/9.
 */

public class App extends Application implements ResponseErrorListener {
    private static App mInstance ;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        FileDownloader.setup(this);
    }

    public static Application getInstance() {
        return mInstance;
    }

    @Override
    public void handlerResponseError(Context context, Exception e) {
        Toast.makeText(context, e.getMessage() + e.getCause(), Toast.LENGTH_LONG).show();
    }
}
