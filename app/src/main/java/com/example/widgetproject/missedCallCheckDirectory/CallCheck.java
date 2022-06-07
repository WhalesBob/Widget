package com.example.widgetproject.missedCallCheckDirectory;
import static android.Manifest.permission.READ_CALL_LOG;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.widgetproject.R;

import java.util.Date;

public class CallCheck extends Activity {

    private static final int PERMISSION_REQUEST_CODE = 200;

    TextView txtView;
    Button btn_call_to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//타이틀 바 제거
        setContentView(R.layout.callcheckpopup);

        Log.v("test", "onCreate callCheck");

        txtView = (TextView) findViewById(R.id.txtView);
        btn_call_to = (Button) findViewById(R.id.btn_call_to);

        if(!checkPermission())
        {
            requestPermission();
        }


        String recentMissedCall = getCallLog();
        txtView.setText(recentMissedCall);
        String missedPhNum = getphNumber();//부재중 전화의 전화번호를 수신

        Log.v("test", missedPhNum);
        //전화 버튼
        btn_call_to.setOnClickListener(view -> phoneCall(missedPhNum));

    }

    private boolean checkPermission() {
        Log.v("test","checkPermission()");
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CALL_LOG);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        Log.v("test","requestPermission");
        ActivityCompat.requestPermissions(this, new String[]{READ_CALL_LOG}, PERMISSION_REQUEST_CODE);
    }

    private String getCallLog() {//가장 최근의 부재중 전화 정보를 전화번호, 날짜 등을 포함한 String 형태로 받아 오는 함수
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

    //확인 버튼. 팝업 제거.
    public void mOnClose(View v) {
        Log.v("test", "mOnClose()");
        moveTaskToBack(true);
        finishAndRemoveTask();
    }

    void phoneCall(String phNum)//전해받은 전화번호로 전화를 거는 함수
    {
        Log.v("test", "phoneCall()");

        checkNull CN = new checkNull();
        if (CN.checkDefault(phNum)) {//만약 최근 10일 내에 부재중 전화가 없을 시 전화를 걸 수 없도록 조치
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(phNum));
            startActivity(intent);
        } else {
            Log.v("test", "no phn Num");
            txtView.setText("부재중 전화가 없는 상황에서 전화를 걸 수 없습니다.");
        }

    }
}

class checkNull {
    boolean checkDefault (String phNum) {//전화를 걸기 위한 String 자료의 내용이 null인지 아닌지 판단하는 함수
        return !phNum.equals("default");
    }
}
