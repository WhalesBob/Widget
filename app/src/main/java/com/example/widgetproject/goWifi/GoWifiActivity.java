package com.example.widgetproject.goWifi;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Global;

import androidx.appcompat.app.AppCompatActivity;


import com.example.widgetproject.R;

public class GoWifiActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gowifi);
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }
}
