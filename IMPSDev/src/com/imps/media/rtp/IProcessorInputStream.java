/***********************************************************************
 * Module:  IProcessorInputStream.java
 * Author:  liwenhaosuper
 * Purpose: Defines the Class IProcessorInputStream
 ***********************************************************************/
package com.imps.media.rtp;

import com.imps.media.rtp.util.Buffer;


/** @pdOid 8efe3faf-5b13-4106-b322-3e2a67ff8d54 */
public interface  IProcessorInputStream {
	 public void open() throws Exception;

	    /**
	     * Close the input stream
	     */
	    public void close();
	    
	    /**
	     * Read from the input stream without blocking
	     * 
	     * @return Buffer 
	     * @throws Exception
	     */
	    public Buffer read() throws Exception;
}