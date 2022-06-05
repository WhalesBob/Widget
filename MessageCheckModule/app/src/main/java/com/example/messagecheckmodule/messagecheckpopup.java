package com.example.messagecheckmodule;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class messagecheckpopup extends Activity {
    TextView txtView;
    Button btn_msg_to;
    TextView txt_msgbody;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("test", "popup 성공");

        requestWindowFeature(Window.FEATURE_NO_TITLE);//타이틀 바 제거
        setContentView(R.layout.messagecheckpopup);

        Log.v("test", "onCreate()");

        txtView = (TextView) findViewById(R.id.txtView);
        txt_msgbody = (TextView)findViewById(R.id.txt_msgbody);
        btn_msg_to = (Button) findViewById(R.id.btn_msg_to);

        Intent intent = getIntent();
        String recentMissedMessage = intent.getStringExtra("recentMissedMessage");//읽지 않은 메세지의 정보를 수신
        txtView.setText(recentMissedMessage);
        String recentMessageBody = intent.getStringExtra("missedMsBody");//읽지 않은 문자의 내용을 가져옴
        txt_msgbody.setText(recentMessageBody);

    }

    public void mOnPopupClick (View v) {// 누르면 팝업 하도록 하는 함수. messagecheckpopup.xml의 버튼에서 직접 선언

        Log.v("test", "mOnPopupClick()");
        Intent popups = new Intent(this, messageSendPopup.class);
        Intent intent = getIntent();
        String missedMSNum = intent.getStringExtra("missedMsNum");//읽지 않은 메세지의 전화번호를 수신

        if (!missedMSNum.equals("default")) {
            popups.putExtra("missedMessageNum", missedMSNum);

            Log.v("test", missedMSNum);

            startActivity(popups);//팝업 생성
        }
    }

    //확인 버튼. 팝업 제거.
    public void mOnClose(View v) {
        Log.v("test", "mOnClose()");
        finish();
    }
}
