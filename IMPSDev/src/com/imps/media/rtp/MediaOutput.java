
package com.imps.media.rtp;


/**
 * Media output (e.g. screen, headset)
 * 
 * @author liwenhaosuper
 */
public interface MediaOutput {
	/**
	 * Open the renderer
	 * 
	 * @throws MediaException
	 */
	public void open() throws java.lang.Exception;
	
	/**
	 * Close the renderer
	 */
	public void close();

	/**
	 * Write a media sample
	 * 
	 * @param sample Media sample
	 * @throws MediaException
	 */
	public void writeSample(MediaSample sample) throws java.lang.Exception;
}
