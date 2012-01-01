/***********************************************************************
 * Module:  LiveVideoPlayer.java
 * Author:  liwenhaosuper
 * Purpose: Defines the Class LiveVideoPlayer
 ***********************************************************************/
package com.imps.media.video;
import java.io.IOException;
import java.util.Iterator;

import android.hardware.Camera;
import android.os.SystemClock;
import android.util.Log;

import com.imps.media.rtp.IMediaEventListener;
import com.imps.media.rtp.IMediaPlayer;
import com.imps.media.rtp.MediaCodec;
import com.imps.media.rtp.MediaInput;
import com.imps.media.rtp.MediaRegistry;
import com.imps.media.rtp.MediaRtpSender;
import com.imps.media.rtp.MediaSample;
import com.imps.media.rtp.core.DatagramConnection;
import com.imps.media.rtp.core.NetworkFactory;
import com.imps.media.rtp.format.VideoFormat;
import com.imps.media.rtp.util.NetworkRessourceManager;
import com.imps.media.video.codec.H263Config;
import com.imps.media.video.codec.H264Config;
import com.imps.media.video.codec.NativeH263Encoder;
import com.imps.media.video.codec.NativeH263EncoderParams;
import com.imps.media.video.codec.NativeH264Encoder;
import com.imps.util.FifoBuffer;

/** @pdOid e2d24bc1-b1b6-4226-8fa0-53f42c1de8b7 */
public class LiveVideoPlayer extends IMediaPlayer implements Camera.PreviewCallback {
	
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
    private int localRtpPort = 1302;
    /**
     * Temporary connection to reserve the port
     */
    private DatagramConnection temporaryConnection = null;
    /**
     * RTP sender session
     */
    private MediaRtpSender rtpSender = null;

    /**
     * RTP media input
     */
    private MediaRtpInput rtpInput = null;
    
    /**
     * Last video frame
     */
    private CameraBuffer frameBuffer = null;

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
     * Constructor
     */
    public LiveVideoPlayer() {
        localRtpPort = 1302;//NetworkRessourceManager.generateLocalRtpPort();
        reservePort(localRtpPort);
    }

    /**
     * Constructor. Force a video codec.
     *
     * @param codec Video codec
     */
    public LiveVideoPlayer(VideoCodec codec) {
        // Set the local RTP port
        localRtpPort =1302;//NetworkRessourceManager.generateLocalRtpPort();
        reservePort(localRtpPort);
        // Set the media codec
        setMediaCodec(codec.getMediaCodec());
    }

