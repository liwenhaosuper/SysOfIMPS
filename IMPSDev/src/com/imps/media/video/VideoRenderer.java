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

package com.imps.media.video;

import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.SystemClock;
import android.util.Log;

import com.imps.media.rtp.IMediaEventListener;
import com.imps.media.rtp.IMediaPlayer;
import com.imps.media.rtp.MediaCodec;
import com.imps.media.rtp.MediaOutput;
import com.imps.media.rtp.MediaRegistry;
import com.imps.media.rtp.MediaRtpReceiver;
import com.imps.media.rtp.MediaSample;
import com.imps.media.rtp.core.DatagramConnection;
import com.imps.media.rtp.format.VideoFormat;
import com.imps.media.video.codec.H263Config;
import com.imps.media.video.codec.H264Config;
import com.imps.media.video.codec.NativeH263Decoder;
import com.imps.media.video.codec.NativeH264Decoder;

/**
 * Video RTP renderer. Supports only H.263 and H264 QCIF formats.
 *
 * @author liwenhaosuper
 */
public class VideoRenderer extends IMediaPlayer {

    /**
     * List of supported video codecs
     */
    public static MediaCodec[] supportedMediaCodecs = {
            new VideoCodec(H264Config.CODEC_NAME, H264Config.CLOCK_RATE, H264Config.CODEC_PARAMS,
                    H264Config.FRAME_RATE, H264Config.BIT_RATE, H264Config.VIDEO_WIDTH,
                    H264Config.VIDEO_HEIGHT).getMediaCodec(),
            new VideoCodec(H263Config.CODEC_NAME, H263Config.CLOCK_RATE, H263Config.CODEC_PARAMS,
                    H263Config.FRAME_RATE, H263Config.BIT_RATE, H263Config.VIDEO_WIDTH,
                    H263Config.VIDEO_HEIGHT).getMediaCodec()
    };

    /**
     * Selected video codec
     */
    private VideoCodec selectedVideoCodec = null;

    /**
     * Video format
     */
    private VideoFormat videoFormat;

    /**
     * Local RTP port
     */
    private int localRtpPort = 1300;

    /**
     * RTP receiver session
     */
    private MediaRtpReceiver rtpReceiver = null;

    /**
     * RTP dummy packet generator
     */
  //  private DummyPacketGenerator rtpDummySender = null;

    /**
     * RTP media output
     */
    private MediaRtpOutput rtpOutput = null;

    /**
     * Is player opened
     */
    private boolean opened = false;

    /**
     * Is player started
     */
    private boolean started = false;

    /**
     * Video start time
     */
    private long videoStartTime = 0L;

    /**
     * Video surface
     */
    private VideoSurfaceView surface = null;


    /**
     * Temporary connection to reserve the port
     */
    private DatagramConnection temporaryConnection = null;

    /**
     * Constructor.
     */
    public VideoRenderer() {
        // Set the local RTP port
        localRtpPort = 1300;//NetworkRessourceManager.generateLocalRtpPort();
        reservePort(localRtpPort);
       // System.out.println("VideoRenderer:localRtpPort reserve is "+localRtpPort);
    }

    /**
     * Constructor Force a video codec.
     *
     * @param codec Video codec
     */
    public VideoRenderer(VideoCodec codec) {
        // Set the local RTP port
       localRtpPort = 1300;//NetworkRessourceManager.generateLocalRtpPort();
        reservePort(localRtpPort);

        // Set the media codec
        setMediaCodec(codec.getMediaCodec());
    }

    /**
     * Constructor Force a video codec.
     *
     * @param codec Video codec name
     */
    public VideoRenderer(String codec) {
        // Set the local RTP port
        localRtpPort = 1300;//NetworkRessourceManager.generateLocalRtpPort();
        reservePort(localRtpPort);
        // Set the media codec
        for (int i = 0; i < supportedMediaCodecs.length; i++) {
            if (codec.toLowerCase().contains(supportedMediaCodecs[i].getCodecName().toLowerCase())) {
                setMediaCodec(supportedMediaCodecs[i]);
                break;
            }
        }
    }

