package com.example.widgetproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, BatteryService.class);
        startForegroundService(intent);
        Log.v("test","startForegroundService");

        int[] permissions = new int[4];

        // 권한받아오는부분 : 시스템 변경 권한, 전화, 문자 이렇게 3개
        permissions[0] = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG);
        permissions[1] = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        permissions[2] = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        boolean isDenied = false;
        for(int i = 0; i < 3; i++){
            if(permissions[i] == PackageManager.PERMISSION_DENIED){
                isDenied = true;
                break;
            }
        }

        if(isDenied){
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS}, 1000);
        }

        getSystemPermission();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1000){
            boolean checkResult = true;
            for(int result : grantResults){
                if(result != PackageManager.PERMISSION_GRANTED){
                    checkResult = false;
                    break;
                }
            }
            if(!checkResult){
                finish();
            }
        }
    }

    public void getSystemPermission(){
        if(Settings.System.canWrite(this)){
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("추가 권한요청")
                    .setMessage("시스템 설정 변경 권한이 필요합니다.\n권한 허용창으로 이동합니다.\n권한을 허용해 주세요")
                    .setPositiveButton("계속", (dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.setData(Uri.parse("package:${packageName}"));
                        startActivity(intent);
                        dialogInterface.dismiss();
                    }).setNegativeButton("취소", (dialogInterface, i) -> dialogInterface.cancel()).create();

            alertDialog.show();
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

        if(resultCode == 0){
            if(Settings.System.canWrite(this)){
                Toast.makeText(this,"권한을 얻었습니다.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"권한을 얻지 못했습니다.",Toast.LENGTH_SHORT).show();
            }
        }
    }
}