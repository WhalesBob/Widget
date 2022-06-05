package com.example.messagecheckmodule;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
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

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.message_send_popup);

        SMS_Sending SMS_S = new SMS_Sending();

        txt_to = (TextView)findViewById(R.id.txt_to);

        btn_send_message = (Button)findViewById(R.id.btn_send_message);

        Intent intent = getIntent();
        String messageTo = intent.getStringExtra("missedMessageNum");

        String messageToTxt = setTxtPhNum(messageTo);
        txt_to.setText(messageToTxt);



        btn_send_message.setOnClickListener(view -> SMS_S.Send_Message(messageTo));
    }

    public void mOnClose (View v) {
        finish();
    }

    String setTxtPhNum (String messageTo)
    {
        return "To. " + messageTo;
    }

}

class SMS_Sending extends AppCompatActivity {

    EditText et_sendingtxt;

    void Send_Message (String phNum)
    {
        et_sendingtxt = (EditText)findViewById(R.id.et_sendingtxt);
        String inserted_message = et_sendingtxt.getText().toString();
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
    }
}