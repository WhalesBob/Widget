package com.example.widgetproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 나중에 권한받아오는 것 구현 여기다가 다 하기.
        Intent intent = new Intent(MainActivity.this, BatteryService.class);
        startForegroundService(intent);
        Log.v("test","startForegroundService");
    }

}