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
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.widgetproject.brightControlDirectory.BrightControlActivity;
import com.example.widgetproject.goWifiAndData.GoDataActivity;
import com.example.widgetproject.goWifiAndData.GoWifiActivity;
import com.example.widgetproject.messageCheckDirectory.MessageCheckPopup;
import com.example.widgetproject.missedCallCheckDirectory.CallCheck;
import com.example.widgetproject.soundDirectory.SoundActivity;

import java.util.Calendar;
import java.util.Locale;

public class AWProvider extends AppWidgetProvider {
    private static final String ACTION_BATTERY_UPDATE = "com.example.widgetproject.action.UPDATE";
    private static int batteryLevel = 0;
    private static String time = "";
    private static final int NOTIFICATION_REQUEST_CODE = 0;

    @Override
    public void onEnabled(Context context) { // 위젯 키면 나타나는 애.
        super.onEnabled(context);

        Log.v("test","onEnabled()");
        turnAlarmOnOff(context,true);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context,appWidgetManager,appWidgetIds);
        Log.v("test","onUpdate()");

        for(int id : appWidgetIds){
            Intent[] intents = getIntents(context);
            int currentLevel = calculateBatteryLevel(context);
            String currentTime = getTime();
            if(batteryChanged(currentLevel) || timeChanged(currentTime)){
                batteryLevel = currentLevel;
                time = currentTime;
            }
            updateViews(context,intents,false);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.v("test","onReceive()");
        Intent[] intents = getIntents(context);
        if(intent.getAction().equals(ACTION_BATTERY_UPDATE)){
            int currentLevel = calculateBatteryLevel(context);
            String currentTime = getTime();
            if(batteryChanged(currentLevel) || timeChanged(currentTime)){
                Log.v("test","Battery Changed!");
                batteryLevel = currentLevel;
                time = currentTime;
                updateViews(context,intents,false);
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
        Intent[] intents = getIntents(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(),NOTIFICATION_REQUEST_CODE,intent,PendingIntent.FLAG_IMMUTABLE);

        if(turnOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000,60 * 1000, pendingIntent);
            Log.v("test","Alarm Set");
            int currentLevel = calculateBatteryLevel(context);
            String currentTime = getTime();
            if(batteryChanged(currentLevel) || timeChanged(currentTime)){
                Log.v("test","Something Changed!");
                batteryLevel = currentLevel;
                time = currentTime;
                updateViews(context,intents,true);
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
    private static void updateViews(Context context,Intent[] intents,boolean fromAlarm){

        Log.v("test","updateViews()");

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgetlayout);
        views.setTextViewText(R.id.battery, batteryLevel + "%");
        views.setTextViewText(R.id.SWETClock, time);
        if(!fromAlarm){

            PendingIntent[] pendingIntents = new PendingIntent[6];
            for(int i = 0; i < 6; i++){
                pendingIntents[i] = PendingIntent.getActivity(context,0,intents[i],PendingIntent.FLAG_IMMUTABLE);
            }

            views.setOnClickPendingIntent(R.id.topLeftButton,pendingIntents[0]); // CallCheck
            views.setOnClickPendingIntent(R.id.topMiddleButton,pendingIntents[1]); // SoundActivity
            views.setOnClickPendingIntent(R.id.topRightButton,pendingIntents[2]); // GoWifiActivity

            views.setOnClickPendingIntent(R.id.bottomLeftButton,pendingIntents[3]); // GoDataActivity
            views.setOnClickPendingIntent(R.id.bottomMiddleButton,pendingIntents[4]); // BrightControlActivity
            views.setOnClickPendingIntent(R.id.bottomRightButton,pendingIntents[5]); // MessageCheckPopup

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
    static Intent[] getIntents(Context context){
        Intent[] intentArray = new Intent[6];
        intentArray[0] = new Intent(context, CallCheck.class);
        intentArray[1] = new Intent(context, SoundActivity.class);
        intentArray[2] = new Intent(context, GoWifiActivity.class);
        intentArray[3] = new Intent(context, GoDataActivity.class);
        intentArray[4] = new Intent(context, BrightControlActivity.class);
        intentArray[5] = new Intent(context, MessageCheckPopup.class);

        return intentArray;
    }
}