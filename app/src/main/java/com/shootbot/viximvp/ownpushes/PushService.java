package com.shootbot.viximvp.ownpushes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.util.Log;

import androidx.annotation.Nullable;


public class PushService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("PushService", "started");

        performOnBackgroundThread(new LongPollingThread());

        return Service.START_STICKY;//super.onStartCommand(intent, flags, startId);
    }

    public static Thread performOnBackgroundThread(Runnable runnable) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {
                    Log.d("PushService", "performOnBackgroundThread finally");
                }
            }
        };
        t.start();
        return t;
    }
}
