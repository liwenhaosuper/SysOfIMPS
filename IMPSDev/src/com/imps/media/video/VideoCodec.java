package com.imps.media.video;

import com.imps.media.rtp.MediaCodec;

/**
 * Video codec
 * 
 * @author liwenhaosuper
 */
public class VideoCodec {

    /**
     * Media codec
     */
    private MediaCodec mediaCodec;

    /**
     * Clock rate key
     */
    private static final String CLOCKRATE = "clockRate";

    /**
     * Codec param key
     */
    private static final String CODECPARAMS = "codecParams";

    /**
     * Frame rate key
     */
    private static final String FRAMERATE = "framerate";

    /**
     * Bit rate key
     */
    private static final String BITRATE = "bitrate";

    /**
     * Codec with key
     */
    private static final String CODECWIDTH = "codecWidth";

    /**
     * Codec height key
     */
    private static final String CODECHEIGHT = "codecHeight";

    /**
     * Constructor
     * 
     * @param codecName Codec name
     * @param clockRate Clock rate
     * @param codecParams Codec parameters
     * @param framerate Frame rate
     * @param bitrate Bit rate
     * @param width Video width
     * @param height Video height
     */
    public VideoCodec(String codecName, int clockRate, String codecParams, int framerate,
            int bitrate, int width, int height) {
        mediaCodec = new MediaCodec(codecName);
        mediaCodec.setParam(CLOCKRATE, "" + clockRate);
        mediaCodec.setParam(CODECPARAMS, codecParams);
        mediaCodec.setParam(FRAMERATE, "" + framerate);
        mediaCodec.setParam(BITRATE, "" + bitrate);
        mediaCodec.setParam(CODECWIDTH, "" + width);
        mediaCodec.setParam(CODECHEIGHT, "" + height);
    }

    /**
     * Constructor
     * 
     * @param mediaCodec Media codec
     */
    public VideoCodec(MediaCodec mediaCodec) {
        this.mediaCodec = mediaCodec;
    }

    /**
     * Get media codec
     * 
     * @return media codec
     */
    public MediaCodec getMediaCodec() {
        return mediaCodec;
    }

    /**
     * Get codec name
     * 
     * @return Codec name
     */
    public String getCodecName() {
        return mediaCodec.getCodecName();
    }

    /**
     * Get video clock rate
     * 
     * @return Video clock rate
     */
    public int getClockRate() {
        return mediaCodec.getIntParam(CLOCKRATE, 90000);
    }

    /**
     * Get video codec parameters
     * 
     * @return Video codec parameters
     */
    public String getCodecParams() {
        return mediaCodec.getStringParam(CODECPARAMS);
    }

    /**
     * Get video frame rate
     * 
     * @return Video frame rate
     */
    public int getFramerate() {
        return mediaCodec.getIntParam(FRAMERATE, 15);
    }

    /**
     * Get video bitrate
     * 
     * @return Video bitrate
     */
    public int getBitrate() {
        return mediaCodec.getIntParam(BITRATE, 0);
    }

    /**
     * Get video width
     * 
     * @return Video width
     */
    public int getWidth() {
        return mediaCodec.getIntParam(CODECWIDTH, 176);
    }

    /**
     * Get video height
     * 
     * @return Video height
     */
    public int getHeight() {
        return mediaCodec.getIntParam(CODECHEIGHT, 144);
    }

    /**
     * Compare codec encodings and resolutions
     * 
     * @param codec Codec to compare
     * @return True if codecs are equals
     */
    public boolean equals(VideoCodec codec) {
        if (getCodecName().equalsIgnoreCase(codec.getCodecName()) &&
        		getWidth() == codec.getWidth() &&
        			getHeight() == codec.getHeight())
            return true;
        return false;
    }

    /**
     * Check if a codec is in a list
     *
     * @param supportedCodecs list of supported codec
     * @param codec selected codec
     * @return True if the codec is in the list
     */
    public static boolean checkVideoCodec(MediaCodec[] supportedCodecs, VideoCodec codec) {
        for (int i = 0; i < supportedCodecs.length; i++) {
            if (codec.equals(new VideoCodec(supportedCodecs[i])))
                return true;
        }
        return false;
    }
}

