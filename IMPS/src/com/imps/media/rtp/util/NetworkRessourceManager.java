
package com.imps.media.rtp.util;

import java.io.IOException;

import com.imps.media.rtp.core.AndroidNetworkFactory;
import com.imps.media.rtp.core.DatagramConnection;
import com.imps.media.rtp.core.NetworkFactory;
import com.imps.media.rtp.core.SocketServerConnection;

/**
 * Network ressource manager
 *
 * @author liwenhaosuper
 */
public class NetworkRessourceManager {

    /**
     * Default RTP port base
     */
    public static int DEFAULT_LOCAL_RTP_PORT_BASE = 1300;


    /**
     * Generate a default free RTP port number
     *
     * @return Local RTP port
     */
    public static synchronized int generateLocalRtpPort() {
    	return generateLocalUdpPort(DEFAULT_LOCAL_RTP_PORT_BASE);
    }

    /**
     * Generate a free UDP port number from a specific port base
     *
     * @param portBase UDP port base
     * @return Local UDP port
     */
    private static int generateLocalUdpPort(int portBase) {
    	int resp = -1;
		int port = portBase;
		while(resp == -1) {
			if (isLocalUdpPortFree(port)) {
				// Free UDP port found
				resp = port;
			} else {
                // +2 needed for RTCP port
                port += 2;
			}
		}
    	return resp;
    }

	/**
     * Test if the given local UDP port is really free (not used by
     * other applications)
     *
     * @param port Port to check
     * @return Boolean
     */
    private static boolean isLocalUdpPortFree(int port) {
    	boolean res = false;
    	try {
    		DatagramConnection conn = AndroidNetworkFactory.getFactory().createDatagramConnection();
    		conn.open(port);
            conn.close();
    		res = true;
    	} catch(IOException e) {
    		res = false;
    	}
    	return res;
    }

    /**
     * Generate a free TCP port number
     *
     * @param portBase TCP port base
     * @return Local TCP port
     */
    private static int generateLocalTcpPort(int portBase) {
    	int resp = -1;
		int port = portBase;
		while(resp == -1) {
			if (isLocalTcpPortFree(port)) {
				// Free TCP port found
				resp = port;
			} else {
				port++;
			}
		}
    	return resp;
    }

	/**
     * Test if the given local TCP port is really free (not used by
     * other applications)
     *
     * @param port Port to check
     * @return Boolean
     */
    private static boolean isLocalTcpPortFree(int port) {
    	boolean res = false;
    	try {
    		SocketServerConnection conn = NetworkFactory.getFactory().createSocketServerConnection();
    		conn.open(port);
            conn.close();
    		res = true;
    	} catch(IOException e) {
    		res = false;
    	}
    	return res;
    }

    /**
     * Is a valid IP address
     *
     * @param ipAddress IP address
     * @return Boolean
     */
    public static boolean isValidIpAddress(String ipAddress) {
    	boolean result = false;
		if ((ipAddress != null) &&
				(!ipAddress.equals("127.0.0.1")) &&
					(!ipAddress.equals("localhost"))) {
			result = true;
		}
        return result;
    }

    /**
     * Convert an IP address to its integer representation
     *
     * @param addr IP address
     * @return Integer
     */
    public static int ipToInt(String addr) {
        String[] addrArray = addr.split("\\.");
        int num = 0;
        for (int i=0; i<addrArray.length; i++) {
            int power = 3-i;
            num += ((Integer.parseInt(addrArray[i])%256 * Math.pow(256,power)));
        }
        return num;
    }
}
