#pragma once

#include <jni.h>

#define CHECK_FOR_EXCEPTION() \
    if(env->ExceptionCheck()) \
    { \
        env->ExceptionDescribe(); \
        env->ExceptionClear(); \
        return; \
    } \

class cnmcuJava
{
public:
    static JNIEnv* env;
    static JavaVM* vm;

    static jclass NullPointerException;
    static jclass IllegalArgumentException;
    static jclass IllegalStateException;
    static jclass RuntimeException;

    static jclass System;
    static jfieldID System_out_id;
    static jobject System_out;

    static jclass PrintStream;
    static jmethodID PrintStream_print;

    static jclass NanoMCU;

    static jclass MOS6502;
    static jmethodID MOS6502_init;

    static jclass CNGPIO;
    static jmethodID CNGPIO_init;

    static jclass CNRAM;
    static jmethodID CNRAM_init;

    static jclass CNROM;
    static jmethodID CNROM_init;

    static bool initialized;

    static void init(JNIEnv* env);
    static void printf(const char* format, ...);
};