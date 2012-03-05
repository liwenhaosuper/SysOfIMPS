package com.imps.audio;


import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class Track extends Thread{
        private AudioTrack audioTrack;
        private int minbuffersize;
        public LinkedList<byte[]> data = new LinkedList<byte[]>();
        @Override 
        public void run() {
                
                Log.i("TAG", "TRACK--------------"+Thread.currentThread().getId());
                
                minbuffersize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                                8000,
                                AudioFormat.CHANNEL_CONFIGURATION_MONO, 
                                AudioFormat.ENCODING_PCM_16BIT, 
                                minbuffersize,
                                AudioTrack.MODE_STREAM);
                audioTrack.play();
                while (data.size() > 1) {
                    byte[] tmp = data.removeFirst();
                    audioTrack.write(tmp, 0, tmp.length);
                }
                audioTrack.stop();
                
                        
        }
        
        
        
}
