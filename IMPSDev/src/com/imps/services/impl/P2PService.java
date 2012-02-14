
package com.imps.services.impl;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

import com.imps.IMPSDev;
import com.imps.media.audioEngine.net.ImpsSocket;

public class P2PService{
	private static P2PService instance;
	private static DatagramPacket udpPacket;
	private static String ip;
	private static int port;
	private static final boolean DEBUG = IMPSDev.isDEBUG();
	private static final String TAG = P2PService.class.getCanonicalName();
	private static ImpsSocket udpSocket;
	private static boolean isStarted = false;
	
	public static void init(String server_ip, int server_port){
		ip = server_ip;
		port = server_port;
		instance = new P2PService();
	}
	public DatagramPacket getPacket(){
		return udpPacket;
	}
	public ImpsSocket getSocket(){
		return udpSocket;
	}
	public static P2PService getInstance(){
		return instance;
	}
	public boolean start(){
		if(isStarted){
			return true;
		}
		isStarted = true;
		prepare();
		return true;
	}
	public boolean isStarted(){
		return isStarted;
	}
	public void prepare(){
		try {
			if(udpSocket!=null&&udpSocket.isBound()){
				udpSocket.close();
			}
			udpSocket = new ImpsSocket(1400);
			udpSocket.setReuseAddress(true);
			udpSocket.setSoTimeout(0);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch ( UnknownHostException e){
			e.printStackTrace();
		}
		byte buffer[] = new byte[100];
		udpPacket  = new DatagramPacket(buffer,100);
		try {
			udpPacket.setAddress(InetAddress.getByName(ip));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 	udpPacket.setPort(port);
	 	if(DEBUG)Log.d(TAG,"Socket prepare...");
	}
	public boolean stop() {
		// TODO Auto-generated method stub
		if(!udpSocket.isClosed())
			udpSocket.close();
		isStarted = false;
		return true;
	}
}
/*package com.imps.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

import com.imps.IMPSDev;
import com.imps.R;
import com.imps.activities.AudioChat;
import com.imps.base.net.IoSession;
import com.imps.basetypes.CommandId;
import com.imps.events.IP2PEvent;
import com.imps.media.audioEngine.net.ImpsSocket;
import com.imps.net.handler.NetMsgLogicHandler;
import com.imps.net.handler.UserManager;
import com.imps.services.IP2PService;

public class P2PService implements IP2PService,IP2PEvent{

	private static final boolean DEBUG = IMPSDev.isDEBUG();
	private static final String TAG = P2PService.class.getCanonicalName();
	private static IoSession session;
	private static P2PService instance;
	private static String ip;
	private static int port;
	private static DatagramPacket udpPacket;
	private static ImpsSocket udpSocket;
	
	public static void init(String server_ip, int server_port){
		ip = server_ip;
		port = server_port;
		instance = new P2PService();
		NetMsgLogicHandler.getInstance().addP2PEventHandler(instance);
	}
	public static P2PService getInstance(){
		return instance;
	}
	public DatagramPacket getPacket(){
		return udpPacket;
	}
	public ImpsSocket getSocket(){
		return udpSocket;
	}
	public void prepare(){
		try {
			udpSocket = new ImpsSocket(13000);
			udpSocket.setReuseAddress(true);
			udpSocket.setSoTimeout(0);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch ( UnknownHostException e){
			e.printStackTrace();
		}
		byte buffer[] = new byte[100];
		udpPacket  = new DatagramPacket(buffer,100);
		try {
			udpPacket.setAddress(InetAddress.getByName(ip));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 	udpPacket.setPort(port);
	 	if(DEBUG)Log.d(TAG,"Socket prepare...");
	}


	@Override
	public boolean isStarted() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean start() {
		// TODO Auto-generated method stub
		prepare();
		return true;
	}

	@Override
	public boolean stop() {
		// TODO Auto-generated method stub
		if(!udpSocket.isClosed())
			udpSocket.close();
		return true;
	}

	@Override
	public void onRecvSendPTPVideoRsp(String friName,boolean rel,String ip,int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRecvSendPTPVideoReq(String friName,String ip,int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRecvSendPTPAudioRsp(String friName,boolean rel,String ip,int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRecvSendPTPAudioReq(String friName,String ip,int port) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void SendPTPAudioReq(String friName, String ip, int port) {
		// TODO Auto-generated method stub
		ByteArrayOutputStream bos = new ByteArrayOutputStream(150);
		DataOutputStream output = new DataOutputStream(bos);
		try {
			output.writeByte(CommandId.C_PTP_AUDIO_REQ);
			output.writeLong(UserManager.getGlobaluser().getUsername().getBytes("gb2312").length);
			output.write(UserManager.getGlobaluser().getUsername().getBytes("gb2312"));
			output.writeLong(friName.getBytes("gb2312").length);
			output.write(friName.getBytes("gb2312"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		udpPacket.setData(bos.toByteArray());
		try {
			udpSocket.send(udpPacket);
			if(DEBUG) Log.d(TAG,"Audio req sent...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void SendPTPVideoReq(String friName, String ip, int port) {
		// TODO Auto-generated method stub

		
	}

	@Override
	public void onP2PAudioReq(String friName, String ip, int port) {
		// TODO Auto-generated method stub
		Log.d(TAG,"audio from "+friName+" received...");
		ServiceManager.showNotification(R.drawable.new_audio_notification, R.drawable.new_audio_notification,
				friName+":"+IMPSDev.getContext().getString(R.string.audio_chat_request_notify), AudioChat.class, friName,ip,Integer.toString(port));
	}

	@Override
	public void onP2PAudioRsp(String friName, boolean result, String ip,
			int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onP2PAudioError(String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onP2PAudioReqSuccess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onP2PVideoReq(String friName, String ip, int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onP2PVideoRsp(String friName, boolean result, String ip,
			int port) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onP2PVideoError(String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onP2PVideoReqSuccess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void SendPTPAudioRsp(String friName, boolean rel, String ip, int port) {
		// TODO Auto-generated method stub
		ByteArrayOutputStream bos = new ByteArrayOutputStream(150);
		DataOutputStream output = new DataOutputStream(bos);
		try {
			output.writeByte(CommandId.C_PTP_AUDIO_RSP);
			output.writeLong(UserManager.getGlobaluser().getUsername().getBytes("gb2312").length);
			output.write(UserManager.getGlobaluser().getUsername().getBytes("gb2312"));
			output.writeLong(friName.getBytes("gb2312").length);
			output.write(friName.getBytes("gb2312"));
			output.writeInt(rel==true?1:0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		udpPacket.setData(bos.toByteArray());
		try {
			udpSocket.send(udpPacket);
			if(DEBUG) Log.d(TAG,"Audio rsp sent...");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void SendPTPVideoRsp(String friName, boolean rel, String ip, int port) {
		// TODO Auto-generated method stub
		
	}

}
*/