package com.elmfer.cnmcu.cpp;

import java.nio.ByteBuffer;

public class NativesUtils {

    private NativesUtils() {
    }
    
    // @formatter:off
    
    public static native long getByteBufferAddress(ByteBuffer buffer); /*
        return (jlong) env->GetDirectBufferAddress(obj_buffer);
    */
}
