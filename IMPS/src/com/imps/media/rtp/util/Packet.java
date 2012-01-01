
package com.imps.media.rtp.util;

/**
 * Generic packet
 * 
 * @author liwenhaosuper
 */
public class Packet {
	/**
	 * Data
	 */
	public byte[] data;
	
	/**
	 * Packet length
	 */
	public int length;

	/**
	 * Offset
	 */
	public int offset;
		
	/**
	 * Received at
	 */
	public long receivedAt;

	/**
	 * Constructor
	 */
	public Packet() {
	}

	/**
	 * Constructor
	 * 
	 * @param packet Packet
	 */
	public Packet(Packet packet) {
		data = packet.data;
		length = packet.length;
		offset = packet.offset;
		receivedAt = packet.receivedAt;
	}
}
