
package com.imps.media.rtp.core;


/**
 * RTCP receiver report event
 * 
 * @author liwenhaosuper
 */
public class RtcpReceiverReportEvent extends RtcpEvent {

	/**
	 * Constructor
	 * 
	 * @param packet RTCP RR packet
	 */
	public RtcpReceiverReportEvent(RtcpReceiverReportPacket packet) {
		super(packet);
	}
}
