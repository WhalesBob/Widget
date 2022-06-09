package com.example.widgetproject.missedCallCheckDirectory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Button;
import java.util.Date;

import static android.Manifest.permission.READ_CALL_LOG;

public class MissedCallActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // 화면 만들어 주었던 메소드이다. 얘만 고치면 되나...?
        Log.v("test", "onCreate");

        if(!checkPermission())//권한 유무 확인 및 없을 시 권한 요청 // 필요하다. 이건 CallCheck 로 넘겨줘서 처리해야 한다. 나머지는 필요없는부분이다.
        {
            Log.v("test", "no permission");
            requestPermission();
        }
        else
            Log.v("test","yes permission");
    }

    public void mOnPopupClick () {// 누르면 팝업 하도록 하는 함수. view_layout.xml의 버튼에서 직접 선언

        Log.v("test","mOnPopupClick()");

        Intent popups = new Intent(MissedCallActivity.this, CallCheck.class);

        popups.putExtra("recentMissedCall", getCallLog());//가장 최근의 부재중 전화 정보를 전화번호, 날짜 등을 포함한 String 형태로 받아 옴
        popups.putExtra("missedCallPhNum", getphNumber());//가장 최근의 부재중 전화의 전화번호를 받아와 전화를 걸 수 있는 형식의 String으로 돌려 받음

        Log.v("test", "popup 준비");

        startActivity(popups);//팝업 생성. 여기서 넘어가나보다.
    }

    private boolean checkPermission() {
        Log.v("test","checkPermission()");
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CALL_LOG);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        Log.v("test","requestPermission");
        ActivityCompat.requestPermissions(this, new String[]{READ_CALL_LOG}, PERMISSION_REQUEST_CODE); // Call Log을 받아오는 애인가보다.
    }

    private String getCallLog() {//가장 최근의 부재중 전화 정보를 전화번호, 날짜 등을 포함한 String 형태로 받아 오는 함수. 필요해 보임.
        // 따로 클래스를 파서 객체로써 들고 와야 하나?

        Log.v("test", "getCallLog()");

        StringBuilder sb = new StringBuilder();
        CheckMissed CM = new CheckMissed();
        DateModule DM = new DateModule();

        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        String dir = "default";
        while (managedCursor.moveToNext()) { //통화 기록이 끝날때 까지. 다만 밑의 break로 인해 제한이 존재.
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callDayTime = new Date(Long.parseLong(callDate));

            dir = "default";

            if(!DM.compareDay(callDayTime))//만약 10일 이내의 통화 기록에서 부재중 전화가 없을 경우
            {
                Log.v("test","no missed calls in 10 days");
                break;
            }

            int dircode = Integer.parseInt(callType);
            dir = CM.CheckMissedCall(dircode);

            if(dir.equals("MISSED"))
            {
                Log.v("test", "missed call detected");
                sb.append("\n부재중 전화:\n전화번호: ").append(phNumber).append("\n날짜: ").append(DM.DateSimplifier(callDayTime));
                sb.append("\n");

                break;
            }
        }

        if(!dir.equals("MISSED"))
        {
            sb.append("최근 10일 내에 부재중 전화가 없습니다.");
        }
        Log.v("test", sb.toString());
        managedCursor.close();
        return sb.toString();
    }

    private String getphNumber() {//가장 최근의 부재중 전화의 전화번호를 받아와 전화를 걸 수 있는 형식의 String으로 돌려 받는 함수
        Log.v("test", "getphNumber()");

        StringBuilder sb = new StringBuilder();
        CheckMissed CM = new CheckMissed();
        DateModule DM = new DateModule();

        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        String dir = "default";
        int count = 0;

        while(managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            Date callTodayDate = new Date(Long.valueOf(callDate));
            dir = "default";
            count++;

            Log.v("test", "counted: " + count);


            if(!DM.compareDay(callTodayDate))//최근 10일 내에 부재중 전화가 없을 시
            {
                Log.v("test","no missed calls for last 10 days");
                break;
            }

            int dircode = Integer.parseInt(callType);
            dir = CM.CheckMissedCall(dircode);

            if (dir.equals("MISSED"))
            {
                Log.v("test", "got phn Num");
                Log.v("test", phNumber);
                sb.append("tel:").append(phNumber);
                break;
            }
        }
        if(!dir.equals("MISSED"))
        {
            Log.v("test", "no records");
            return sb.append("default").toString();
        }
        managedCursor.close();
        return sb.toString();
    }
}


