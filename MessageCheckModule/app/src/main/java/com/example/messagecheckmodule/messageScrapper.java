package com.example.messagecheckmodule;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;

class messageScrapper extends Activity {


    public String getMessageLog(Cursor managedCursor) {//가장 최근의 부재중 전화 정보를 전화번호, 날짜 등을 포함한 String 형태로 받아 오는 함수
        Log.v("test", "getCallLog()");

        StringBuilder sb = new StringBuilder();
        DateModule DM = new DateModule();
        String msSender = managedCursor.getString(1);
        long msDate = managedCursor.getLong(3);
        Date msGotDate = new Date(Long.valueOf(msDate));

        sb.append("송신자: ").append(msSender).append("\n날짜: ").append(DM.DateSimplifier(msGotDate));

        return sb.toString();
    }

    public String getmsNumber(Cursor managedCursor) {//가장 최근의 부재중 전화의 전화번호를 받아와 전화를 걸 수 있는 형식의 String으로 돌려 받는 함수
        Log.v("test", "getmsNumber()");

        StringBuilder sb = new StringBuilder();

        String msPhNum = managedCursor.getString(1);
        sb.append(msPhNum);

        return sb.toString();
    }

    public String getMessageBody(Cursor managedCursor) {//가장 최근의 부재중 전화 정보를 전화번호, 날짜 등을 포함한 String 형태로 받아 오는 함수
        Log.v("test", "getCallLog()");

        StringBuilder sb = new StringBuilder();

        String msBody = managedCursor.getString(4);

        sb.append(msBody);

        return sb.toString();
    }
}