    /**
     * Set the surface to render video
     *
     * @param surface Video surface
     */
    public void setVideoSurface(VideoSurfaceView surface) {
        this.surface = surface;
    }

    /**
     * Return the video start time
     *
     * @return Milliseconds
     */
    public long getVideoStartTime() {
        return videoStartTime;
    }

    /**
     * Returns the local RTP port
     *
     * @return Port
     */
    public int getLocalRtpPort() {
        return localRtpPort;
    }

    /**
     * Reserve a port.
     *
     * @param port the port to reserve
     */
    private void reservePort(int port) {
        /*if (temporaryConnection == null) {
            try {
                temporaryConnection = NetworkFactory.getFactory().createDatagramConnection();
                temporaryConnection.open(port);
            } catch (IOException e) {
                temporaryConnection = null;
            }
        }*/
    }

    /**
     * Release the reserved port.
     */
    private void releasePort() {
        /*if (temporaryConnection != null) {
            try {
                temporaryConnection.close();
            } catch (IOException e) {
                temporaryConnection = null;
            }
        }*/
    }

    /**
     * Is player opened
     *
     * @return Boolean
     */
    public boolean isOpened() {
        return opened;
    }

    /**
     * Is player started
     *
     * @return Boolean
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Open the renderer
     *
     * @param remoteHost Remote host
     * @param remotePort Remote port
     */
    public void open(String remoteHost, int remotePort) {
        if (opened) {
            // Already opened
            return;
        }

        // Check video codec
        if (selectedVideoCodec == null) {
            notifyPlayerEventError("Video Codec not selected");
            return;
        }

        try {
            // Init the video decoder
            int result;
            if (selectedVideoCodec.getCodecName().equalsIgnoreCase(H264Config.CODEC_NAME)) {
                result = NativeH264Decoder.InitDecoder();
            } else { // default H263
                result = NativeH263Decoder.InitDecoder(selectedVideoCodec.getWidth(),
                        selectedVideoCodec.getHeight());
            }
            if (result == 0) {
                notifyPlayerEventError("Decoder init failed with error code " + result);
                return;
            }
        } catch (UnsatisfiedLinkError e) {
        	e.printStackTrace();
            notifyPlayerEventError(e.getMessage());
            return;
        }

        try {
            // Init the RTP layer
            releasePort();
            rtpReceiver = new MediaRtpReceiver(localRtpPort);
          //  rtpDummySender = new DummyPacketGenerator();
            rtpOutput = new MediaRtpOutput();
            rtpOutput.open();
            rtpReceiver.prepareSession(rtpOutput, videoFormat);
          //  rtpDummySender.prepareSession(remoteHost, remotePort);
        } catch (Exception e) {
            notifyPlayerEventError(e.getMessage());
            e.printStackTrace();
            return;
        }

        // Player is opened
        opened = true;
        notifyPlayerEventOpened();
    }

    /**
     * Close the renderer
     */
    public void close() {
        if (!opened) {
            // Already closed
            return;
        }

        // Close the RTP layer
        rtpOutput.close();
        rtpReceiver.stopSession();
       // rtpDummySender.stopSession();

        try {
            // Close the video decoder
            if (selectedVideoCodec.getCodecName().equalsIgnoreCase(H264Config.CODEC_NAME)) {
                NativeH264Decoder.DeinitDecoder();
            } else { // default H263
                NativeH263Decoder.DeinitDecoder();
            }
        } catch (UnsatisfiedLinkError e) {
               e.printStackTrace();
        }

        // Player is closed
        opened = false;
        notifyPlayerEventClosed();
    }

    /**
     * Start the player
     */
    public void start() {
        if (!opened) {
            // Player not opened
            return;
        }

        if (started) {
            // Already started
            return;
        }

        // Start RTP layer
        rtpReceiver.startSession();
       // rtpDummySender.startSession();

        // Renderer is started
        videoStartTime = SystemClock.uptimeMillis();
        started = true;
        notifyPlayerEventStarted();
    }

