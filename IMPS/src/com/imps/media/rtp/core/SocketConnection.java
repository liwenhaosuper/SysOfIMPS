
package com.imps.media.rtp.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Socket client connection
 * 
 * @author liwenhaosuper
 */
public interface SocketConnection {
	/**
	 * Open the socket
	 * 
	 * @param remoteAddr Remote address
	 * @param remotePort Remote port
	 * @throws IOException
	 */
	public void open(String remoteAddr, int remotePort) throws IOException;

	/**
	 * Close the socket
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException;
	
	/**
	 * Returns the socket input stream
	 * 
	 * @return Input stream
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException;
	
	/**
	 * Returns the socket output stream
	 * 
	 * @return Output stream
	 * @throws IOException
	 */
	public OutputStream getOutputStream() throws IOException;

	/**
	 * Returns the remote address of the connection
	 * 
	 * @return Address
	 * @throws IOException
	 */
	public String getRemoteAddress() throws IOException;
	
	/**
	 * Returns the remote port of the connection
	 * 
	 * @return Port
	 * @throws IOException
	 */
	public int getRemotePort() throws IOException;

	/**
	 * Returns the local address of the connection
	 * 
	 * @return Address
	 * @throws IOException
	 */
	public String getLocalAddress() throws IOException;
	
	/**
	 * Returns the local port of the connection
	 * 
	 * @return Port
	 * @throws IOException
	 */
	public int getLocalPort() throws IOException;
	
	/**
	 * Get the timeout for this socket during which a reading
	 * operation shall block while waiting for data
	 * 
	 * @return Milliseconds
	 * @throws IOException
	 */
	public int getSoTimeout() throws IOException;

	/**
	 * Set the timeout for this socket during which a reading
	 * operation shall block while waiting for data
	 * 
	 * @param timeout Timeout in milliseconds
	 * @throws IOException
	 */
	public void setSoTimeout(int timeout) throws IOException;
}
