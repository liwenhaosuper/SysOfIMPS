
package com.imps.media.rtp.core;


/**
 * RTCP sender report event
 * 
 * @author liwenhaosuper
 */
public class RtcpSenderReportEvent extends RtcpEvent {

	/**
	 * Constructor
	 * 
	 * @param packet RTCP SR packet
	 */
	public RtcpSenderReportEvent(RtcpSenderReportPacket packet) {
		super(packet);
	}
}
