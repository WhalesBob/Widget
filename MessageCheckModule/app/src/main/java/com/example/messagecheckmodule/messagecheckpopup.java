package com.example.messagecheckmodule;

import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.SEND_SMS;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class messagecheckpopup extends Activity {
    Button sendTextBtn1;
    Button sendTextBtn2;
    Button sendTextBtn3;
    Button sendTextBtn4;
    Button sendTextBtn5;
    TextView message;
    TextView msgbody;

    int PERMISSION_REQUEST_CODE = 201;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        Log.v("test", "onCreate()");
        if(!checkPermission()) {
            Log.v("test", "no per");
            requestPermission();
        }
        else
        {
            Log.v("test", "yes per");
        }
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//타이틀 바 제거


        setContentView(R.layout.messagecheckpopup);

        Intent sending = backgroundMain();

        sendTextBtn1 = (Button)findViewById(R.id.btn_msg_to_1);
        sendTextBtn2 = (Button)findViewById(R.id.btn_msg_to_2);
        sendTextBtn3 = (Button)findViewById(R.id.btn_msg_to_3);
        sendTextBtn4 = (Button)findViewById(R.id.btn_msg_to_4);
        sendTextBtn5 = (Button)findViewById(R.id.btn_msg_to_5);

        sendTextBtn1.setOnClickListener(view -> mOnPopupClick(1,sending));
        sendTextBtn2.setOnClickListener(view -> mOnPopupClick(2,sending));
        sendTextBtn3.setOnClickListener(view -> mOnPopupClick(3,sending));
        sendTextBtn4.setOnClickListener(view -> mOnPopupClick(4,sending));
        sendTextBtn5.setOnClickListener(view -> mOnPopupClick(5,sending));

    }

    private boolean checkPermission() {
        Log.v("test","checkPermission()");
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_SMS);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        Log.v("test","requestPermission()");
        ActivityCompat.requestPermissions(this, new String[]{READ_SMS, SEND_SMS}, PERMISSION_REQUEST_CODE);
    }

    private Intent backgroundMain() {//실질적인 작업 진행
        Log.v("test", "backgroundMain()");

        Cursor managingCursor = getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, new String[] {" _id", "address", "person" ,"date", "body"}, null, null, "date DESC");//SMS만 수신 가능
        //MMS나 RCS는 확인 불가능
        Intent sendMessage = new Intent(this, messageSendPopup.class);
        int count=1;
        while(managingCursor.moveToNext()) {
            String instantPhNum = defineAndDistribute(count, managingCursor);
            String currentMessage = "Message"+count;
            sendMessage.putExtra(currentMessage, instantPhNum);
            if (count==5)
                break;
            count = count+1;
        }

        managingCursor.close();
        return sendMessage;
    }

    public void mOnPopupClick(int count, Intent sending) {// 누르면 팝업 하도록 하는 함수. messagecheckpopup.xml의 버튼에서 직접 선언

        Log.v("test", "mOnPopupClick()");
        String buttonNo = Integer.toString(count);
        String buttonName = "btn_from";
        sending.putExtra(buttonName, buttonNo);


        startActivity(sending);//팝업 생성

    }

    //확인 버튼. 팝업 제거.
    public void mOnClosed(View v) {
        Log.v("test", "mOnClose()");
        moveTaskToBack(true);
        finishAndRemoveTask();
    }

    String defineAndDistribute(int count, Cursor cursor) {

        Log.v("test", "defineAndDistribute()");
        Log.v("test", Integer.toString(count));

        messageScrapper mS = new messageScrapper();

        switch (count)//위에서부터 차례대로 채워넣을 때 각 칸의 위치에 따라 분류하는 기능
        {
            case 1:
                Log.v("test", "case 1");
                //message = (TextView)findViewById(R.id.message_1);
                message = (TextView)findViewById(R.id.message_1);
                Log.v("test", "part 1 txt");
                String MessageInfo1 = mS.getMessageLog(cursor);
                message.setText(MessageInfo1);
                String MessagePhNum1 = mS.getmsNumber(cursor);
                Log.v("test", MessagePhNum1);

                msgbody=(TextView)findViewById(R.id.msgbody_1);
                String MessageBody1 = mS.getMessageBody(cursor);
                msgbody.setText(MessageBody1);

                return MessagePhNum1;
            case 2:
                message = (TextView)findViewById(R.id.message_2);
                String MessageInfo2 = mS.getMessageLog(cursor);
                message.setText(MessageInfo2);
                String MessagePhNum2 = mS.getmsNumber(cursor);

                msgbody=(TextView)findViewById(R.id.msgbody_2);
                String MessageBody2 = mS.getMessageBody(cursor);
                msgbody.setText(MessageBody2);

                return MessagePhNum2;
            case 3:
                message = (TextView)findViewById(R.id.message_3);
                String MessageInfo3 = mS.getMessageLog(cursor);
                message.setText(MessageInfo3);
                String MessagePhNum3 = mS.getmsNumber(cursor);

                msgbody=(TextView)findViewById(R.id.msgbody_3);
                String MessageBody3 = mS.getMessageBody(cursor);
                msgbody.setText(MessageBody3);

                return MessagePhNum3;
            case 4:
                message = (TextView)findViewById(R.id.message_4);
                String MessageInfo4 = mS.getMessageLog(cursor);
                message.setText(MessageInfo4);
                String MessagePhNum4 = mS.getmsNumber(cursor);

                msgbody=(TextView)findViewById(R.id.msgbody_4);
                String MessageBody4 = mS.getMessageBody(cursor);
                msgbody.setText(MessageBody4);

                return MessagePhNum4;
            case 5:
                message = (TextView)findViewById(R.id.message_5);
                String MessageInfo5 = mS.getMessageLog(cursor);
                message.setText(MessageInfo5);
                String MessagePhNum5 = mS.getmsNumber(cursor);

                msgbody=(TextView)findViewById(R.id.msgbody_5);
                String MessageBody5 = mS.getMessageBody(cursor);
                msgbody.setText(MessageBody5);

                return MessagePhNum5;
            default:
                return "error";
        }
    }
}