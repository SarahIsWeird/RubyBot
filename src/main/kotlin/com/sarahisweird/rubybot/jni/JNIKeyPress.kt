package com.sarahisweird.rubybot.jni

class JNIKeyPress {
    init {
        System.loadLibrary("JNIKeyPress")
    }

    private external fun press(key: Int)
    private external fun release(key: Int)
    private external fun stroke(key: Int)
    private external fun getMainDspHandle(): Int
    private external fun getLastImageWidth(): Long
    private external fun getLastImageHeight(): Long

    external fun getDisplayContent(displayId: Int): ByteArray

    val mainDisplayHandle: Int
        get() = getMainDspHandle()

    val lastWidth: Long
        get() = getLastImageWidth()
    val lastHeight: Long
        get() = getLastImageHeight()

    fun press(key: CGKeyCode) { press(key.keyCode) }
    fun release(key: CGKeyCode) { release(key.keyCode) }
    fun stroke(key: CGKeyCode) { stroke(key.keyCode) }
}