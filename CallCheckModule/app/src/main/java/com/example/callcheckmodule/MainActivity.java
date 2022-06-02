package com.example.callcheckmodule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.READ_CALL_LOG;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;

    Button btn_call_widget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_layout);//main_activity 없이 view_layout으로

        Log.v("test", "onCreate");

        btn_call_widget = (Button)findViewById(R.id.btn_call_widget);

        if(!checkPermission())//권한 유무 확인 및 없을 시 권한 요청
        {
            Log.v("test", "no permission");
            requestPermission();
        }
        else
            Log.v("test","yes permission");
    }

    public void mOnPopupClick (View v) {// 누르면 팝업 하도록 하는 함수. view_layout.xml의 버튼에서 직접 선언

        Log.v("test","mOnPopupClick()");

        Intent popups = new Intent(this, callcheckpopup.class);

        popups.putExtra("recentMissedCall", getCallLog());//가장 최근의 부재중 전화 정보를 전화번호, 날짜 등을 포함한 String 형태로 받아 옴
        popups.putExtra("missedCallPhNum", getphNumber());//가장 최근의 부재중 전화의 전화번호를 받아와 전화를 걸 수 있는 형식의 String으로 돌려 받음
        startActivityForResult(popups, 1);//팝업 생성
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

        StringBuffer sb = new StringBuffer();
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

            if(DM.compareDay(callDate))//만약 10일 이내의 통화 기록에서
            {
                Log.v("test","no missed calls in 10 days");
                break;
            }

            int dircode = Integer.parseInt(callType);
            dir = CM.CheckMissedCall(dircode);

            if(dir.equals("MISSED"))
            {
                Log.v("test", "missed call detected");
                sb.append("\n부재중 전화:\n전화번호: ").append(phNumber).append("\n날짜: ").append(callDayTime);
                sb.append("\n");
                break;
            }
        }

        if(!dir.equals("MISSED"))
        {
            sb.append("최근 10일 내에 부재중 전화가 없습니다.");
        }

        managedCursor.close();
        return sb.toString();
    }

    private String getphNumber() {//가장 최근의 부재중 전화의 전화번호를 받아와 전화를 걸 수 있는 형식의 String으로 돌려 받는 함수
        Log.v("test", "getphNumber()");

        StringBuffer sb = new StringBuffer();
        CheckMissed CM = new CheckMissed();
        DateModule DM = new DateModule();

        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        String dir = "default";

        while(managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDate = managedCursor.getString(date);
            dir = "default";

            if(DM.compareDay(callDate))//최근 10일 내에 부재중 전화가 없을 시
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
            return null;
        }
        managedCursor.close();
        return sb.toString();
    }
}

class CheckMissed {
    String CheckMissedCall(int dircode)//제공받은 자료가 부재중 전화인지 아닌지 확인하는 함수
    {
        Log.v("test", "CheckMissedCall()");
        String dir;
        if (dircode == CallLog.Calls.MISSED_TYPE) {
            dir = "MISSED";
            return dir;
        }
        return "default";
    }
}

class DateModule {
    String getTodayDate (){//오늘의 날짜 자료를 생성해내는 함수
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(today);
    }

    String getTodayDate (String callDate){//입력 받은 자료의 날짜 자료를 생성해내는 함수. Overloading
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(callDate);
    }

    int getDay (String Date){//입력된 자료에서 일을 분리해내는 함수
        String strDay = Date.substring(Date.length()-2);
        return Integer.parseInt(strDay);
    }

    int getMonth (String Date) {//입력된 자료에서 월을 분리해내는 함수
        String strMonth = Date.substring(Date.length()-5, Date.length()-3);
        return Integer.parseInt(strMonth);
    }

    int changeMinusDate (int day, int month) {//계산 과정에서 날짜가 0 이하가 될 경우 그 전 달로 넘기는 함수. 다만 윤년은 계산하지 않는다.
        Log.v("test", "changeMinusDate()");

        if(day<=0)
        {
            month--;
            if(month==0)
                month = 12;
            if(month==1||month==3||month==5||month==7||month==8||month==10||month==12)
                day = 31 + day;
            else if(month==2)
                day = 28 + day;
            else
                day = 30 + day;
        }

        return day;
    }

    boolean compareDay (String callDate)//최근 10일 내의 통화기록인지 확인하여 10일 내라면 true값을, 아니라면 false 값을 반환한다.
    {
        Log.v("test", "compareDay()");

        int standard_date_month = getMonth(getTodayDate());
       int standard_date_day = changeMinusDate(getDay(getTodayDate()) - 10, standard_date_month);
       int target_date_day = getDay(getTodayDate(callDate));
       int target_date_month = getMonth(getTodayDate(callDate));

       if(getDay(getTodayDate())<=10)
           standard_date_month--;

       if(standard_date_month<target_date_month)
           return true;
       else if(standard_date_month == target_date_month)
       {
           return standard_date_day <= target_date_day;
       }
       else
           return false;
    }
}