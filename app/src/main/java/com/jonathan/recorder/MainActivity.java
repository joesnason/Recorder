package com.jonathan.recorder;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.AndroidException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {


    private int audioSource = MediaRecorder.AudioSource.DEFAULT;
    private static int sampleRate = 8000;
    private int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    private int audioFormat = AudioFormat.ENCODING_PCM_8BIT;
    private int bufferSizeInBytes = 0;
    private AudioRecord mAudioRecord;


    private int[] SampleRates = {8000, 11025, 16000, 22050, 32000, 44100, 47250, 48000};
    private int[] AudioFormats = {AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT};
    private int[] ChannelConfigs = {AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                                AudioFormat.CHANNEL_CONFIGURATION_STEREO};




    public static final int DRAW_LINE = 1;
    private DrawHandler mDrawHandler = null;
    private AudioRecordThread mAudioRecordThread = null;

    private Button btnRecord;
    private Boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAudioRecordThread = new AudioRecordThread();

//        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRate,channelConfig,audioFormat);
//        Log.d("JOJO","buffer size: " +bufferSizeInBytes);
//        mAudioRecord = new AudioRecord(audioSource,sampleRate,channelConfig,audioFormat,bufferSizeInBytes);


        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.RECORD_AUDIO)){

            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO} ,
                        1);
            }
        }

        btnRecord = (Button) findViewById(R.id.record);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRecording){
                    btnRecord.setText("Start Recording");
                    isRecording = false;
                    mAudioRecord.stop();
                    mAudioRecord.release();
                    mAudioRecordThread.cancel();



                } else {
                    btnRecord.setText("Stop Recroding");
                    isRecording = true;



                    for(int SampleRate : SampleRates){
                        for( int AudioFormat : AudioFormats){
                            for(int ChannelConfig : ChannelConfigs){
                                try {

                                    bufferSizeInBytes = AudioRecord.getMinBufferSize(SampleRate,ChannelConfig,AudioFormat);
                                    if (bufferSizeInBytes < 0) {
                                        continue;
                                    }

                                    mAudioRecord = new AudioRecord(audioSource,SampleRate,ChannelConfig,AudioFormat,bufferSizeInBytes);

                                    if(mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                                        Log.d ("JOJO", "sampleRate: " + SampleRate + " channelConfig: " + ChannelConfig + " audioFormat: " + AudioFormat);

                                    }

                                    mAudioRecord.release();
                                    mAudioRecord = null;

                                }catch (Exception e){

                                }
                            }
                        }
                    }





                    //mAudioRecord.startRecording();

                    //mAudioRecordThread.start();
                }

            }
        });

        mDrawHandler = new DrawHandler();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {



                break;
            }
            default : {
                break;
            }



        }

    }

    private class AudioRecordThread extends Thread {

        @Override
        public void run() {
            byte[] audiodata = new byte[bufferSizeInBytes];
            int readsize = 0;

            while(isRecording){
                readsize = mAudioRecord.read(audiodata,0,bufferSizeInBytes);
                mDrawHandler.obtainMessage(DRAW_LINE,readsize,-1,audiodata);

            }

        }

        public void cancel(){
            isRecording = false;
        }
    }

    private class DrawHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case DRAW_LINE:
                    byte[] data = (byte[]) msg.obj;
                    int length = msg.arg1;

                    Log.d("JOJO","get data size: " + length);

                    break;
                default:
                    break;
            }
        }
    }
}
