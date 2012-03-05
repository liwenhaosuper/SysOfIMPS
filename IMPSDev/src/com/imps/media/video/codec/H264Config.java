package com.imps.media.video.codec;

/**
 * Default H264 Settings
 *
 * @author liwenhaosuper
 */
public class H264Config {
    /**
     * H264 Codec Name
     */
    public final static String CODEC_NAME = "h264";

    /**
     * Default clock rate
     */
    public final static int CLOCK_RATE = 90000;

    /**
     * Default codec params
     */
    public final static String CODEC_PARAMS = "";
    // TODO: with value "profile-level-id=42B00B", the native encoder fails to
    // encode video frame!

    /**
     * Default video width
     */
    public final static int VIDEO_WIDTH = 176;

    /**
     * Default video height
     */
    public final static int VIDEO_HEIGHT = 144;

    /**
     * Default video frame rate
     */
    public final static int FRAME_RATE = 15;

    /**
     * Default video bit rate
     */
    public final static int BIT_RATE = 64000;
}

