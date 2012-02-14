package com.imps.media.video.codec;


public class NativeH264Decoder
{

    public NativeH264Decoder()
    {
    }

    public static native int InitDecoder();

    public static native int DeinitDecoder();

    public static synchronized native int DecodeAndConvert(byte abyte0[], int ai[]);

    public static native int InitParser(String s);

    public static native int DeinitParser();

    public static native int getVideoLength();

    public static native int getVideoWidth();

    public static native int getVideoHeight();

    public static native String getVideoCoding();

    public static native VideoSample getVideoSample(int ai[]);

    static 
    {
        String libname = "H264Decoder";
        try
        {
            System.loadLibrary(libname);
        }
        catch(Exception exception) { }
    }
}

