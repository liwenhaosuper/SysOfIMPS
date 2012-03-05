package com.imps.media.rtp.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;


/**
 * Android socket connection
 * 
 * @author liwenhaosuper
 */
public class AndroidSocketServerConnection implements SocketServerConnection {
	/**
	 * Socket server connection
	 */
	private ServerSocket acceptSocket = null; 

    /**
	 * Constructor
	 */
	public AndroidSocketServerConnection() {
	}

	/**
	 * Open the socket
	 * 
	 * @param port Local port
	 * @throws IOException
	 */
	public void open(int port) throws IOException {
		acceptSocket = new ServerSocket(port);
	}

	/**
	 * Close the socket
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (acceptSocket != null) {
			acceptSocket.close();
			acceptSocket = null;		
		}
	}
	
	/**
	 * Accept connection
	 * 
	 * @return Socket connection
	 * @throws IOException
	 */
	public SocketConnection acceptConnection() throws IOException {
		if (acceptSocket != null) { 
			Socket socket = acceptSocket.accept();		
			return new AndroidSocketConnection(socket);
		} else {
			throw new IOException("Connection not openned");
		}
	}
}
