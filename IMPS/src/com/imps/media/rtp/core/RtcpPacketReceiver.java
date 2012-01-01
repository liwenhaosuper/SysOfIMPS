
package com.imps.media.rtp.core;





import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Vector;

import com.imps.media.rtp.util.Packet;

/**
 * RTCP packet receiver
 *
 * @author liwenhaosuper
 */
public class RtcpPacketReceiver extends Thread {
	/**
	 * Datagram connection
	 */
    public DatagramConnection datagramConnection = null;

	/**
	 * Statistics
	 */
	private RtcpStatisticsReceiver stats =  new RtcpStatisticsReceiver();

	/**
	 * RTCP event listeners
	 */
	private Vector<RtcpEventListener> listeners = new Vector<RtcpEventListener>();

    /**
     * RTCP Session
     */
    private RtcpSession rtcpSession = null;


    /**
     * Constructor
     *
     * @param port Listenning port
     * @param rtcpSession the RTCP session
     * @throws IOException
     */
    public RtcpPacketReceiver(int port, RtcpSession rtcpSession) throws IOException {
        super();

        this.rtcpSession = rtcpSession;

		// Create the UDP server
		datagramConnection = NetworkFactory.getFactory().createDatagramConnection();
		datagramConnection.open(port);
        System.out.println("RtcpPakcetReceiver:conn opened and port is :"+port);
		// Start background processing
		start();
	}

	/**
     * Close the receiver
     *
     * @throws IOException
     */
	public void close() throws IOException {
		// Interrup the current thread processing
		try {
			interrupt();
		} catch(Exception e) {}

		// Close the datagram connection
		if (datagramConnection != null) {
			datagramConnection.close();
			datagramConnection = null;
		}
	}

