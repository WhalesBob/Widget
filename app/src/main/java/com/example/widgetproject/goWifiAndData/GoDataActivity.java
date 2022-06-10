package com.example.widgetproject.goWifiAndData;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;


import com.example.widgetproject.R;

public class GoDataActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gowifi);
        startActivity(new Intent(Settings.ACTION_DATA_USAGE_SETTINGS));
    }
}