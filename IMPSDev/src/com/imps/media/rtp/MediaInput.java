
package com.imps.media.rtp;

/**
 * Media input (e.g. camera, microphone)
 * 
 * @author liwenhaosuper
 */
public interface MediaInput {
	/**
	 * Open the player
	 * 
	 * @throws MediaException
	 */
	public void open() throws java.lang.Exception;
	
	/**
	 * Close the player
	 */
	public void close();

	/**
	 * Read a media sample (blocking method)
	 * 
	 * @return Media sample
	 * @throws MediaException
	 */
	public MediaSample readSample() throws java.lang.Exception;
}
