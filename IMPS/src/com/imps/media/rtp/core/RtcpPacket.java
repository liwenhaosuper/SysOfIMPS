
package com.imps.media.rtp.core;

import java.io.DataOutputStream;
import java.io.IOException;

import com.imps.media.rtp.util.Packet;

/**
 * Abstract RCTP packet
 *
 * @author liwenhaosuper
 */
public abstract class RtcpPacket extends Packet {
    /**
     *   Version =2
     */
    public static final byte VERSION = 2;

    /**
    *   Padding =0
    */
    public static final byte PADDING = 0;

    /**
     * RTCP SR
     */
    public static final int RTCP_SR = 200;

    /**
     * RTCP RR
     */
    public static final int RTCP_RR = 201;

    /**
     * RTCP SDES
     */
    public static final int RTCP_SDES = 202;

    /**
     * RTCP BYE
     */
    public static final int RTCP_BYE = 203;

    /**
     * RTCP APP
     */
    public static final int RTCP_APP = 204;

    /**
     * RTCP APP
     */
    public static final int RTCP_COMPOUND = -1;

	public Packet base;

	public int type;

	public RtcpPacket() {
	}

	public RtcpPacket(RtcpPacket rtcppacket) {
		super((Packet)rtcppacket);

		base = rtcppacket.base;
	}

	public RtcpPacket(Packet packet) {
		super(packet);

		base = packet;
	}

	public abstract void assemble(DataOutputStream dataoutputstream) throws IOException;

	public abstract int calcLength();
}
