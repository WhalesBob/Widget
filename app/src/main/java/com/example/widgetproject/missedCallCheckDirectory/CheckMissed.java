package com.example.widgetproject.missedCallCheckDirectory;


import android.provider.CallLog;
import android.util.Log;

public class CheckMissed {
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
