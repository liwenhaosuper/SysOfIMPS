
package com.imps.media.rtp;

import java.io.IOException;

import com.imps.media.rtp.core.RtcpPacketReceiver;
import com.imps.media.rtp.core.RtcpPacketTransmitter;
import com.imps.media.rtp.core.RtcpSession;
import com.imps.media.rtp.core.RtpConfig;
import com.imps.media.rtp.core.RtpPacketReceiver;
import com.imps.media.rtp.core.RtpPacketTransmitter;
import com.imps.media.rtp.util.Buffer;

/**
 * RTP output stream
 *
 * @author liwenhaosuper
 */
public class RtpOutputStream implements IProcessorOutputStream {
    /**
     * Remote address
     */
    private String remoteAddress;

    /**
     * Remote port
     */
    private int remotePort;

    /**
     * Local port
     */
    private int localRtpPort = -1;

    /**
     * RTP receiver
     */
    private RtpPacketReceiver rtpReceiver = null;

    /**
     * RTCP receiver
     */
    private RtcpPacketReceiver rtcpReceiver = null;

	/**
	 * RTP transmitter
	 */
	private RtpPacketTransmitter rtpTransmitter =  null;

	/**
	 * RTCP transmitter
	 */
	private RtcpPacketTransmitter rtcpTransmitter =  null;

    /**
     * RTCP Session
     */
    private RtcpSession rtcpSession = null;

    /**
     * Constructor
     *
     * @param remoteAddress Remote address
     * @param remotePort Remote port
     */
    public RtpOutputStream(String remoteAddress, int remotePort) {
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;

        rtcpSession = new RtcpSession(true, 16000);
    }

    /**
     * Constructor
     *
     * @param remoteAddress Remote address
     * @param remotePort Remote port
     */
    public RtpOutputStream(String remoteAddress, int remotePort, int localRtpPort) {
		this.remoteAddress = remoteAddress;
		this.remotePort = remotePort;
        this.localRtpPort = localRtpPort;

        rtcpSession = new RtcpSession(true, 16000);
    }

    /**
     * Open the output stream
     *
     * @throws Exception
     */
    public void open() throws Exception {

        if (localRtpPort != -1) {
            // Create the RTP receiver
            rtpReceiver = new RtpPacketReceiver(localRtpPort, rtcpSession);
            // Create the RTCP receiver
            rtcpReceiver = new RtcpPacketReceiver(localRtpPort + 1, rtcpSession);

            if (RtpConfig.SYMETRIC_RTP) {
                // Create the RTP transmitter
                rtpTransmitter = new RtpPacketTransmitter(remoteAddress, remotePort, rtcpSession,
                        rtpReceiver.getConnection());
                // Create the RTCP transmitter
                rtcpTransmitter = new RtcpPacketTransmitter(remoteAddress, remotePort + 1,
                        rtcpSession, rtcpReceiver.getConnection());
            } else {
                // Create the RTP transmitter
                rtpTransmitter = new RtpPacketTransmitter(remoteAddress, remotePort, rtcpSession);
                // Create the RTCP transmitter
                rtcpTransmitter = new RtcpPacketTransmitter(remoteAddress, remotePort + 1,
                        rtcpSession);
            }
        } else {
            // Create the RTP transmitter
            rtpTransmitter = new RtpPacketTransmitter(remoteAddress, remotePort, rtcpSession);
            // Create the RTCP transmitter
            rtcpTransmitter = new RtcpPacketTransmitter(remoteAddress, remotePort + 1, rtcpSession);
        }
    }

    /**
     * Close the output stream
     */
    public void close() {
		try {
			// Close the RTP transmitter
            if (rtpTransmitter != null)
				rtpTransmitter.close();

            // Close the RTCP transmitter
            if (rtcpTransmitter != null)
                rtcpTransmitter.close();

            // Close the RTP receiver
            if (rtpReceiver != null)
                rtpReceiver.close();

            // Close the RTCP receiver
            if (rtcpReceiver != null)
                rtcpReceiver.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Write to the stream without blocking
     *
     * @param buffer Input buffer
     * @throws IOException
     */
    public void write(Buffer buffer) throws IOException {
		rtpTransmitter.sendRtpPacket(buffer);
    }
}
