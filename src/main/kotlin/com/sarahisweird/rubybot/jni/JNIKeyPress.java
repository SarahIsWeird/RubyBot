package com.sarahisweird.rubybot.jni;

public class JNIKeyPress {
    static {
        System.loadLibrary("JNIKeyPress");
    }

    public native void press(int key);
    public native void release(int key);
    public native void stroke(int key);

    public native int getMainDspHandle();
    public native byte[] getDisplayContent(int displayId);
    public native long getLastImageWidth();
    public native long getLastImageHeight();
}
