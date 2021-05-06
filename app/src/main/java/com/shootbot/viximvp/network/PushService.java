package com.shootbot.viximvp.network;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PushService extends Service {
    ExecutorService executorService;
    Handler mainThreadHandler;

    public PushService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    static boolean isStarted = false;
    static Timer timer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // executorService = Executors.newFixedThreadPool(1);
        // mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());

        return Service.START_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {
        timer.cancel();
        isStarted = false;
        timer = null;
        return super.stopService(name);
    }


    public static void start(Context cx) {
        if (!isStarted) {
            cx.startService(new Intent(cx, PushService.class));
        }
    }

}
