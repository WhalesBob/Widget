package com.example.messagecheckmodule;

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
        txt_to.setText(messageToTxt);



        btn_send_message.setOnClickListener(view -> Send_Message(phNum));
    }

    public void mOnClose (View v) {
        finish();
    }

    String setTxtPhNum (String messageTo)
    {
        return "To. " + messageTo;
    }

    void Send_Message (String phNum)
    {
        et_sendingtxt = (EditText)findViewById(R.id.et_sendingtxt);
        String inserted_message = et_sendingtxt.getText().toString();
        Log.v("test", inserted_message);
        if (!inserted_message.equals("default"))
        {
            SmsManager SMSM = SmsManager.getDefault();
            SMSM.sendTextMessage(phNum, null, inserted_message, null, null);
            Toast.makeText(getApplicationContext(), "전송 완료", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "전송 실패", Toast.LENGTH_LONG).show();
        }

        finish();
    }
}