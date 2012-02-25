package com.imps.media.audio;


import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.widget.ImageView;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.basetypes.ListContentEntity;

public class AudioPlay extends Thread{
		private static String TAG = Track.class.getCanonicalName();
		private static boolean DEBUG = IMPSDev.isDEBUG();
        private AudioTrack audioTrack;
        private int minbuffersize;
        public LinkedList<byte[]> data = new LinkedList<byte[]>();
        private ImageView v;
        private ListContentEntity entity;
        public AudioPlay(ListContentEntity entity,ImageView v){
        	this.entity = entity;
        	data = entity.audioData;
        	this.v = v;
        }
        @Override 
        public void run() {
        		v.setBackgroundResource(R.drawable.icon_sound_play);
        		entity.status = ListContentEntity.PLAY;
                minbuffersize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                                8000,
                                AudioFormat.CHANNEL_CONFIGURATION_MONO, 
                                AudioFormat.ENCODING_PCM_16BIT, 
                                minbuffersize,
                                AudioTrack.MODE_STREAM);
                audioTrack.play();
                for(int i=0;i<data.size();i++) {
                    byte[] tmp = data.get(i);
                    audioTrack.write(tmp, 0, tmp.length);
                }
                audioTrack.stop();
                v.setBackgroundResource(R.drawable.icon_sound_default);
                entity.status = ListContentEntity.MUTE;
                        
        }
        
        
        
}
