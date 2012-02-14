
package com.imps.media.rtp.core;

/**
 * RTP statistics receiver
 * 
 * @author liwenhaosuper
 */
public class RtpStatisticsReceiver {
	/**
	 * Number of RTP packets received
	 */
	public int numPackets = 0;
	
	/**
	 * Number of RTP bytes received
	 */
	public int numBytes = 0;
	
	/**
	 * Number of bad RTP packet received
	 */
	public int numBadRtpPkts = 0;
}
