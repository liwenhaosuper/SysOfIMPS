
package com.imps.media.rtp;

import com.imps.media.rtp.util.Buffer;

/**
 * Media renderer stream 
 * 
 * @author liwenhaosuper
 */
public class MediaRendererStream implements IProcessorOutputStream {
	/**
     * Media renderer
     */
	private MediaOutput renderer;

	/**
	 * Constructor
	 * 
     * @param renderer Media renderer
	 */
	public MediaRendererStream(MediaOutput renderer) {
		this.renderer = renderer;
	}

	/**
	 * Open the output stream
	 * 
     * @throws Exception
	 */	
    public void open() throws Exception {
    	try {
	    	renderer.open();

		} catch(Exception e) {
			e.printStackTrace(); 
		}
    }

    /**
     * Close the output stream
     */
    public void close() {
		renderer.close();
		  	
    }
        
    /**
     * Write to the stream without blocking
     * 
     * @param buffer Input buffer 
     * @throws Exception
     */
    public void write(Buffer buffer) throws Exception {
    	MediaSample sample = new MediaSample((byte[])buffer.getData(), buffer.getTimeStamp());
    	renderer.writeSample(sample);
    }
}
