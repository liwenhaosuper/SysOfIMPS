
package com.imps.media.rtp.core;

/**
 * RTCP events listener interface
 * 
 * @author liwenhaosuper
 */
public interface RtcpEventListener {
	/**
	 * Receive RTCP event
	 * 
	 * @param event RTCP event
	 */
	void receiveRtcpEvent(RtcpEvent event);
}