	/**
	 * Background processing
	 */
	public void run() {
		try {
            while (datagramConnection != null) {
				// Wait a packet
				byte[] data = datagramConnection.receive();

		        // Create a packet object
				Packet packet = new Packet();
				packet.data = data;
				packet.length = data.length;
				packet.offset = 0;
				packet.receivedAt = System.currentTimeMillis();

		        // Process the received packet
				handlePacket(packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Handle the received packet
     *
     * @param packet Packet
     * @return RTCP packet
     */
	public RtcpPacket handlePacket(Packet p) {
		// Update statistics
		stats.numRtcpPkts++;
		stats.numRtcpBytes += p.length;

		// Parse the RTCP packet
		RtcpPacket result;
		try {
			result = parseRtcpPacket(p);
		} catch (Exception e) {
			stats.numBadRtcpPkts++;
			return null;
		}
		return result;
	}

    /**
     * Parse the RTCP packet
     *
     * @param packet RTCP packet not yet parsed
     * @return RTCP packet
     */
	public RtcpPacket parseRtcpPacket(Packet packet) {
		RtcpCompoundPacket compoundPacket = new RtcpCompoundPacket(packet);
		Vector<RtcpPacket> subpackets = new Vector<RtcpPacket>();
		DataInputStream in = new DataInputStream(
				new ByteArrayInputStream(compoundPacket.data,
						compoundPacket.offset,
						compoundPacket.length));
		try {
            rtcpSession.updateavgrtcpsize(compoundPacket.length);
			int length = 0;
			for (int offset = 0; offset < compoundPacket.length; offset += length) {
				// Read first byte
				int firstbyte = in.readUnsignedByte();
				if ((firstbyte & 0xc0) != 128) {
			     
					return null;
				}

				// Read type of subpacket
				int type = in.readUnsignedByte();

				// Read length of subpacket
				length = in.readUnsignedShort();
				length = length + 1 << 2;
				int padlen = 0;
				if (offset + length > compoundPacket.length) {
					return null;
				}
				if (offset + length == compoundPacket.length) {
					if ((firstbyte & 0x20) != 0) {
						padlen = compoundPacket.data[compoundPacket.offset + compoundPacket.length - 1] & 0xff;
						if (padlen == 0) {
							return null;
						}
					}
				} else
				if ((firstbyte & 0x20) != 0) {
					return null;
				}
				int inlength = length - padlen;
				firstbyte &= 0x1f;

				// Parse subpacket
				RtcpPacket subpacket;
				switch (type) {
					// RTCP SR event
                    case RtcpPacket.RTCP_SR:
						stats.numSrPkts++;
						if (inlength != 28 + 24 * firstbyte) {
							stats.numMalformedRtcpPkts++;
							return null;
						}
						RtcpSenderReportPacket srp = new RtcpSenderReportPacket(compoundPacket);
						subpacket = srp;
						srp.ssrc = in.readInt();
						srp.ntptimestampmsw = (long) in.readInt() & 0xffffffffL;
						srp.ntptimestamplsw = (long) in.readInt() & 0xffffffffL;
						srp.rtptimestamp = (long) in.readInt() & 0xffffffffL;
						srp.packetcount = (long) in.readInt() & 0xffffffffL;
						srp.octetcount = (long) in.readInt() & 0xffffffffL;
						srp.reports = new RtcpReport[firstbyte];

                        RtpSource sourceSR = rtcpSession.getMySource();
                        if (sourceSR != null)
                            sourceSR.timeOfLastRTCPArrival = rtcpSession.currentTime();

						for (int i = 0; i < srp.reports.length; i++) {
							RtcpReport report = new RtcpReport();
							srp.reports[i] = report;
							report.ssrc = in.readInt();
							long val = in.readInt();
							val &= 0xffffffffL;
							report.fractionlost = (int) (val >> 24);
							report.packetslost = (int) (val & 0xffffffL);
							report.lastseq = (long) in.readInt() & 0xffffffffL;
							report.jitter = in.readInt();
							report.lsr = (long) in.readInt() & 0xffffffffL;
							report.dlsr = (long) in.readInt() & 0xffffffffL;
						}

						// Notify event listeners
						notifyRtcpListeners(new RtcpSenderReportEvent(srp));
						break;

					// RTCP RR event
                    case RtcpPacket.RTCP_RR:
						if (inlength != 8 + 24 * firstbyte) {
							stats.numMalformedRtcpPkts++;
							return null;
						}
						RtcpReceiverReportPacket rrp = new RtcpReceiverReportPacket(compoundPacket);
						subpacket = rrp;
						rrp.ssrc = in.readInt();
						rrp.reports = new RtcpReport[firstbyte];

                        RtpSource sourceRR = rtcpSession.getMySource();
                        if (sourceRR != null)
                            sourceRR.timeOfLastRTCPArrival = rtcpSession.currentTime();

						for (int i = 0; i < rrp.reports.length; i++) {
							RtcpReport report = new RtcpReport();
							rrp.reports[i] = report;
							report.ssrc = in.readInt();
							long val = in.readInt();
							val &= 0xffffffffL;
							report.fractionlost = (int) (val >> 24);
							report.packetslost = (int) (val & 0xffffffL);
							report.lastseq = (long) in.readInt() & 0xffffffffL;
							report.jitter = in.readInt();
							report.lsr = (long) in.readInt() & 0xffffffffL;
							report.dlsr = (long) in.readInt() & 0xffffffffL;
						}

						// Notify event listeners
						notifyRtcpListeners(new RtcpReceiverReportEvent(rrp));
						break;

					// RTCP SDES event
                    case RtcpPacket.RTCP_SDES:
						RtcpSdesPacket sdesp = new RtcpSdesPacket(compoundPacket);
						subpacket = sdesp;
						sdesp.sdes = new RtcpSdesBlock[firstbyte];
						int sdesoff = 4;
						for (int i = 0; i < sdesp.sdes.length; i++) {
							RtcpSdesBlock chunk = new RtcpSdesBlock();
							sdesp.sdes[i] = chunk;
							chunk.ssrc = in.readInt();
							sdesoff += 5;
							Vector<RtcpSdesItem> items = new Vector<RtcpSdesItem>();
							boolean gotcname = false;
							int j;
							while ((j = in.readUnsignedByte()) != 0) {
								if (j < 1 || j > 8) {
									stats.numMalformedRtcpPkts++;
									
									return null;
								}
								if (j == 1) {
									gotcname = true;
								}
								RtcpSdesItem item = new RtcpSdesItem();
								items.addElement(item);
								item.type = j;
								int sdeslen = in.readUnsignedByte();
								item.data = new byte[sdeslen];
								in.readFully(item.data);
								sdesoff += 2 + sdeslen;
							}
							if (!gotcname) {
								stats.numMalformedRtcpPkts++;
								
								return null;
							}
							chunk.items = new RtcpSdesItem[items.size()];
							items.copyInto(chunk.items);
							if ((sdesoff & 3) != 0) {
								in.skip(4 - (sdesoff & 3));
								sdesoff = sdesoff + 3 & -4;
							}
						}

						if (inlength != sdesoff) {
							stats.numMalformedRtcpPkts++;
							
							return null;
						}

						// Notify event listeners
                        notifyRtcpListeners(new RtcpSdesEvent(sdesp));
						break;

					// RTCP BYE event
                    case RtcpPacket.RTCP_BYE:
						RtcpByePacket byep = new RtcpByePacket(compoundPacket);
						subpacket = byep;
						byep.ssrc = new int[firstbyte];
						for (int i = 0; i < byep.ssrc.length; i++) {
							byep.ssrc[i] = in.readInt();
						}

						int reasonlen;
						if (inlength > 4 + 4 * firstbyte) {
							reasonlen = in.readUnsignedByte();
							byep.reason = new byte[reasonlen];
							reasonlen++;
						} else {
							reasonlen = 0;
							byep.reason = new byte[0];
						}
						reasonlen = reasonlen + 3 & -4;
						if (inlength != 4 + 4 * firstbyte + reasonlen) {
							stats.numMalformedRtcpPkts++;
						
							return null;
						}
						in.readFully(byep.reason);
						in.skip(reasonlen - byep.reason.length);

						// Notify event listeners
						notifyRtcpListeners(new RtcpByeEvent(byep));
						break;

					// RTCP APP event
                    case RtcpPacket.RTCP_APP:
						if (inlength < 12) {
							
							return null;
						}
						RtcpAppPacket appp = new RtcpAppPacket(compoundPacket);
						subpacket = appp;
						appp.ssrc = in.readInt();
						appp.name = in.readInt();
						appp.subtype = firstbyte;
						appp.data = new byte[inlength - 12];
						in.readFully(appp.data);
						in.skip(inlength - 12 - appp.data.length);

						// Notify event listeners
						notifyRtcpListeners(new RtcpApplicationEvent(appp));
						break;

					// RTCP unknown event
					default:
						stats.numUnknownTypes++;
					
						return null;
				}
				subpacket.offset = offset;
				subpacket.length = length;
				subpackets.addElement(subpacket);
				in.skipBytes(padlen);
			}

		} catch (Exception e) {
			return null;
		}
		compoundPacket.packets = new RtcpPacket[subpackets.size()];
		subpackets.copyInto(compoundPacket.packets);
		return compoundPacket;
	}

    /**
     * Add a RTCP event listener
     *
     * @param listener Listener
     */
	public void addRtcpListener(RtcpEventListener listener) {
		listeners.addElement(listener);
	}

	/**
     * Remove a RTCP event listener
     *
     * @param listener Listener
     */
	public void removeRtcpListener(RtcpEventListener listener) {
		listeners.removeElement(listener);
	}

	/**
     * Notify RTCP event listeners
     *
     * @param event RTCP event
     */
	public void notifyRtcpListeners(RtcpEvent event) {
		for(int i=0; i < listeners.size(); i++) {
			RtcpEventListener listener = (RtcpEventListener)listeners.elementAt(i);
			listener.receiveRtcpEvent(event);
		}
	}

    /**
     * Returns the statistics of RTCP reception
     *
     * @return Statistics
     */
	public RtcpStatisticsReceiver getRtcpReceptionStats() {
		return stats;
    }

    /**
     * Returns the DatagramConnection of RTCP
     *
     * @return DatagramConnection
     */
    public DatagramConnection getConnection() {
        return datagramConnection;
    }
}
