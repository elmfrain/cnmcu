package com.elmfer.cnmcu.cpp;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is used to store the native pointer of a native object.
 * It is used to track the native object's life cycle. Java code is
 * not responisble for the life cycle of the native object, therefore
 * it can only borrow the native object (if it is still valid).
 */
public class WeakNativeObject {

    private static final Map<Class<? extends WeakNativeObject>, Method[]> INVALIDATORS = new ConcurrentHashMap<>();

    static {
        INVALIDATORS.put(WeakNativeObject.class, getInvalidators(WeakNativeObject.class));
    }
    
    private long nativePtr = 0;

    protected WeakNativeObject() {
    }

    protected long getNativePtr() {
        return nativePtr;
    }

    protected void setNativePtr(long ptr) {
        nativePtr = ptr;
    }

    public void invalidateNativeObject() {
        nativePtr = 0;
        
        Method[] invalidators = INVALIDATORS.get(getClass());
        Object[] args = { nativePtr };
        for (Method invalidator : invalidators) {
            try {
                invalidator.invoke(this, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /*
     * Check if the native object is still valid.
     */
    public boolean isNativeObjectValid() {
        return nativePtr != 0;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(nativePtr);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WeakNativeObject) {
            WeakNativeObject other = (WeakNativeObject) obj;
            return nativePtr == other.nativePtr;
        }
        return false;
    }

    /**
     * Collect all invalidators of the class and its super classes with the help of
     * reflection.
     */
    private static Method[] getInvalidators(Class<? extends WeakNativeObject> clazz) {
        ArrayList<Method> invalidators = new ArrayList<>();
        for (Class<?> c = clazz; c != WeakNativeObject.class; c = c.getSuperclass()) {
            try {
                Method invalidator = c.getDeclaredMethod("invalidateNative", long.class);
                invalidator.setAccessible(true);
                invalidators.add(invalidator);
            } catch (IllegalArgumentException | NoClassDefFoundError | SecurityException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
            }
        }

        int numInvalidators = invalidators.size();
        Method[] finalizers = new Method[numInvalidators];
        invalidators.toArray(finalizers);

        return finalizers;
    }
}
