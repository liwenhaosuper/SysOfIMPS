/***********************************************************************
 * Module:  IProcessorOutputStream.java
 * Author:  liwenhaosuper
 * Purpose: Defines the Class IProcessorOutputStream
 ***********************************************************************/
package com.imps.media.rtp;

import com.imps.media.rtp.util.Buffer;

/** @pdOid 39084643-80c1-495e-bcbf-250c4373a7e5 */
public interface IProcessorOutputStream {
	/**
	 * Open the output stream
	 * 
     * @throws Exception
	 */	
    public void open() throws Exception;

    /**
     * Close from the output stream
     */
    public void close();
    
    /**
     * Write to the stream without blocking
     * 
     * @param buffer Input buffer
     * @throws Exception
     */
    public void write(Buffer buffer) throws Exception;
}