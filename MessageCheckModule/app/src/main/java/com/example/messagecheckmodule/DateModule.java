package com.example.messagecheckmodule;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class DateModule {
    String DateSimplifier(Date callDate) {//날짜 읽기 쉽게
        Log.v("test", "DateSimplifier()");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd E", Locale.KOREA);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm a", Locale.KOREA);
        String simpleDate = ""+dateFormat1.format(callDate)+"요일  "+dateFormat2.format(callDate);
        return simpleDate;
    }
}
