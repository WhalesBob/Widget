package com.example.widgetproject;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class BatteryService extends Service {
    private static BroadcastReceiver screenOffReceiver;
    private static BroadcastReceiver screenOnReceiver;
    private static BroadcastReceiver userPresentReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){

        registerScreenOffReceiver();
        registerScreenOnReceiver();
        registerUserPresentReceiver();

        makeForegroundNoti();

        return START_NOT_STICKY;
    }
    private void makeForegroundNoti(){
        Notification.Builder noti;

        NotificationChannel nc = new NotificationChannel("channel","name",NotificationManager.IMPORTANCE_LOW);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(nm != null){
            nm.createNotificationChannel(nc);
        }
        noti = new Notification.Builder(this,"channel");

        noti.setSmallIcon(R.mipmap.ic_launcher);
        noti.setContentTitle("효도 위젯서비스");
        noti.setContentText("위젯서비스가 구동중입니다");
        startForeground(1,noti.build());
    }

    public void onDestroy(){
        super.onDestroy();

        unregisterReceiver(screenOffReceiver);
        unregisterReceiver(screenOnReceiver);
        unregisterReceiver(userPresentReceiver);

        stopForeground(true);
    }

    private void registerScreenOffReceiver(){
        Log.v("test","registerScreenOff");
        screenOffReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                AWProvider.turnAlarmOnOff(context,false);
            }
        };
        registerReceiver(screenOffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }
    private void registerScreenOnReceiver(){
        Log.v("test","registerScreenOn");
        screenOnReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                if(!keyguardManager.inKeyguardRestrictedInputMode()){
                    AWProvider.turnAlarmOnOff(context,true);
                }
            }
        };
        registerReceiver(screenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
    }
    private void registerUserPresentReceiver(){
        Log.v("test","registerUserPresent");
        userPresentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                AWProvider.turnAlarmOnOff(context,true);
            }
        };
        registerReceiver(userPresentReceiver,new IntentFilter(Intent.ACTION_USER_PRESENT));
    }
}
