package com.example.callcheckmodule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class callcheckpopup extends Activity {
    TextView txtView;
    Button btn_call_to;
    Button btn_cut_popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.callcheckpopup);

        txtView = (TextView)findViewById(R.id.txtView);
        btn_call_to = (Button)findViewById(R.id.btn_call_to);
        btn_cut_popup = (Button)findViewById(R.id.btn_cut_popup);

        Intent intent = getIntent();
        String recentMissedCall = intent.getStringExtra("recentMissedCall");//부재중 전화의 정보를 수신
        txtView.setText(recentMissedCall);
        String missedPhNum = intent.getStringExtra("missedCallPhNum");//부재중 전화의 전화번호를 수신

        //전화 버튼
        btn_call_to.setOnClickListener(view -> phoneCall(missedPhNum));

        //확인 버튼. 팝업 제거.
        btn_cut_popup.setOnClickListener(view -> finish());

    }

    void phoneCall (String phNum)//전해받은 전화번호로 전화를 거는 함수
    {
        checkNull CN = new checkNull();
        if(CN.checkNull(phNum)) {//만약 최근 10일 내에 부재중 전화가 없을 시 전화를 걸 수 없도록 조치
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(phNum));
            startActivity(intent);
        }
    }
}

class checkNull {
    boolean checkNull (String phNum) {//전화를 걸기 위한 String 자료의 내용이 null인지 아닌지 판단하는 함수
        return phNum != null;
    }
}
