/***********************************************************************
 * Module:  Processor.java
 * Author:  liwenhaosuper
 * Purpose: Defines the Class Processor
 ***********************************************************************/
package com.imps.media.rtp;

import java.util.logging.Logger;

import com.imps.media.rtp.util.Buffer;

public class Processor extends Thread {
	/**
	 * Processor input stream
	 */
	private IProcessorInputStream inputStream;

	/**
	 * Processor output stream
	 */
	private IProcessorOutputStream outputStream;

	/**
	 * Codec chain
	 */
	private CodecChain codecChain;

	/**
	 * Processor status flag
	 */
	private boolean interrupted = false;

    /**
     * bigger Sequence Number
     */
    private long bigSeqNum = 0;
    /**
     * Constructor
     *
     * @param inputStream Input stream
     * @param outputStream Output stream
     * @param codecs List of codecs
     */
	public Processor(IProcessorInputStream inputStream, IProcessorOutputStream outputStream, Codec[] codecs) {
        super();

		this.inputStream = inputStream;
        this.outputStream = outputStream;

		// Create the codec chain
		codecChain = new CodecChain(codecs, outputStream);

	}

	/**
	 * Start processing
	 */
	public void startProcessing() {
		interrupted = false;
        bigSeqNum = 0;
        start();
	}

	/**
	 * Stop processing
	 */
	public void stopProcessing() {
		interrupted = true;

		// Close streams
		outputStream.close();
		inputStream.close();
	}

	/**
	 * Background processing
	 */
	public void run() {
		try {

			// Start processing
			while (!interrupted) {
				// Read data from the input stream
				Buffer inBuffer = inputStream.read();
				if (inBuffer == null) {
					interrupted = true;
					break;
				}

                // Drop the old packet
                long seqNum = inBuffer.getSequenceNumber();
                if (seqNum + 3 > bigSeqNum) {
                    if (seqNum > bigSeqNum) {
                        bigSeqNum = seqNum;
                    }

                    // Codec chain processing
                    int result = codecChain.process(inBuffer);
                    if ((result != Codec.BUFFER_PROCESSED_OK)
                            && (result != Codec.OUTPUT_BUFFER_NOT_FILLED)) {
                        interrupted = true;
                        break;
                    }
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Returns the input stream
     *
     * @return Stream
     */
	public IProcessorInputStream getInputStream() {
		return inputStream;
	}

    /**
     * Returns the output stream
     *
     * @return Stream
     */
	public IProcessorOutputStream getOutputStream() {
		return outputStream;
	}
}
