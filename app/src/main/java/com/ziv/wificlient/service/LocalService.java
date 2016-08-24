package com.ziv.wificlient.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.ziv.wificlient.threads.ListenThread;

public class LocalService extends Service {

    private static final String TAG = "LocalService";
    private IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void startWaitDataThread(Handler handler) {
        new ListenThread(12345, handler).start();
    }

    //定义内容类继承Binder
    public class LocalBinder extends Binder {
        //返回本地服务
        public LocalService getService() {
            return LocalService.this;
        }
    }
}
