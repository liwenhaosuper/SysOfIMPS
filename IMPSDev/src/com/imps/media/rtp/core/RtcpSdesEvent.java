
package com.imps.media.rtp.core;


/**
 * RTCP session description event
 * 
 * @author liwenhaosuper
 */
public class RtcpSdesEvent extends RtcpEvent {

	/**
	 * Constructor
	 * 
	 * @param packet RTCP SDES packet
	 */
	public RtcpSdesEvent(RtcpSdesPacket packet) {
		super(packet);
	}
}
