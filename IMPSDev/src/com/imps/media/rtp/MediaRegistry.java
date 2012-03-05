/*******************************************************************************
 * Software Name : RCS IMS Stack
 *
 * Copyright © 2010 France Telecom S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.imps.media.rtp;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.imps.media.rtp.format.AudioFormat;
import com.imps.media.rtp.format.Format;
import com.imps.media.rtp.format.H263VideoFormat;
import com.imps.media.rtp.format.H264VideoFormat;
import com.imps.media.rtp.format.VideoFormat;
import com.imps.media.video.codec.JavaDepacketizer;
import com.imps.media.video.codec.JavaPacketizer;

/**
 * Media registry that handles the supported codecs
 * 
 * @author liwenhaosuper
 */
public class MediaRegistry {

	/**
	 * Supported codecs
	 */
	private static Hashtable<String, Format> SUPPORTED_CODECS = new Hashtable<String, Format>();
	static {
		SUPPORTED_CODECS.put(H263VideoFormat.ENCODING.toLowerCase(), new H263VideoFormat());		
		SUPPORTED_CODECS.put(H264VideoFormat.ENCODING.toLowerCase(), new H264VideoFormat());		
	}
	
	/**
	 * Returns the list of the supported video format
	 * 
	 * @return List of video formats
	 */
	public static Vector<VideoFormat> getSupportedVideoFormats() {
		Vector<VideoFormat> list = new Vector<VideoFormat>();
    	for (Enumeration<Format> e = SUPPORTED_CODECS.elements() ; e.hasMoreElements() ;) {
	         Format fmt = (Format)e.nextElement();
	         if (fmt instanceof VideoFormat) {
		         list.addElement((VideoFormat)fmt);
	         }
	     }
		return list;
	}
	
	/**
	 * Returns the list of the supported audio format
	 * 
	 * @return List of audio formats
	 */
	public static Vector<AudioFormat> getSupportedAudioFormats() {
		Vector<AudioFormat> list = new Vector<AudioFormat>();
    	for (Enumeration<Format> e = SUPPORTED_CODECS.elements() ; e.hasMoreElements() ;) {
	         Format fmt = (Format)e.nextElement();
	         if (fmt instanceof AudioFormat) {
		         list.addElement((AudioFormat)fmt);
	         }
	     }
		return list;
	}

	/**
     * Generate the format associated to the codec name
     * 
     * @param codec Codec name
     * @return Format
     */
    public static Format generateFormat(String codec) {
    	return (Format)SUPPORTED_CODECS.get(codec.toLowerCase());
    }    
    
	/**
     * Is codec supported
     * 
     * @param codec Codec name
     * @return Boolean
     */
    public static boolean isCodecSupported(String codec) {
    	Format format = (Format)SUPPORTED_CODECS.get(codec.toLowerCase());
		return (format != null);
    }    
    
	/**
     * Generate the codec encoding chain
     * 
	 * @param encoding Encoding name
     * @return Codec chain
     */
    public static Codec[] generateEncodingCodecChain(String encoding) {
    	if (encoding.toLowerCase().equalsIgnoreCase(H263VideoFormat.ENCODING)) {
    		// Java H263 packetizer
    		Codec[] chain = {
    			new JavaPacketizer()
    		};
    		return chain;
		} else { 
			// Codec implemented in the native part
			return new Codec[0];
		}
    }

	/**
	 * Generate the decoding codec chain
	 * 
	 * @param encoding Encoding name
	 * @return Codec chain
	 */
	public static Codec[] generateDecodingCodecChain(String encoding) {
    	if (encoding.toLowerCase().equalsIgnoreCase(H263VideoFormat.ENCODING)) {
    		// Java H263 depacketizer
    		Codec[] chain = {
    			new JavaDepacketizer()
    		};
    		return chain;
		} else { 
			// Codec implemented in the native part
			return new Codec[0];
		}
	}
}
