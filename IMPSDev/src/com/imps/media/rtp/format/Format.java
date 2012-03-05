
package com.imps.media.rtp.format;

/**
 * Abstract format
 * 
 * @author liwenhaosuper
 */
public abstract class Format {
	/**
	 * Unknown payload
	 */
    public static final int UNKNOWN_PAYLOAD = -1;

    /**
     * Codec
     */
    private String codec;

	/**
     * Payload type
     */
    private int payload;

    /**
     * Constructor
     * 
     * @param codec Codec
     * @param payload Payload type
     */
    public Format(String codec, int payload) {
    	this.codec = codec;	
    	this.payload = payload;
    }

    /**
     * Get the codec name
     *
     * @return Name 
     */
    public String getCodec() {
    	return codec;
    }

    /**
     * Get the type of payload
     * 
     * @return Payload type
     */
    public int getPayload() {
    	return payload;
    }
}
