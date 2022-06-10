package com.example.messagecheckmodule;

import static java.sql.Types.NULL;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class messageSendPopup extends Activity {

    TextView txt_to;
    Button btn_send_message;
    EditText et_sendingtxt;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("test", "SendPopup onCreate()");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.message_send_popup);

        txt_to = (TextView)findViewById(R.id.txt_to);

        btn_send_message = (Button)findViewById(R.id.btn_send_message);

        Intent intent = getIntent();
        String messageTo = intent.getStringExtra("btn_from");
        int count = Integer.parseInt(messageTo);
        String messageNum = "Message"+count;
        String phNum = intent.getStringExtra(messageNum);
        Log.v("test", phNum);
        String messageToTxt = setTxtPhNum(phNum);
        txt_to.setText(messageToTxt);//To. XXX-XXX-XXXX

        btn_send_message.setOnClickListener(view -> Send_Message(phNum));//송신 버튼 누르면 전송
    }

    public void mOnClose (View v) {//문자 보내기 창 닫기
        Log.v("test", "mOnClose()");
        finish();
    }

    String setTxtPhNum (String messageTo)//To. XXX-XXX-XXXX로 변환
    {
        Log.v("test", "setTxtPhNum()");
        return "To. " + messageTo;
    }

    void Send_Message (String phNum)//문자 보내기
    {
        Log.v("test", "Send_Message()");
        et_sendingtxt = (EditText)findViewById(R.id.et_sendingtxt);
        String inserted_message = et_sendingtxt.getText().toString();
        Log.v("test", inserted_message);
        if (!(inserted_message.equals(""))) {//보낼 내용이 비어있지 않다면 문자 보내고 전 화면으로
            SmsManager SMSM = SmsManager.getDefault();
            SMSM.sendTextMessage(phNum, null, inserted_message, null, null);
            Toast.makeText(getApplicationContext(), "전송 완료", Toast.LENGTH_LONG).show();
            finish();
        } else {//보낼 내용이 없다면 전송 실패 메세지
            Toast.makeText(getApplicationContext(), "전송 실패", Toast.LENGTH_LONG).show();
        }
    }
}