
package com.imps.media.rtp.core;

/**
 * RTCP utils.
 *
 * @author liwenhaosuper
 */
public class RtcpPacketUtils {

    /**
     * Convert 64 bit long to n bytes.
     *
     * @param data data
     * @param n desired number of bytes to convert the long to.
     * @return buffer
     */
    public static byte[] longToBytes(long data, int n) {
        byte buf[] = new byte[n];
         for (int i = n - 1; i >= 0; i--) {
            buf[i] = (byte)data;
            data = data >> 8;
        }
        return buf;
    }

    /**
     * Append two byte arrays.
     *
     * @param pck1 first packet.
     * @param pck2 second packet.
     * @return concatenated packet.
     */
    public static byte[] append(byte[] pck1, byte[] pck2) {
        byte packet[] = new byte[pck1.length + pck2.length];
        for (int i = 0; i < pck1.length; i++)
            packet[i] = pck1[i];
        for (int i = 0; i < pck2.length; i++)
            packet[i + pck1.length] = pck2[i];
        return packet;
    }
};
