package com.example.androidsockettest;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;

import java.io.IOException;
import java.util.Random;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by quxia on 2018/4/22.
 */

public class AppUtils {
    /**
     * 唤醒手机屏幕并解锁
     */
    public static void wakeUpAndUnlock() {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) MyApplication.sContext.getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "ScreenWakeUp");
            // 点亮屏幕
            wl.acquire(5000);
            // 释放
            wl.release();
        }
        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) MyApplication.sContext.getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("UnlockScreen");
        // 屏幕锁定
        keyguardLock.reenableKeyguard();
        // 解锁
        keyguardLock.disableKeyguard();
    }

        // 播放默认铃声
        // 返回Notification id
        public static void playSound(final Context context) {

            final MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.in_call_alarm);
            mediaPlayer.setLooping(true);
//            try {
//                mediaPlayer.prepare();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            mediaPlayer.start();

           //铃声响5秒
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    mediaPlayer.pause();
                    mediaPlayer.stop();;
                    mediaPlayer.release();
                }
            }, 5000);

//            NotificationManager mgr = (NotificationManager) context
//                    .getSystemService(Context.NOTIFICATION_SERVICE);
//            Notification nt = new Notification();
//            nt.defaults = Notification.DEFAULT_SOUND;
//            int soundId = new Random(System.currentTimeMillis())
//                    .nextInt(Integer.MAX_VALUE);
//            mgr.notify(soundId, nt);
//            return soundId;
        }
}
