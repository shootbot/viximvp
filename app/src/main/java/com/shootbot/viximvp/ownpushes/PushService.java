package com.shootbot.viximvp.ownpushes;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import android.util.Log;

import androidx.annotation.Nullable;

import com.shootbot.viximvp.network.PushReceiver;

import static com.shootbot.viximvp.utilities.Constants.DEVICE_TOKEN;


public class PushService extends Service {
    PushReceiver pushReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        pushReceiver = new PushReceiver();
        registerReceiver(pushReceiver, new IntentFilter("com.shootbot.viximvp.ownpushes.NEW_PUSH"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("PushService", "started");

        performOnBackgroundThread(new LongPollingThread(this, intent.getStringExtra(DEVICE_TOKEN)));

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(pushReceiver);
    }
}
