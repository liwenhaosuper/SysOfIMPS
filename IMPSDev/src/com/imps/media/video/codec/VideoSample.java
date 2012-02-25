package com.imps.media.video.codec;
/**
 * 
 * @author liwenhaosuper
 *
 */
public class VideoSample {
    public byte data[];
    public int timestamp;

    public VideoSample(byte data[], int timestamp)
    {
        this.data = null;
        this.timestamp = 0;
        this.data = data;
        this.timestamp = timestamp;
    }
}