    /**
     * Constructor. Force a video codec.
     *
     * @param codec Video codec name
     */
    public LiveVideoPlayer(String codec) {
        // Set the local RTP port
        localRtpPort = 1302;//NetworkRessourceManager.generateLocalRtpPort();
        reservePort(localRtpPort);
        // Set the media codec
        for (int i = 0; i < supportedMediaCodecs.length ; i++) {
            if (codec.toLowerCase().contains(supportedMediaCodecs[i].getCodecName().toLowerCase())) {
                setMediaCodec(supportedMediaCodecs[i]);
                break;
            }
        }
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
     * Return the video start time
     *
     * @return Milliseconds
     */
    public long getVideoStartTime() {
        return videoStartTime;
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
     * Open the player
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

        // Init video encoder
        try {
            if (selectedVideoCodec.getCodecName().equalsIgnoreCase(H264Config.CODEC_NAME)) {
                // H264
                NativeH264Encoder.InitEncoder(selectedVideoCodec.getWidth(),
                        selectedVideoCodec.getHeight(), selectedVideoCodec.getFramerate());
                // TODO: To be analyzed: InitEncoder exit with 0 but it works...
                // if (result == 0) {
                //   notifyPlayerEventError("Encoder init failed with error code " + result);
                //   return;
                // }
            } else if (selectedVideoCodec.getCodecName().equalsIgnoreCase(H263Config.CODEC_NAME)) {
                // Default H263
                NativeH263EncoderParams params = new NativeH263EncoderParams();
                params.setEncFrameRate(selectedVideoCodec.getFramerate());
                params.setBitRate(selectedVideoCodec.getBitrate());
                params.setTickPerSrc(params.getTimeIncRes() / selectedVideoCodec.getFramerate());
                params.setIntraPeriod(-1);
                params.setNoFrameSkipped(false);
                int result = NativeH263Encoder.InitEncoder(params);
                if (result != 1) {
                    notifyPlayerEventError("Encoder init failed with error code " + result);
                    return;
                }
            }
        } catch (UnsatisfiedLinkError e) {
            notifyPlayerEventError(e.getMessage());
            return;
        }

        // Init the RTP layer
        try {
        	releasePort();
            rtpSender = new MediaRtpSender(videoFormat, localRtpPort);
            rtpInput = new MediaRtpInput();
            rtpInput.open();
            rtpSender.prepareSession(rtpInput, remoteHost, remotePort);
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
     * Close the player
     */
    public void close() {
        if (!opened) {
            // Already closed
            return;
        }
        // Close the RTP layer
        rtpInput.close();
        rtpSender.stopSession();

        try {
            // Close the video encoder
            if (selectedVideoCodec.getCodecName().equalsIgnoreCase(H264Config.CODEC_NAME)) {
                NativeH264Encoder.DeinitEncoder();
            } else if (selectedVideoCodec.getCodecName().equalsIgnoreCase(H263Config.CODEC_NAME)) {
                NativeH263Encoder.DeinitEncoder();
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
    public synchronized void start() {
        if (!opened) {
            // Player not opened
            return;
        }

        if (started) {
            // Already started
            return;
        }

        // Start RTP layer
        rtpSender.startSession();

        // Start capture
        captureThread.start();

        // Player is started
        videoStartTime = SystemClock.uptimeMillis();
        started = true;
        notifyPlayerEventStarted();
    }

    /**
     * Stop the player
     */
    public void stop() {
        if (!opened) {
            // Player not opened
            return;
        }

        if (!started) {
            // Already stopped
            return;
        }

        // Stop capture
        try {
            captureThread.interrupt();
        } catch (Exception e) {
        }

        // Player is stopped
        videoStartTime = 0L;
        started = false;
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
     * @return Media Codec
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

            // Initialize frame buffer
            if (frameBuffer == null)
                frameBuffer = new CameraBuffer();
        } else {
            notifyPlayerEventError("Codec not supported");
        }
    }

    /**
     * Notify player event started
     */
    private void notifyPlayerEventStarted() {
    	if(iMediaEventListener==null)
    		return;
        Iterator<IMediaEventListener> ite = iMediaEventListener.iterator();
        while (ite.hasNext()) {
            try {
                ((IMediaEventListener)ite.next()).onMediaStarted();
            } catch (java.lang.Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Notify player event stopped
     */
    private void notifyPlayerEventStopped() {

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
    private void notifyPlayerEventOpened() {

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
    private void notifyPlayerEventClosed() {

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
    private void notifyPlayerEventError(String error) {
    	Log.d("LiveVideoPlayer", "LiveVideoPlayer error:"+error);
    	if(iMediaEventListener==null)
    		return;
        Iterator<IMediaEventListener> ite = iMediaEventListener.iterator();
        while (ite.hasNext()) {
            ((IMediaEventListener)ite.next()).onMediaError(error);  
        }
    }

    /**
     * Preview frame from the camera
     *
     * @param data Frame
     * @param camera Camera
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (frameBuffer != null)
            frameBuffer.setFrame(data);
    }

    /**
     * Video capture thread
     */
    private Thread captureThread = new Thread() {
        /**
         * Timestamp
         */
        private long timeStamp = 0;

        /**
         * Processing
         */
        public void run() {
            if (rtpInput == null) {
                return;
            }

            int timeToSleep = 1000 / selectedVideoCodec.getFramerate();
            int timestampInc = 90000 / selectedVideoCodec.getFramerate();
            byte[] frameData;
            byte[] encodedFrame;
            long encoderTs = 0;
            long oldTs = System.currentTimeMillis();

            while (started) {
                // Set timestamp
                long time = System.currentTimeMillis();
                encoderTs = encoderTs + (time - oldTs);

                // Get data to encode
                frameData = frameBuffer.getFrame();
                // Encode frame
                if (selectedVideoCodec.getCodecName().equalsIgnoreCase(H264Config.CODEC_NAME)) {
                    encodedFrame = NativeH264Encoder.EncodeFrame(frameData, encoderTs);
                } else {
                    encodedFrame = NativeH263Encoder.EncodeFrame(frameData, encoderTs);
                }

                if (encodedFrame.length > 0) {
                    // Send encoded frame
                    rtpInput.addFrame(encodedFrame, timeStamp += timestampInc);
                    //Log.d("LiveVideoPlayer", "LiveVideoPlayer:encoded is:"+selectedVideoCodec.getCodecName());
                }
                // Sleep between frames if necessary
                long delta = System.currentTimeMillis() - time;
                if (delta < timeToSleep) {
                    try {
                        Thread.sleep((timeToSleep - delta) - (((timeToSleep - delta) * 10) / 100));
                    } catch (InterruptedException e) {
                    }
                }

                // Update old timestamp
                oldTs = time;
            }
        }
    };
    
    
    private  class MediaRtpInput implements MediaInput
    {
        /**
         * Received frames
         */
        private FifoBuffer fifo = null;
        /**
         * Constructor
         */
        public MediaRtpInput() {
        }
        /**
         * Add a new video frame
         *
         * @param data Data
         * @param timestamp Timestamp
         */
        public void addFrame(byte[] data, long timestamp) {
            if (fifo != null) {
                fifo.addObject(new MediaSample(data, timestamp));
            }
        }
        
        /**
         * Open the player
         */
        public void open() {
            fifo = new FifoBuffer();
        }
        
        /**
         * Read a media sample (blocking method)
         *
         * @return Media sample
         * @throws MediaException
         */
        public MediaSample readSample() throws java.lang.Exception {
            try {
                if (fifo != null) {
                    return (MediaSample)fifo.getObject();
                } else {
                    throw new java.lang.Exception("Media input not opened");
                }
            } catch (Exception e) {
                throw new java.lang.Exception("Can't read media sample");
            }
        }
		@Override
		public void close() {
			// TODO Auto-generated method stub
			if(fifo!=null)
				fifo.close();
			
		}
        
        
    }
    
    /**
     * Camera buffer
     */
    private class CameraBuffer {
        /**
         * YUV frame where frame size is always (videoWidth*videoHeight*3)/2
         */
        private byte frame[] = new byte[(selectedVideoCodec.getWidth()
                * selectedVideoCodec.getHeight() * 3) / 2];

        /**
         * Set the last captured frame
         *
         * @param frame Frame
         */
        public void setFrame(byte[] frame) {
            this.frame = frame;
        }

        /**
         * Return the last captured frame
         *
         * @return Frame
         */
        public byte[] getFrame() {
            return frame;
        }
    }
    
    
}