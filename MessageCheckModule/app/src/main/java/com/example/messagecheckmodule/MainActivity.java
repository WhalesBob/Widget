package com.example.messagecheckmodule;

import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.SEND_SMS;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    int PERMISSION_REQUEST_CODE = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkPermission())
        {
            requestPermission();
        }
    }

    public void mOnPopupClick (View v) {// 누르면 팝업 하도록 하는 함수. view_layout.xml의 버튼에서 직접 선언

        Log.v("test","mOnPopupClick()");
        ContentResolver cr = getContentResolver();
        Intent popups = new Intent(this, messagecheckpopup.class);
        getCursors gC = new getCursors();

        Log.v("test", "Cursors ready");
        popups.putExtra("recentMissedMessage", getMessageLog(gC.getCursor(cr)));//가장 최근의 부재중 전화 정보를 전화번호, 날짜 등을 포함한 String 형태로 받아 옴
        Log.v("test", "got log");
        popups.putExtra("missedMsNum", getmsNumber(gC.getCursor(cr)));//가장 최근의 부재중 전화의 전화번호를 받아와 전화를 걸 수 있는 형식의 String으로 돌려 받음
        Log.v("test", "got num");
        popups.putExtra("missedMsBody", getMessageBody(gC.getCursor(cr)));
        Log.v("test", "got body");
        startActivity(popups);//팝업 생성
    }

    private boolean checkPermission() {
        Log.v("test","checkPermission()");
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_SMS);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        Log.v("test","requestPermission");
        ActivityCompat.requestPermissions(this, new String[]{READ_SMS, SEND_SMS}, PERMISSION_REQUEST_CODE);
    }

    private String getMessageLog(Cursor managedCursor) {//가장 최근의 부재중 전화 정보를 전화번호, 날짜 등을 포함한 String 형태로 받아 오는 함수
        Log.v("test", "getCallLog()");

        StringBuffer sb = new StringBuffer();
        CheckMissed CM = new CheckMissed();
        DateModule DM = new DateModule();

        String dir = "default";
        while (managedCursor.moveToNext()) { //통화 기록이 끝날때 까지. 다만 밑의 break로 인해 제한이 존재.
            String msNumber = managedCursor.getString(2);
            String msType = Integer.toString(managedCursor.getInt(6));
            String msDate = Integer.toString((int)managedCursor.getLong(4));

            Log.v("test", msType);

            Date msDayTime =new Date(Long.parseLong(msDate));
            dir = "default";

            if(DM.compareDay(msDate))//만약 10일 이내의 통화 기록에서
            {
                Log.v("test","no missed calls in 10 days");
                break;
            }

            int dircode = Integer.parseInt(msType);
            dir = CM.CheckMissedMessage(dircode);

            if(dir.equals("Unread"))
            {
                Log.v("test", "missed call detected");
                sb.append("\n읽지 않은 메세지:\n전화번호: ").append(msNumber).append("\n날짜: ").append(msDayTime);
                sb.append("\n");
                break;
            }
        }

        if(!dir.equals("Unread"))
        {
            sb.append("최근 10일 내에 부재중 전화가 없습니다.");
        }

        managedCursor.close();
        return sb.toString();
    }

    private String getmsNumber(Cursor managedCursor) {//가장 최근의 부재중 전화의 전화번호를 받아와 전화를 걸 수 있는 형식의 String으로 돌려 받는 함수
        Log.v("test", "getmsNumber()");

        StringBuffer sb = new StringBuffer();
        CheckMissed CM = new CheckMissed();
        DateModule DM = new DateModule();

        String dir = "default";

        while(managedCursor.moveToNext()) {
            String msNumber = managedCursor.getString(2);
            String msType = Integer.toString(managedCursor.getInt(6));
            String msDate = Integer.toString((int)managedCursor.getLong(4));
            dir = "default";

            if(DM.compareDay(msDate))//최근 10일 내에 읽지 않은 문자가 없을 시
            {
                Log.v("test","no missed calls for last 10 days");
                break;
            }

            int dircode = Integer.parseInt(msType);
            dir = CM.CheckMissedMessage(dircode);

            if (dir.equals("Unread"))
            {
                Log.v("test", "got msg Num");
                Log.v("test", msNumber);
                sb.append(msNumber);
                break;
            }
        }
        if(!dir.equals("Unread"))
        {
            return null;
        }
        managedCursor.close();
        return sb.toString();
    }

    private String getMessageBody(Cursor managedCursor) {//가장 최근의 부재중 전화 정보를 전화번호, 날짜 등을 포함한 String 형태로 받아 오는 함수
        Log.v("test", "getCallLog()");

        StringBuffer sb = new StringBuffer();
        CheckMissed CM = new CheckMissed();
        DateModule DM = new DateModule();

        String dir = "default";
        while (managedCursor.moveToNext()) { //통화 기록이 끝날때 까지. 다만 밑의 break로 인해 제한이 존재.
            String msType = Integer.toString(managedCursor.getInt(6));
            String msDate = Integer.toString((int)managedCursor.getLong(4));
            String msBody = managedCursor.getString(5);
            dir = "default";

            if(DM.compareDay(msDate))//만약 10일 이내의 통화 기록에서
            {
                Log.v("test","no missed messages in 10 days");
                break;
            }

            int dircode = Integer.parseInt(msType);
            dir = CM.CheckMissedMessage(dircode);

            if(dir.equals("Unread"))
            {
                Log.v("test", "missed call detected");
                sb.append(msBody);
                break;
            }
        }

        if(!dir.equals("Unread"))
        {
            sb.append("최근 10일 내에 읽지 않은 메세지가 없습니다.");
        }

        managedCursor.close();
        return sb.toString();
    }
}

class CheckMissed {
    String CheckMissedMessage(int dircode)//제공받은 자료가 부재중 전화인지 아닌지 확인하는 함수
    {
        Log.v("test", "CheckMissedMessage()");
        String dir;
        if (dircode == SmsManager.STATUS_ON_ICC_UNREAD) {
            dir = "Unread";
            return dir;
        }
        return "default";
    }
}

class DateModule {
    String getTodayDate (){//오늘의 날짜 자료를 생성해내는 함수
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        String simpleDate = dateFormat.format(today);
        return simpleDate;
    }

    String getTodayDate (Date msDate){//입력 받은 자료의 날짜 자료를 생성해내는 함수. Overloading
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd",Locale.KOREA);
        String simpleDate = dateFormat.format(msDate);
        return simpleDate;
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

    boolean compareDay (Date msDate)//최근 10일 내의 통화기록인지 확인하여 10일 내라면 true값을, 아니라면 false 값을 반환한다.
    {
        Log.v("test", "compareDay()");

        int standard_date_month = getMonth(getTodayDate());
        int standard_date_day = changeMinusDate(getDay(getTodayDate()) - 10, standard_date_month);
        int target_date_day = getDay(getTodayDate(msDate));
        int target_date_month = getMonth(getTodayDate(msDate));

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

class getCursors {
    Cursor getCursor(ContentResolver cr) {
        Log.v("test", "getCursor()");

        Uri allMessage = Uri.parse("content://sms");


        Cursor managedCursor = cr.query(allMessage, new String[] {" _id", "address", "date", "body"}, null, null, "date DESC");

        return managedCursor;
    }
}
