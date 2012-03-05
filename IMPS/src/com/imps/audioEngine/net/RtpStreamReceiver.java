package com.imps.audioEngine.net;


import java.io.IOException;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.imps.activities.AudioChat;
import com.imps.audioEngine.codecs.ulaw;

public class RtpStreamReceiver extends Thread{

	public static final int BUFFER_SIZE = 1024;
	public AudioChat activity;
	RtpPacket rtp_packet;
	
	RtpSocket rtp_socket = null;
	
	AudioTrack track;
	int maxjitter,minjitter,minjitteradjust,minheadroom;
	int cnt,cnt2,user,luser,luser2,lserver;
	public static int jitter,mu;
	boolean running = false;
	
	int sample_rate =  8000;
	
	ulaw pcmu ;
	
	public static int timeout;
	
	double avgheadroom;
	
	public static float good, late, lost, loss;
	
	int seq;
	
	public RtpStreamReceiver(ImpsSocket socket){
		rtp_socket = new RtpSocket(socket);
	}
	
	
	public void stopPcmu(){
		running = false;
	}
	
	
	public void run(){
		
		byte[] buffer = new byte[BUFFER_SIZE+12];
		rtp_packet = new RtpPacket(buffer, 0);
		
		running = true;
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
		
		setCodec();
		
		short lin[] = new short[BUFFER_SIZE];
		short lin2[] = new short[BUFFER_SIZE];
		int server, headroom, todo, len = 0, m = 1, expseq, getseq, vm = 1, gap, gseq=0;
		
		AudioManager am = (AudioManager) activity.mContext.getSystemService(Context.AUDIO_SERVICE);
		am.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,AudioManager.VIBRATE_SETTING_OFF);
		am.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,AudioManager.VIBRATE_SETTING_OFF);
		
		track.play();
		
//		DatagramSocket udpSocket = null;
//		DatagramPacket udpPacket = null;
		
//		try {
//			udpSocket = new DatagramSocket (2110);
////			udpPacket = new DatagramPacket (rtp_packet.packet,buffer.length);
//		} catch (SocketException e1) {
//			e1.printStackTrace();
//		}
		
		
		while(running)
		{
			try {
				rtp_socket.receive(rtp_packet);
				Log.d("RtpStreamReceiver", " audio packet received!!!!");
//				rtp_socket.datagram.setData(rtp_packet.packet);
//				rtp_socket.datagram.setLength(rtp_packet.packet_len);
//				
//				udpSocket.receive(rtp_socket.datagram);
//				System.out.println("receiver ok");
//				FileOutputStream file_out = new FileOutputStream ("/sdcard/test.dat");
//				file_out.write(rtp_socket.)
				
				if (timeout != 0) {
					track.pause();
					for (int i = maxjitter*2; i > 0; i -= BUFFER_SIZE)//6144
						write(lin2,0,i>BUFFER_SIZE?BUFFER_SIZE:i);
					cnt += maxjitter*2;
					track.play();
				}
				timeout = 0;
				
				if (running && timeout == 0)
				{	
					 gseq = rtp_packet.getSequenceNumber();
					 server = track.getPlaybackHeadPosition();
					 headroom = user-server;
					 
					 if (headroom > 2*jitter)
						 cnt += len;
					 else
						 cnt = 0;
					 
					 if (lserver == server)
						 cnt2++;
					 else
						 cnt2 = 0;
					 
					 if (cnt <= 500*mu || cnt2 >= 2 || headroom - jitter < len)
					 {
						 len = pcmu.decode(buffer, lin, rtp_packet.getPayloadLength());//len应该为160才对
					 }
					 
					 avgheadroom = avgheadroom * 0.99 + (double)headroom * 0.01;
					 if (headroom < minheadroom)
						 minheadroom = headroom;
		 			 if (headroom < 250*mu) { 
		 				 late++;
						 if (good != 0 && lost/good < 0.01)
							 if (late/good > 0.01 && jitter + minjitteradjust < maxjitter) {
								 jitter += minjitteradjust;
								 late = 0;
								 luser2 = user;
								 minheadroom = maxjitter*2;
							 }
						todo = jitter - headroom;
						write(lin2,0,todo>BUFFER_SIZE?BUFFER_SIZE:todo);
					 }

					 if (cnt > 500*mu && cnt2 < 2) {
						 todo = headroom - jitter;
						 System.out.println("--todo:"+todo+"--len:"+len);
						 if (todo < len)
							 write(lin,todo,len-todo);
					 } else
					 {
						 System.out.println("--write 0-len");
						 write(lin,0,len);
					 }
						 
					 System.out.println("--seq:"+seq);
					 if (seq != 0) {
						 getseq = gseq&0xff;
						 expseq = ++seq&0xff;
						 if (m == RtpStreamSender.m) vm = m;
						 gap = (getseq - expseq) & 0xff;
						 if (gap > 0) {
							 if (gap > 100) gap = 1;
							 loss += gap;
							 lost += gap;
							 good += gap - 1;
						 } else {
							 if (m < vm)
								 loss++;
						 }
						 good++;
						 if (good > 100) {
							 good *= 0.99;
							 lost *= 0.99;
							 loss *= 0.99;
							 late *= 0.99;
						 }
					 }
					 m = 1;
					 seq = gseq;
					 if (user >= luser + 8000*mu) {
						 System.out.println("---action");
						 luser = user;
						 if (user >= luser2 + 160000*mu && good != 0 && lost/good < 0.01 && avgheadroom > minheadroom) {
							 int newjitter = (int)avgheadroom - minheadroom + minjitter;
							 if (jitter-newjitter > minjitteradjust)
								 jitter = newjitter;
							 minheadroom = maxjitter*2;
							 luser2 = user;
						 }
					 }
					 lserver = server;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	void write(short a[],int b,int c) {
		synchronized (this) {
			user += track.write(a,b,c);
		}
	}
	
	private void setCodec(){
		pcmu = new ulaw ();
		pcmu.init();
		
		mu = sample_rate/8000;
		maxjitter = AudioTrack.getMinBufferSize(sample_rate, 
				AudioFormat.CHANNEL_CONFIGURATION_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);//1486
		
		if (maxjitter < 2*2*BUFFER_SIZE*3*mu)
			maxjitter = 2*2*BUFFER_SIZE*3*mu;//12288
		
		track = new AudioTrack(AudioManager.STREAM_VOICE_CALL,sample_rate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT,
				maxjitter, AudioTrack.MODE_STREAM);
		
		maxjitter /= 2*2;//3072
		minjitter = minjitteradjust = 500*mu;
		jitter = 875*mu;
		minheadroom = maxjitter*2;//6144
		timeout = 1;
		luser = luser2 = -8000*mu;//-8000
		cnt = cnt2 = user = lserver = 0;
	}
	
	
}
