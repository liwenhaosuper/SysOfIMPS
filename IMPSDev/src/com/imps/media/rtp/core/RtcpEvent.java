
package com.imps.media.rtp.core;


/**
 * Abstract RTCP event
 * 
 * @author liwenhaosuper
 */
public abstract class RtcpEvent {
	/**
	 * RTCP packet
	 */
	private RtcpPacket packet;
	
	/**
	 * Constructor
	 * 
	 * @param packet RTCP packet
	 */
	public RtcpEvent(RtcpPacket packet) {
		this.packet = packet;
	}

	/**
	 * Returns the RTCP packet
	 * 
	 * @return Packet
	 */
	public RtcpPacket getPacket() {
		return packet;
	}	
}
