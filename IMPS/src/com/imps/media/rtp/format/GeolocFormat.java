
package com.imps.media.rtp.format;

/**
 * Geoloc format
 * 
 * @author liwenhaosuper
 */
public class GeolocFormat extends Format {

	/**
	 * Encoding name
	 */
	public static final String ENCODING = "geoloc";

	/**
	 * Payload type
	 */
    public static final int PAYLOAD = 101;

	/**
	 * Constructor
	 */
	public GeolocFormat() {
		super(ENCODING, PAYLOAD);
	}

    /**
     * Constructor
     * 
     * @param codec Codec
     * @param payload Payload type
     */
    public GeolocFormat(String codec, int payload) {
    	super(codec, payload);
    }
}
