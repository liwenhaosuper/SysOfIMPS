
package com.imps.media.rtp.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/**
 * Android datagram server connection
 * 
 * @author liwenhaosuper
 */
public class AndroidDatagramConnection implements DatagramConnection {
	/**
	 * Datagram connection
	 */
	private DatagramSocket connection = null; 
	
	/**
	 * Constructor
	 */
	public AndroidDatagramConnection() {
	}

	/**
	 * Open the datagram connection
	 * 
	 * @throws IOException
	 */
	public void open() throws IOException {
		connection = new DatagramSocket();
	}

	/**
	 * Open the datagram connection
	 * 
	 * @param port Local port
	 * @throws IOException
	 */
	public void open(int port) throws IOException {
		connection = new DatagramSocket(port);
	}

	/**
	 * Close the datagram connection
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}
	
	/**
	 * Receive data with a specific buffer size
	 * 
	 * @param bufferSize Buffer size 
	 * @return Byte array
	 * @throws IOException
	 */
	public byte[] receive(int bufferSize) throws IOException {
		if (connection != null) {
			byte[] buf = new byte[bufferSize];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			connection.receive(packet);
			
			int packetLength = packet.getLength();
	        byte[] bytes =  packet.getData();
	        byte[] data = new byte[packetLength];
	        System.arraycopy(bytes, 0, data, 0, packetLength);
			return data;
		} else {
			throw new IOException("Connection not openned");
		}
	}
	
	/**
	 * Receive data
	 * 
	 * @return Byte array
	 * @throws IOException
	 */
	public byte[] receive() throws IOException {
		return receive(DatagramConnection.DEFAULT_DATAGRAM_SIZE);
	}
	
	/**
	 * Send data
	 * 
	 * @param remoteAddr Remote address
	 * @param remotePort Remote port
	 * @param data Data as byte array
	 * @throws IOException
	 */
	public void send(String remoteAddr, int remotePort, byte[] data) throws IOException {
		if (data == null) {
			return;
		}
		
		if (connection != null) {
			InetAddress address = InetAddress.getByName(remoteAddr);
			DatagramPacket packet = new DatagramPacket(data, data.length, address, remotePort);
			connection.send(packet);
		} else {
			throw new IOException("Connection not openned");
		}
	}
	
	/**
	 * Returns the local address
	 * 
	 * @return Address
	 * @throws IOException
	 */
	public String getLocalAddress() throws IOException {
		if (connection != null) {
			return connection.getLocalAddress().getHostAddress();
		} else {
			throw new IOException("Connection not openned");
		}
	}

	/**
	 * Returns the local port
	 * 
	 * @return Port
	 * @throws IOException
	 */
	public int getLocalPort() throws IOException {
		if (connection != null) {
			return connection.getLocalPort();
		} else {
			throw new IOException("Connection not openned");
		}
	}
}
