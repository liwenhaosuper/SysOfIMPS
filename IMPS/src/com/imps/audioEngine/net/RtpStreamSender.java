package com.imps.audioEngine.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Random;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.imps.audioEngine.codecs.*;

public class RtpStreamSender extends Thread{

	
	/** The RtpSocket */
	public RtpSocket rtp_socket = null;
	
	int frame_size = 160;
	int samp_rate =  8000 ;
	int frame_rate = samp_rate/frame_size ;//50
	
	public Random random;
	
	public boolean running = false;
	
	public static int delay = 0;
	public static int m = 1;
	public static String dest_ip;
	public static int dest_port;
	
	public RtpStreamSender(ImpsSocket src_socket,String dest_ip,int dest_port){
		try {
			rtp_socket = new RtpSocket(src_socket, InetAddress
					.getByName(dest_ip), dest_port);
			RtpStreamSender.dest_ip = dest_ip;
			RtpStreamSender.dest_port = dest_port;
		} catch (Exception e) {
//			if (!Sipdroid.release) e.printStackTrace();
		}
	}
	
	DatagramSocket udpSocket ;
	DatagramPacket udpPacket ;
	
	public void run() {
		
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		
		int min = AudioRecord.getMinBufferSize(samp_rate, 
				AudioFormat.CHANNEL_CONFIGURATION_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);
		
		long frame_period = 1000 / frame_rate;
		frame_rate *= 1.5;//75
		byte[] buffer = new byte[frame_size + 12];//172字节
		
		try {
			udpSocket = new DatagramSocket ();
			udpPacket  = new DatagramPacket (buffer,frame_size+12);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		
		RtpPacket rtp_packet = new RtpPacket(buffer,0);
		rtp_packet.setPayloadType(0);
		
		AudioRecord record = null;
		short[] lin = new short[frame_size*(frame_rate+1)];//(160*76)
		int num,ring = 0,pos;
		random = new Random();
		
		
		ulaw pcmu = new ulaw ();
		pcmu.init();
		
		record = new AudioRecord(MediaRecorder.AudioSource.MIC,samp_rate, AudioFormat.CHANNEL_CONFIGURATION_MONO, 
				AudioFormat.ENCODING_PCM_16BIT, 
				min);
		
		record.startRecording();
		
		int micgain = 0;
		long last_tx_time = 0;
		long next_tx_delay;
		long now;
		running = true;
		int seqn = 0;
		long time = 0;
		m = 1;
		
		while(running)
		{
			
			 if (frame_size < 480) {
				 now = System.currentTimeMillis();
				 next_tx_delay = frame_period - (now - last_tx_time);
				 last_tx_time = now;
				 if (next_tx_delay > 0) {
					 try {
						 sleep(next_tx_delay);
					 } catch (InterruptedException e1) {
					 }
					 last_tx_time += next_tx_delay-2;
				 }
			 }
			 pos = (ring+delay*frame_rate*frame_size)%(frame_size*(frame_rate+1));
			 num = record.read(lin,pos,frame_size);
			 System.out.println("num:"+num);
			 
			 if (num <= 0)
				 continue;
			 calc2(lin,pos,num);
			 
			 num = pcmu.encode(lin, ring%(frame_size*(frame_rate+1)), buffer, num);
			 
			 ring += frame_size;
 			 rtp_packet.setSequenceNumber(seqn++);
 			 rtp_packet.setTimestamp(time);
 			 rtp_packet.setPayloadLength(num);//160
 			 
 			 System.out.println("record time:"+time+":ring--"+time);
 			 
 			 try {
 				udpPacket.setData(rtp_packet.packet);
 	 			udpPacket.setLength(rtp_packet.packet_len);
 	 			udpPacket.setAddress(InetAddress.getByName(dest_ip));
 	 			udpPacket.setPort(dest_port);
 	 			Log.d("RtpStreamSender", "dest_ip is "+dest_ip);
				udpSocket.send(udpPacket);
				Log.d("RtpStreamSender", " audio packet sent has been called!!!!");
			} catch (IOException e) {
				e.printStackTrace();
			}
// 			try {
//				 rtp_socket.send(rtp_packet);
//				 if (m == 2) //m=2的情况不可能发生
//					 rtp_socket.send(rtp_packet);
//			 } catch (IOException e) {
//			 }
			 
			 time += frame_size;
			 
		}
	}
	
	
	public void calc2(short[] lin,int off,int len) {
		int i,j;
		
		for (i = 0; i < len; i++) {
			j = lin[i+off];
			lin[i+off] = (short)(j>>1);
		}
	}
	
	
	public void stopPcmu(){
		running = false;
	}
}
