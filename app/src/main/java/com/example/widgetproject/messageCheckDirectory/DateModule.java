package com.example.widgetproject.messageCheckDirectory;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateModule {
    String dateSimplifier(Date callDate){
        Log.v("test", "DateSimplifier()");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd E", Locale.KOREA);
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("hh:mm a", Locale.KOREA);
        String simpleDate = ""+dateFormat1.format(callDate)+"요일  "+dateFormat2.format(callDate);
        return simpleDate;
    }
}
