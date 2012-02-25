/***********************************************************************
 * Module:  MediaRtpReceiver.java
 * Author:  liwenhaosuper
 * Purpose: Defines the Class MediaRtpReceiver
 ***********************************************************************/
package com.imps.media.rtp;
import com.imps.media.rtp.core.RtpConfig;
import com.imps.media.rtp.format.Format;

/**
 * Media RTP receiver
 */
public class MediaRtpReceiver {
    /**
     * Media processor
     */
    private Processor processor = null;

	/**
	 * Local port number (RTP listening port)
	 */
	private int localPort;


    /**
     * Constructor
     *
     * @param localPort Local port number
     */
	public MediaRtpReceiver(int localPort) {
		this.localPort = localPort;

        // Activate symmetric RTP configuration
		RtpConfig.SYMETRIC_RTP = true;
	}

    /**
     * Prepare the RTP session
     *
     * @param renderer Media renderer
     * @param format Media format
     * @throws RtpException
     */
    public void prepareSession(MediaOutput renderer, Format format)
            throws java.lang.Exception {
    	try {
			// Create the input stream
    		//System.out.println("MediaRtpReceiver:localPort is "+localPort+" and rtcp port is"+(localPort+1)+"...");
            RtpInputStream inputStream = new RtpInputStream(localPort, format);
    		inputStream.open();
    		//System.out.println("MediaRtpReceiver:inputstream is opened...");
            // Create the output stream
        	MediaRendererStream outputStream = new MediaRendererStream(renderer);
    		outputStream.open();
    		//System.out.println("MediaRtpReceiver:outputstream is opened...");
        	// Create the codec chain
        	Codec[] codecChain = MediaRegistry.generateDecodingCodecChain(format.getCodec());

            // Create the media processor
    		processor = new Processor(inputStream, outputStream, codecChain);
    		//System.out.println("MediaRtpReceiver:processor is created...");
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
	}
}
