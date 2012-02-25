package com.imps.media.audio;


import java.util.LinkedList;

import com.imps.IMPSDev;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class Track extends Thread{
		private static String TAG = Track.class.getCanonicalName();
		private static boolean DEBUG = IMPSDev.isDEBUG();
        private AudioTrack audioTrack;
        private int minbuffersize;
        public LinkedList<byte[]> data = new LinkedList<byte[]>();
        @Override 
        public void run() {
                
                if(DEBUG) Log.i("TAG", "TRACK--------------"+Thread.currentThread().getId());
                
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
