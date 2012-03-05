
package com.imps.media.rtp;

import com.imps.media.rtp.format.Format;
import com.imps.media.rtp.util.Buffer;

/**
 * Media capture stream
 * 
 * @author liwenhaosuper
 */
public class MediaCaptureStream implements IProcessorInputStream {
	/**
     * Media player
     */
	private MediaInput player;

	/**
	 * Media format
	 */
	private Format format;
	
    /**
     * Sequence number
     */
    private long seqNo = 0;

    /**
     * Input buffer
     */
	private Buffer buffer = new Buffer();

    /**
	 * Constructor
	 * 
	 * @param format Input format
     * @param player Media player
	 */
    public MediaCaptureStream(Format format, MediaInput player) {
    	this.format = format;
		this.player = player;
	}
    
    
    /**
	 * Open the input stream
	 * 
     * @throws Exception
	 */	
    public void open() throws Exception {
    	try {
	    	player.open();
			
    	} catch(Exception e) {
			e.printStackTrace();
    	}
	}    	
	
    /**
     * Close the input stream
     */
    public void close() {
		player.close();
		
    }
    
    /**
     * Format of the data provided by the source stream
     * 
     * @return Format
     */
    public Format getFormat() {
    	return format;
    }

    /**
     * Read from the stream
     * 
     * @return Buffer
     * @throws Exception
     */
    public Buffer read() throws Exception {
    	// Read a new sample from the media player
    	MediaSample sample = player.readSample();
    	if (sample == null) {
    		return null;
    	}
    	
    	// Create a buffer
	    buffer.setData(sample.getData());   	
	    buffer.setLength(sample.getLength());
    	buffer.setFormat(format);
    	buffer.setSequenceNumber(seqNo++);
    	buffer.setFlags(Buffer.FLAG_SYSTEM_TIME | Buffer.FLAG_LIVE_DATA);
    	buffer.setTimeStamp(sample.getTimeStamp());
    	return buffer;  
    }    
}
