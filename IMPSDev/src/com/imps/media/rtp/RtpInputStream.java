
package com.imps.media.rtp;



import com.imps.media.rtp.core.RtcpPacketReceiver;
import com.imps.media.rtp.core.RtcpSession;
import com.imps.media.rtp.core.RtpPacket;
import com.imps.media.rtp.core.RtpPacketReceiver;
import com.imps.media.rtp.format.Format;
import com.imps.media.rtp.util.Buffer;

/**
 * RTP input stream
 *
 * @author liwenhaosuper
 */
public class RtpInputStream implements IProcessorInputStream {
    /**
     * Local port
     */
    private int localPort;

	/**
	 * RTP receiver
	 */
	private RtpPacketReceiver rtpReceiver =  null;

	/**
	 * RTCP receiver
	 */
	private RtcpPacketReceiver rtcpReceiver =  null;

    /**
     * Input buffer
     */
	private Buffer buffer = new Buffer();

    /**
     * Input format
     */
	private Format inputFormat = null;

    /**
     * RTCP Session
     */
    private RtcpSession rtcpSession = null;

    /**
     * Constructor
     *
     * @param localPort Local port
     * @param inputFormat Input format
     */
    public RtpInputStream(int localPort, Format inputFormat) {
		this.localPort = localPort;
		this.inputFormat = inputFormat;

        rtcpSession = new RtcpSession(false, 16000);
    }

    /**
     * Open the input stream
     *
     * @throws Exception
     */
    public void open() throws Exception {

    	// Create the RTP receiver
        rtpReceiver = new RtpPacketReceiver(localPort, rtcpSession);
    	// Create the RTCP receiver
        rtcpReceiver = new RtcpPacketReceiver(localPort + 1, rtcpSession);
    }

    /**
     * Close the input stream
     */
    public void close() {
		try {
			// Close the RTP receiver
			if (rtpReceiver != null) {
				rtpReceiver.close();
			}

			// Close the RTCP receiver
			if (rtcpReceiver != null) {
				rtcpReceiver.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Returns the RTCP receiver
     *
     * @return RTCP receiver
     */
    public RtcpPacketReceiver getRtcpReceiver() {
    	return rtcpReceiver;
    }

    /**
     * Read from the input stream without blocking
     *
     * @return Buffer
     * @throws Exception
     */
    public Buffer read() throws Exception {
    	// Wait and read a RTP packet
    	RtpPacket rtpPacket = rtpReceiver.readRtpPacket();
    	//System.out.println("RtpInputStream:read over...");
    	if (rtpPacket == null) {
    		return null;
    	}

    	// Create a buffer
        buffer.setData(rtpPacket.data);
        buffer.setLength(rtpPacket.payloadlength);
        buffer.setOffset(0);
        buffer.setFormat(inputFormat);
    	buffer.setSequenceNumber(rtpPacket.seqnum);
    	buffer.setFlags(Buffer.FLAG_RTP_MARKER | Buffer.FLAG_RTP_TIME);
    	buffer.setRTPMarker(rtpPacket.marker!=0);
    	buffer.setTimeStamp(rtpPacket.timestamp);

    	// Set inputFormat back to null
    	inputFormat = null;
    	return buffer;
    }
}
