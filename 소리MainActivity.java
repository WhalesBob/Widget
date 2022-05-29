package com.example.sound;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView textView1;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = findViewById(R.id.TV1);
        textView2 = findViewById(R.id.TV2);
        SeekBar seekBar1 = (SeekBar) findViewById(R.id.SB1);
        SeekBar seekBar2 = (SeekBar) findViewById(R.id.SB2);
        AudioManager audioManager =(AudioManager)getSystemService(AUDIO_SERVICE);

        seekBar1.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        seekBar2.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
        seekBar2.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBar2.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_RING,progress,0);
                textView1.setText("알림 볼륨 : " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                textView2.setText("미디어 볼륨 : " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
