package com.imps.audio;


import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class Record extends Thread{
        private AudioRecord audioRecord;
        private int minbuffersize;
        public LinkedList<byte[]> dataList = new LinkedList<byte[]>();
        public boolean stop = false;
        @Override
        public void run() {
                Log.i("TAG", "Record--------------"+Thread.currentThread().getId());
                minbuffersize = AudioRecord.getMinBufferSize(8000, 
                         AudioFormat.CHANNEL_CONFIGURATION_MONO, 
                         AudioFormat.ENCODING_PCM_16BIT);
                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                8000,AudioFormat.CHANNEL_CONFIGURATION_MONO,
                                AudioFormat.ENCODING_PCM_16BIT,
                                minbuffersize);
                
                audioRecord.startRecording();
                while (true) {
                        byte[] audioData = new byte[minbuffersize];
                        audioRecord.read(audioData, 0, minbuffersize);
                         dataList.add(audioData);
                         if(stop)
                         {
                        	 Log.d("stop", "true");
                        	 audioRecord.stop();
                        	 break;
                         }
                         Log.d("record", "cycle");
                }
        }
        public void stopThread()
        {
        	stop = true;
        }
}