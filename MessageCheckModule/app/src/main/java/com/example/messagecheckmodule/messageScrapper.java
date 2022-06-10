package com.example.messagecheckmodule;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Date;

class messageScrapper extends Activity {


    public String getMessageLog(Cursor managedCursor) {//문자의 송신인, 날짜를 받아오는 함수
        Log.v("test", "getCallLog()");

        StringBuilder sb = new StringBuilder();
        DateModule DM = new DateModule();
        String msSender = managedCursor.getString(1);
        long msDate = managedCursor.getLong(3);
        Date msGotDate = new Date(Long.valueOf(msDate));

        sb.append("송신자: ").append(msSender).append("\n날짜: ").append(DM.DateSimplifier(msGotDate));

        return sb.toString();
    }

    public String getmsNumber(Cursor managedCursor) {//문자를 보낸 사람의 전화번호를 받아 오는 함수
        Log.v("test", "getmsNumber()");

        StringBuilder sb = new StringBuilder();

        String msPhNum = managedCursor.getString(1);
        sb.append(msPhNum);

        return sb.toString();
    }

    public String getMessageBody(Cursor managedCursor) {//문자 내용을 받아오는 함수
        Log.v("test", "getCallLog()");

        StringBuilder sb = new StringBuilder();

        String msBody = managedCursor.getString(4);

        sb.append(msBody);

        return sb.toString();
    }
}