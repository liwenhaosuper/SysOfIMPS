

package com.imps.media.audioEngine.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
public class ImpsSocket extends DatagramSocket {

	public static boolean loaded = false;
	
	public ImpsSocket(int port) throws SocketException, UnknownHostException {
		super(port);
//		super(!loaded?port:0);
//		if (loaded) {
//			impl = new PlainDatagramSocketImpl();
//			impl.create();
//			impl.bind(port,InetAddress.getByName("0"));
//		}
	}
	
	public void close() {
		super.close();
//		if (loaded) impl.close();
	}
	
	public void setSoTimeout(int val) throws SocketException {
//		if (loaded) impl.setOption(SocketOptions.SO_TIMEOUT, val);
//		else super.setSoTimeout(val);
		super.setSoTimeout(val);
	}
	
	public void receive(DatagramPacket pack) throws IOException {
//		if (loaded) impl.receive(pack);
//		else
//			super.receive(pack);
		super.receive(pack);
	}
	
	public void send(DatagramPacket pack) throws IOException {
//		if (loaded)
//			{
//			impl.send(pack);
//			}
//		else
//		{
//			super.send(pack);
//		}
		super.send(pack);
	}
	
	public boolean isConnected() {
		if (loaded) return true;
		else return super.isConnected();
	}
	
	public void disconnect() {
		if (!loaded) super.disconnect();
	}
	
	public void connect(InetAddress addr,int port) {
		if (!loaded) super.connect(addr,port);
	}
}
