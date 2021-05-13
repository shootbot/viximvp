package com.shootbot.viximvp.ownpushes;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.Timer;


public class PushServiceTemplate extends Service {
    public PushServiceTemplate() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    static boolean isStarted = false;
    static Timer timer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (!isStarted) {
                timer = new Timer();
                timer.schedule(new CheckUpdateTimerTask(new Handler()), 60000L, 5L * 60L * 1000);
                isStarted = true;
            }
        } catch (Exception e) {
            Log.d("PushService", e.getMessage());
            isStarted = false;
        }
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
            cx.startService(new Intent(cx, PushServiceTemplate.class));
        }
    }

}
