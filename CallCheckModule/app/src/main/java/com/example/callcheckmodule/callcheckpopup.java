package com.example.callcheckmodule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


public class callcheckpopup extends Activity {
    TextView txtView;
    Button btn_call_to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v("test", "popup 성공");

        requestWindowFeature(Window.FEATURE_NO_TITLE);//타이틀 바 제거
        setContentView(R.layout.callcheckpopup);

        Log.v("test", "onCreate()");

        txtView = (TextView) findViewById(R.id.txtView);
        btn_call_to = (Button) findViewById(R.id.btn_call_to);

        Intent intent = getIntent();
        String recentMissedCall = intent.getStringExtra("recentMissedCall");//부재중 전화의 정보를 수신
        txtView.setText(recentMissedCall);
        String missedPhNum = intent.getStringExtra("missedCallPhNum");//부재중 전화의 전화번호를 수신

        Log.v("test", missedPhNum);
        //전화 버튼
        btn_call_to.setOnClickListener(view -> phoneCall(missedPhNum));


    }

    //확인 버튼. 팝업 제거.
    public void mOnClose(View v) {
        Log.v("test", "mOnClose()");
        finish();
    }

    void phoneCall(String phNum)//전해받은 전화번호로 전화를 거는 함수
    {
        Log.v("test", "phoneCall()");

        checkNull CN = new checkNull();
        if (CN.checkDefault(phNum)) {//만약 최근 10일 내에 부재중 전화가 없을 시 전화를 걸 수 없도록 조치
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(phNum));
            startActivity(intent);
        } else {
            Log.v("test", "no phn Num");
            txtView.setText("부재중 전화가 없는 상황에서 전화를 걸 수 없습니다.");
        }

    }
}

class checkNull {
    boolean checkDefault (String phNum) {//전화를 걸기 위한 String 자료의 내용이 null인지 아닌지 판단하는 함수
        return !phNum.equals("default");
    }
}
