package com.example.widgetproject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetID : appWidgetIds){
            Intent intent = new Intent(context,MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widgetlayout);
            views.setOnClickPendingIntent(R.id.topLeftButton,pendingIntent);
            views.setOnClickPendingIntent(R.id.topMiddleButton,pendingIntent);
            views.setOnClickPendingIntent(R.id.topRightButton,pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetID,views);
        }
    }
}