    /**
     * Stop the renderer
     */
    public void stop() {
        if (!started) {
            return;
        }

        // Stop RTP layer
        if (rtpReceiver != null) {
            rtpReceiver.stopSession();
        }
/*        if (rtpDummySender != null) {
            rtpDummySender.stopSession();
        }*/
        if (rtpOutput != null) {
            rtpOutput.close();
        }

        // Force black screen
    	surface.clearImage();

        // Stop decoder
        // TODO

        // Renderer is stopped
        started = false;
        videoStartTime = 0L;
        notifyPlayerEventStopped();
    }

   
    /**
     * Get supported media codecs
     *
     * @return media Codecs list
     */
    public MediaCodec[] getSupportedMediaCodecs() {
        return supportedMediaCodecs;
    }

    /**
     * Get media codec
     *
     * @return Media codec
     */
    public MediaCodec getMediaCodec() {
        if (selectedVideoCodec == null)
            return null;
        else
            return selectedVideoCodec.getMediaCodec();
    }

    /**
     * Set media codec
     *
     * @param mediaCodec Media codec
     */
    public void setMediaCodec(MediaCodec mediaCodec) {
        if (VideoCodec.checkVideoCodec(supportedMediaCodecs, new VideoCodec(mediaCodec))) {
            selectedVideoCodec = new VideoCodec(mediaCodec);
            videoFormat = (VideoFormat) MediaRegistry.generateFormat(mediaCodec.getCodecName());
        } else {
            notifyPlayerEventError("Codec not supported");
        }
    }

    /**
     * Notify player event started
     */
    public void notifyPlayerEventStarted() {
    	if(iMediaEventListener==null)
    		return;
        Iterator<IMediaEventListener> ite = iMediaEventListener.iterator();
        while (ite.hasNext()) {
            ((IMediaEventListener)ite.next()).onMediaStarted();
        }
    }

    /**
     * Notify player event stopped
     */
    public void notifyPlayerEventStopped() {
    	if(iMediaEventListener==null)
    		return;
        Iterator<IMediaEventListener> ite = iMediaEventListener.iterator();
        while (ite.hasNext()) {
            ((IMediaEventListener)ite.next()).onMediaStopped();
        }
    }

    /**
     * Notify player event opened
     */
    public void notifyPlayerEventOpened() {
    	if(iMediaEventListener==null)
    		return;
        Iterator<IMediaEventListener> ite = iMediaEventListener.iterator();
        while (ite.hasNext()) {
            ((IMediaEventListener)ite.next()).onMediaOpened();
        }
    }

    /**
     * Notify player event closed
     */
    public void notifyPlayerEventClosed() {
    	if(iMediaEventListener==null)
    		return;
        Iterator<IMediaEventListener> ite = iMediaEventListener.iterator();
        while (ite.hasNext()) {
            ((IMediaEventListener)ite.next()).onMediaClosed();
        }
    }

    /**
     * Notify player event error
     */
    public void notifyPlayerEventError(String error) {
    	Log.d("VideoRenderer", "VideoRenderer error:"+error);
    	if(iMediaEventListener==null)
    		return;
        Iterator<IMediaEventListener> ite = iMediaEventListener.iterator();
        while (ite.hasNext()) {
            ((IMediaEventListener)ite.next()).onMediaError(error);
        }
    }

    /**
     * Media RTP output
     */
    private class MediaRtpOutput implements MediaOutput {
        /**
         * Video frame
         */
        private int decodedFrame[];

        /**
         * Bitmap frame
         */
        private Bitmap rgbFrame;

        /**
         * Constructor
         */
        public MediaRtpOutput() {
            decodedFrame = new int[selectedVideoCodec.getWidth() * selectedVideoCodec.getHeight()];
            rgbFrame = Bitmap.createBitmap(selectedVideoCodec.getWidth(),
                    selectedVideoCodec.getHeight(),
                    Bitmap.Config.RGB_565);
          //  Log.d("VideoRenderer", "VideoRenderer:codec is :"+selectedVideoCodec.getCodecName());
        }

        /**
         * Open the renderer
         */
        public void open() {
            // Nothing to do
        }

