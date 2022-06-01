package com.example.widgetproject;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;
import android.widget.TextClock;

import java.util.Calendar;
import java.util.TimerTask;

public class GetTime extends TimerTask {
    RemoteViews remoteViews;
    AppWidgetManager appWidgetManager;
    ComponentName thisWidget;

    public GetTime(Context context, AppWidgetManager appWidgetManager){
        this.appWidgetManager = appWidgetManager;
        remoteViews = new RemoteViews(context.getPackageName(),R.layout.widgetlayout);
        thisWidget = new ComponentName(context, TextClock.class);
    }

    @Override
    public void run(){
        remoteViews.setTextViewText(R.id.SWETClock,getTime());
        appWidgetManager.updateAppWidget(thisWidget,remoteViews);
    }
    private String getTime(){
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int sec = now.get(Calendar.SECOND);
        return hour + ":" + minute + ":" + sec;
    }
}
