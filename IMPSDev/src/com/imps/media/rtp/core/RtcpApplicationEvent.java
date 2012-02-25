
package com.imps.media.rtp.core;


/**
 * RTCP application event
 * 
 * @author liwenhaosuper
 */
public class RtcpApplicationEvent extends RtcpEvent {

	/**
	 * Constructor
	 * 
	 * @param packet RTCP APP packet
	 */
	public RtcpApplicationEvent(RtcpAppPacket packet) {
		super(packet);
	}
}
