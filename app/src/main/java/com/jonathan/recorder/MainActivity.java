package com.jonathan.recorder;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {


    private int audioSource = MediaRecorder.AudioSource.MIC;
    private static int sampleRate = 8000;
    private int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int bufferSizeInByte = 0;


    private Button btnRecord;
    private Boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btnRecord = (Button) findViewById(R.id.record);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording){
                    btnRecord.setText("Start Recording");
                    isRecording = false;
                } else {
                    btnRecord.setText("Stop Recroding");
                    isRecording = true;
                }

            }
        });

    }
}