        /**
         * Close the renderer
         */
        public void close() {
        }

        /**
         * Write a media sample
         *
         * @param sample Sample
         */
        public void writeSample(MediaSample sample) {
			if(sample==null){
				System.out.println("videorenderer:sample is null");
				return;
			}
			Log.d("VideoRenderer", "renderering the surface...");
            if (selectedVideoCodec.getCodecName().equalsIgnoreCase(H264Config.CODEC_NAME)) {
                if (NativeH264Decoder.DecodeAndConvert(sample.getData(), decodedFrame) == 1) {
                    rgbFrame.setPixels(decodedFrame, 0, selectedVideoCodec.getWidth(), 0, 0,
                            selectedVideoCodec.getWidth(), selectedVideoCodec.getHeight());
                    Matrix matrix= new Matrix();
        	        matrix.postRotate(90);
                    Bitmap resFrame = Bitmap.createBitmap(rgbFrame,0,0,selectedVideoCodec.getWidth(),
                    		selectedVideoCodec.getHeight(),matrix,true);
                    if (surface != null) {
                        surface.setImage(resFrame);
                    }
                }
            } else {// default H263
                if (NativeH263Decoder.DecodeAndConvert(sample.getData(), decodedFrame,
                        sample.getTimeStamp()) == 1) {
                    rgbFrame.setPixels(decodedFrame, 0, selectedVideoCodec.getWidth(), 0, 0,
                            selectedVideoCodec.getWidth(), selectedVideoCodec.getHeight());
                    Matrix matrix= new Matrix();
        	        matrix.postRotate(90);
                    Bitmap resFrame = Bitmap.createBitmap(rgbFrame,0,0,selectedVideoCodec.getWidth(),
                    		selectedVideoCodec.getHeight(),matrix,true);
                    if (surface != null) {
                        surface.setImage(resFrame);
                    }
                }                     
        }
    }
    }
     public void decodeYUV420SP(int[] rgbBuf, byte[] yuv420sp, int width, int height) {
    	final int frameSize = width * height;
		if (rgbBuf == null)
			throw new NullPointerException("buffer 'rgbBuf' is null");
		if (rgbBuf.length < frameSize * 3)
			throw new IllegalArgumentException("buffer 'rgbBuf' size "
					+ rgbBuf.length + " < minimum " + frameSize * 3);

		if (yuv420sp == null)
			throw new NullPointerException("buffer 'yuv420sp' is null");

		if (yuv420sp.length < frameSize * 3 / 2)
			throw new IllegalArgumentException("buffer 'yuv420sp' size " + yuv420sp.length
					+ " < minimum " + frameSize * 3 / 2);
    	
    	int i = 0, y = 0;
    	int uvp = 0, u = 0, v = 0;
    	int y1192 = 0, r = 0, g = 0, b = 0;
    	
    	for (int j = 0, yp = 0; j < height; j++) {
    		uvp = frameSize + (j >> 1) * width;
    		u = 0;
    		v = 0;
    		for (i = 0; i < width; i++, yp++) {
    			y = (0xff & ((int) yuv420sp[yp])) - 16;
    			if (y < 0) y = 0;
    			if ((i & 1) == 0) {
    				v = (0xff & yuv420sp[uvp++]) - 128;
    				u = (0xff & yuv420sp[uvp++]) - 128;
    			}
    			
    			y1192 = 1192 * y;
    			r = (y1192 + 1634 * v);
    			g = (y1192 - 833 * v - 400 * u);
    			b = (y1192 + 2066 * u);
    			
    			if (r < 0) r = 0; else if (r > 262143) r = 262143;
    			if (g < 0) g = 0; else if (g > 262143) g = 262143;
    			if (b < 0) b = 0; else if (b > 262143) b = 262143;
    			
    			rgbBuf[yp * 3] = (byte)(r >> 10);
    			rgbBuf[yp * 3 + 1] = (byte)(g >> 10);
    			rgbBuf[yp * 3 + 2] = (byte)(b >> 10);
    			
    			rgbBuf[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
    		}
    	}
    }
}
