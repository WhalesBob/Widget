package com.example.widgetproject.missedCallCheckDirectory;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateModule {
    String getTodayDate (){//오늘의 날짜 자료를 생성해내는 함수
        Log.v("test", "getTodayDate-void");
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        String gotTodayDate = ""+dateFormat.format(today);
        Log.v("test", gotTodayDate);
        return gotTodayDate;
    }

    String getTodayDate (Date callDate){//입력 받은 자료의 날짜 자료를 생성해내는 함수. Overloading
        Log.v("test", "getTodayDate-String");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        String gotTodayDate = ""+dateFormat.format(callDate);
        Log.v("test", gotTodayDate);
        return gotTodayDate;
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

        Log.v("test", Integer.toString(day));
        return day;
    }

    boolean compareDay (Date callDate)//최근 10일 내의 통화기록인지 확인하여 10일 내라면 true값을, 아니라면 false 값을 반환한다.
    {
        Log.v("test", "compareDay()");

        int standard_date_month = getMonth(getTodayDate());
        int standard_date_day = changeMinusDate(getDay(getTodayDate()) - 10, standard_date_month);
        int target_date_day = getDay(getTodayDate(callDate));
        int target_date_month = getMonth(getTodayDate(callDate));

        if(getDay(getTodayDate())<=10)
            standard_date_month--;

        Log.v("test", "target month: "+target_date_month);
        Log.v("test", "target day: "+target_date_day);
        Log.v("test", "standard date: "+standard_date_month+"/"+standard_date_day);

        if(standard_date_month<target_date_month) {
            Log.v("test", "no error here");
            return true;
        }
        else if(standard_date_month == target_date_month)
        {
            Log.v("test", "there is no error here");
            return standard_date_day <= target_date_day;
        }
        else
            return false;
    }

    String DateSimplifier(Date callDate) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd E", Locale.KOREA);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm a", Locale.KOREA);
        String simpleDate = ""+dateFormat1.format(callDate)+"요일  "+dateFormat2.format(callDate);
        return simpleDate;
    }
}