package com.imps.media.video.codec;

public class NativeH264Encoder
{

    public NativeH264Encoder()
    {
    }

    public static native int InitEncoder(int i, int j, int k);

    public static native byte[] EncodeFrame(byte abyte0[], long l);

    public static native int DeinitEncoder();

    public static native int getLastEncodeStatus();

    static 
    {
        String libname = "H264Encoder";
        try
        {
            System.loadLibrary(libname);
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror) { }
    }
}

