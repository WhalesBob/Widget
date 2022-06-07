package com.example.widgetproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.widgetproject.missedCallCheckDirectory.CallCheck;

import java.util.Calendar;
import java.util.Locale;

public class AWProvider extends AppWidgetProvider {
    private static final String ACTION_BATTERY_UPDATE = "com.example.widgetproject.action.UPDATE";
    private static int batteryLevel = 0;
    private static String time = "";

    @Override
    public void onEnabled(Context context) { // 위젯 키면 나타나는 애.
        super.onEnabled(context);

        Log.v("test","onEnabled()");
        turnAlarmOnOff(context,true);

        context.startService(new Intent(context, BatteryService.class));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        Log.v("test","onUpdate()");

        for(int id : appWidgetIds){
            Intent missedCallCheckIntent = new Intent(context, CallCheck.class);
            Intent intent = new Intent(context,MainActivity.class); // 여기서 MainActivity.class 를 받아서 Intent 를 생성함.
            int currentLevel = calculateBatteryLevel(context);
            String currentTime = getTime();
            if(batteryChanged(currentLevel) || timeChanged(currentTime)){
                batteryLevel = currentLevel;
                time = currentTime;
            }
            updateViews(context,intent,missedCallCheckIntent,false);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.v("test","onReceive()");
        Intent missedCallCheckIntent = new Intent(context, CallCheck.class);
        if(intent.getAction().equals(ACTION_BATTERY_UPDATE)){
            int currentLevel = calculateBatteryLevel(context);
            String currentTime = getTime();
            if(batteryChanged(currentLevel) || timeChanged(currentTime)){
                Log.v("test","Battery Changed!");
                batteryLevel = currentLevel;
                time = currentTime;
                updateViews(context,intent,missedCallCheckIntent,false);
            }
        }
    }

    @Override
    public void onDisabled(Context context) { // disabled 는 그냥 위젯 없애면 이렇게 되나 보더라.
        super.onDisabled(context);

        Log.v("test","onDisabled()");
        turnAlarmOnOff(context,false);
        context.stopService(new Intent(context,BatteryService.class));
    }

    public static void turnAlarmOnOff(Context context, boolean turnOn){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Intent.ACTION_BATTERY_CHANGED);
        Intent missedCallCheckIntent = new Intent(context, CallCheck.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//        if(Build.VERSION.SDK_INT >= 32){
//            pendingIntent = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_MUTABLE);
//        }else{
//            pendingIntent = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
//        }

        if(turnOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000,60 * 1000, pendingIntent);
            Log.v("test","Alarm Set");
            int currentLevel = calculateBatteryLevel(context);
            String currentTime = getTime();
            if(batteryChanged(currentLevel) || timeChanged(currentTime)){
                Log.v("test","Something Changed!");
                batteryLevel = currentLevel;
                time = currentTime;
                updateViews(context,intent,missedCallCheckIntent,true);
            }else{
                Log.v("test","Something is not Changed");
            }
        }else{
            alarmManager.cancel(pendingIntent);
            Log.v("test","Alarm Disabled");

        }
    }
    private static int calculateBatteryLevel(Context context){
        Log.v("test","CalculateBatteryLevel()");
        Intent batteryIntent = context.getApplicationContext().registerReceiver(null,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE,100);
        return (level * 100) / scale;
    }
    private static boolean batteryChanged(int currentLevelLeft){
        Log.v("test","Check Battery Change");
        return (batteryLevel != currentLevelLeft);
    }
    private static void updateViews(Context context,Intent intent,Intent missedCallCheckIntent, boolean fromAlarm){

        Log.v("test","updateViews()");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgetlayout);
        views.setTextViewText(R.id.battery, batteryLevel + "%");
        views.setTextViewText(R.id.SWETClock, time);
        if(!fromAlarm){
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_IMMUTABLE);
            PendingIntent missedCallPendingIntent = PendingIntent.getActivity(context,0,missedCallCheckIntent, PendingIntent.FLAG_IMMUTABLE);

            views.setOnClickPendingIntent(R.id.topLeftButton,missedCallPendingIntent);
            views.setOnClickPendingIntent(R.id.topMiddleButton,pendingIntent);
            views.setOnClickPendingIntent(R.id.topRightButton,pendingIntent);
        }

        ComponentName componentName = new ComponentName(context,AWProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(componentName,views);
    }
    private static String getTime(){ // 시간을 받아오는 함수.
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        String minFormat = (minute < 10) ? String.format(Locale.KOREA,"0%d",minute) : Integer.toString(minute);
        return hour + ":" + minFormat;
    }
    private static boolean timeChanged(String currentTimeLeft){
        Log.v("test","Check Time Change");
        return (!time.equals(currentTimeLeft));
    }
}