package com.iab.GJobs.util;

public class NativeClass {
    static {
        System.loadLibrary("native_lib");
    }
    public static native String getDecodedString();
}
