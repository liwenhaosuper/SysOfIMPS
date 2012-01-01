package com.imps.media.video.codec;


/**
 * 
 * @author liwenhaosuper
 *
 */
public class NativeH263Decoder {

	public NativeH263Decoder()
    {
    }

    public static native int InitDecoder(int i, int j);

    public static native int DeinitDecoder();

    public static native int DecodeAndConvert(byte abyte0[], int ai[], long l);

    public static native int InitParser(String s);

    public static native int DeinitParser();

    public static native int getVideoLength();

    public static native int getVideoWidth();

    public static native int getVideoHeight();

    public static native String getVideoCoding();

    public static native VideoSample getVideoSample(int ai[]);

    static 
    {
        String libname = "H263Decoder";
        try
        {
            System.loadLibrary(libname);
        }
        catch(Exception exception) { }
    }
}
