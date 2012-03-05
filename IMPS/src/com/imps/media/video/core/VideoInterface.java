package com.imps.media.video.core;

import com.yz.net.IoSession;

import android.graphics.Canvas;

public interface VideoInterface {
	static final String LOG_TAG = "video";
	/**
	 * try to establist the connection
	 * @return true if the connection is established
	 */
	boolean connect();
	
	/**
	 * @return true if the connection is established
	 */
	boolean isConnect();
	boolean disConnect();
	/**
	 * Open the camera source for subsequent use via calls to capture().
	 * @return true if the camera source was successfully opened.
	 */
	boolean open();
	/**
	 * Close the camera source. Calling close on a closed CameraSource is
	 * permitted but has no effect. The camera source may be reopened after
	 * being closed.
	 */
	void close();
	/**
	 * The width of the captured image.
	 * 
	 * @return the width of the capture in pixels
	 */
	int getWidth();
	/**
	 * The height of the captured image.
	 * 
	 * @return the height of the capture in pixels
	 */
	int getHeight();
	/**
	 * Attempts to render the current camera view onto the supplied canvas.
	 * The capture will be rendered into the rectangle (0,0,width,height).
	 * Outstanding transformations on the canvas may alter this.
	 * 
	 * @param canvas the canvas to which the captured pixel data will be written
	 * @return true iff a frame was successfully written to the canvas
	 */
	boolean getCapture(Canvas canvas);
	
	boolean setCapture(byte[] data);

}
