package com.example.widgetproject.brightControlDirectory;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.widgetproject.R;

public class BrightControlActivity extends AppCompatActivity {
    private static final int WRITE_SETTINGS_PERMISSION_REQUEST_CODE = 0x1000;
    private static int nRotationValue = 0;
    private SeekBar mSeekBarBrightness;
    private TextView mTextViewBrightnessValue;
    private int mBrightnessModeBackup;
    private int mBrightnessValueBackup;
    private boolean mHasWriteSettingsPermission;

    @Override
    protected void onResume() {
        super.onResume();

        if (!isFinishing() && permissionCheckAndRequest()) {
            initBrightness();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brightcontrol);
        mTextViewBrightnessValue = findViewById(R.id.tvBrightnessValue);

        mSeekBarBrightness = findViewById(R.id.BrightnessSeekBar);
        mSeekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mTextViewBrightnessValue.setText(String.valueOf(progress));
                changeScreenBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void initBrightness() {
        backupBrightnessSystemSetting();
        initSeekBarBrightness();
        findViewById(R.id.tvBrightness).setVisibility(View.VISIBLE);
        mTextViewBrightnessValue.setVisibility(View.VISIBLE);
    }

    private void backupBrightnessSystemSetting() {
        mBrightnessModeBackup = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        mBrightnessValueBackup = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 128);
    }

    private void initSeekBarBrightness() {
        mSeekBarBrightness.setVisibility(View.VISIBLE);
        mSeekBarBrightness.setProgress(mBrightnessValueBackup);
    }

    private void changeScreenBrightness(int value) {
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = value * 1.0f / 255;
        window.setAttributes(layoutParams);
        changeBrightnessSystemSetting(value);
    }

    private void changeBrightnessSystemSetting(int value) {
        ContentResolver cResolver = getContentResolver();
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, value);
    }

    private boolean permissionCheckAndRequest() {
        boolean permission;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(getApplicationContext());
        } else {
            permission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }

        mHasWriteSettingsPermission = permission;

        if (!permission) {
            Toast.makeText(getApplicationContext(), "시스템 설정(밝기 조절 세팅)을 변경하기 위해서 시스템 변경할 수 있는 권한이 필요합니다." +
                    "\n잠시 후에 시스템 설정 변경 창으로 이동합니다. 권한을 [허용]해주세요.", Toast.LENGTH_LONG).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, WRITE_SETTINGS_PERMISSION_REQUEST_CODE);
                    } else {
                        ActivityCompat.requestPermissions(BrightControlActivity.this, new String[]{Manifest.permission.WRITE_SETTINGS}, WRITE_SETTINGS_PERMISSION_REQUEST_CODE);
                    }
                }
            }, 3500);

            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean permission;
        if (requestCode == WRITE_SETTINGS_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permission = Settings.System.canWrite(this);
            } else {
                permission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
            }

            mHasWriteSettingsPermission = permission;

            if (permission) {
                initBrightness();
            } else {
                Toast.makeText(getApplicationContext(), "시스템 설정(밝기 조절 세팅)을 변경을 위한 권한이 없어서 앱을 종료하였습니다", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == WRITE_SETTINGS_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                recreate();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                        startActivity(intent);
                    }
                }, 3500);
            }
        }
    }
}
