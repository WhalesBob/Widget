package com.example.widgetproject;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;

public class BatteryService extends Service {

    public int onStartCommand(Intent intent, int flags, int startID){
        super.onStartCommand(intent,flags,startID);
        IntentFilter filter = new IntentFilter();

        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBRBattery,filter);
        return START_STICKY;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mBRBattery);
    }

    BroadcastReceiver mBRBattery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Intent.ACTION_BATTERY_CHANGED)){
                int current, max, percent;
                current = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,100);
                max = intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);
                percent = current * 100 / max;

                RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widgetlayout);
                views.setTextViewText(R.id.battery,"" + percent + "%");
                AppWidgetManager wm = AppWidgetManager.getInstance(BatteryService.this);
                ComponentName widget = new ComponentName(context, AWProvider.class);
                wm.updateAppWidget(widget,views);
            }
        }
    };
}
