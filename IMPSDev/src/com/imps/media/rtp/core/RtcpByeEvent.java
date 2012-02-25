
package com.imps.media.rtp.core;


/**
 * RTCP bye event
 * 
 * @author liwenhaosuper
 */
public class RtcpByeEvent extends RtcpEvent {

	/**
	 * Constructor
	 * 
	 * @param packet RTCP BYE packet
	 */
	public RtcpByeEvent(RtcpByePacket packet) {
		super(packet);
	}
}
