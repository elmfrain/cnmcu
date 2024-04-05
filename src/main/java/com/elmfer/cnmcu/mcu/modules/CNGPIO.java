package com.elmfer.cnmcu.mcu.modules;

import com.elmfer.cnmcu.cpp.WeakNativeObject;

/**
 * Reference to a CNGPIO object
 * 
 * it is a weak reference, so it will be invalidated if the native
 * object is deleted.
 */
public class CNGPIO extends WeakNativeObject {

    /**
     * Constructor
     * 
     * Called in the mod's native code, do not call directly.
     */
    protected CNGPIO(long ptr) {
        setNativePtr(ptr);
    }
}
