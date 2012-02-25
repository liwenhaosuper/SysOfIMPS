/***********************************************************************
 * Module:  MediaRtpSender.java
 * Author:  liwenhaosuper
 * Purpose: Defines the Class MediaRtpSender
 ***********************************************************************/
package com.imps.media.rtp;

import com.imps.media.rtp.format.Format;

/** @pdOid 6fffbcff-ecff-4817-9398-f49ea47245c7 */
public class MediaRtpSender {
	/**
	 * Format
	 */
	private Format format;

    /**
     * Media processor
     */
    private Processor processor = null;

    /**
     * MediaCaptureStream
     */
    MediaCaptureStream inputStream = null;

    /**
     * RTP output stream
     */
    private RtpOutputStream outputStream = null;

    /**
     * Local RTP port
     */
    private int localRtpPort;

    /**
     * Constructor
     *
     * @param format Media format
     */
    public MediaRtpSender(Format format, int localRtpPort) {
    	this.format = format;
        this.localRtpPort = localRtpPort;
    }

    /**
     * Prepare the RTP session
     *
     * @param player Media player
     * @param remoteAddress Remote address
     * @param remotePort Remote port
     * @throws RtpException
     */
    public void prepareSession(MediaInput player, String remoteAddress, int remotePort)
            throws java.lang.Exception {
    	try {
    		// Create the input stream
            inputStream = new MediaCaptureStream(format, player);
    		inputStream.open();
    		//System.out.println("MediaRtpSender:inputstream is opened...");
            // Create the output stream
            outputStream = new RtpOutputStream(remoteAddress, remotePort,
                    localRtpPort);
            outputStream.open();
            //System.out.println("MediaRtpSender:outputstream is opened...");


        	// Create the codec chain
        	Codec[] codecChain = MediaRegistry.generateEncodingCodecChain(format.getCodec());

            // Create the media processor
    		processor = new Processor(inputStream, outputStream, codecChain);
    		//System.out.println("MediaRtpSender:processor is created...");
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * Start the RTP session
     */
    public void startSession() {


    	// Start the media processor
		if (processor != null) {
			processor.startProcessing();
			//System.out.println("MediaRtpSender:process session start...");
		}
		else{
			//System.out.println("MediaRtpSender:processor is null");
		}
    }

    /**
     * Stop the RTP session
     */
    public void stopSession() {
    	// Stop the media processor
		if (processor != null) {
			processor.stopProcessing();
		}

        if (outputStream != null)
            outputStream.close();
    }
}