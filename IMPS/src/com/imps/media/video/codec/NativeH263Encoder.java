package com.imps.media.video.codec;


public class NativeH263Encoder
{
    public NativeH263Encoder()
    {
    }
    public static native int InitEncoder(NativeH263EncoderParams nativeh263encoderparams);
    public static native byte[] EncodeFrame(byte abyte0[], long l);
    public static native int DeinitEncoder();
    static 
    {
        String libname = "H263Encoder";
        try
        {
            System.loadLibrary(libname);
        }
        catch(UnsatisfiedLinkError unsatisfiedlinkerror) { }
    }
}

