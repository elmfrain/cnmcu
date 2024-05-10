#pragma once

#include <jni.h>

#define CHECK_FOR_EXCEPTION() \
    if(env->ExceptionCheck()) \
    { \
        env->ExceptionDescribe(); \
        env->ExceptionClear(); \
        return; \
    } \

#define GET_CLASS(var, name) \
    (var) = env->FindClass(name); \
    CHECK_FOR_EXCEPTION(); \
    (var) = (jclass) env->NewGlobalRef(var); \
    CHECK_FOR_EXCEPTION(); \

#define GET_METHOD_ID(var, clazz, name, sig) \
    (var) = env->GetMethodID(clazz, name, sig); \
    CHECK_FOR_EXCEPTION(); \

#define GET_STATIC_METHOD_ID(var, clazz, name, sig) \
    (var) = env->GetStaticMethodID(clazz, name, sig); \
    CHECK_FOR_EXCEPTION(); \

#define GET_FIELD_ID(var, clazz, name, sig) \
    (var) = env->GetFieldID(clazz, name, sig); \
    CHECK_FOR_EXCEPTION(); \

#define GET_STATIC_FIELD_ID(var, clazz, name, sig) \
    (var) = env->GetStaticFieldID(clazz, name, sig); \
    CHECK_FOR_EXCEPTION(); \

class cnmcuJava
{
public:
    static JNIEnv* env;
    static JavaVM* vm;


    // Exceptions
    static jclass NullPointerException;
    static jclass IllegalArgumentException;
    static jclass IllegalStateException;
    static jclass RuntimeException;


    // System.out
    static jclass System;
    static jfieldID System_out_id;
    static jobject System_out;

    static jclass PrintStream;
    static jmethodID PrintStream_print;


    // For mesh loading
    static jclass Mesh;
    static jmethodID Mesh_loadPositions;
    static jmethodID Mesh_loadNormals;
    static jmethodID Mesh_loadIndices;
    static jmethodID Mesh_loadColors;
    static jmethodID Mesh_loadUvs;


    // For CNMCU
    static jclass NanoMCU;

    static jclass MOS6502;
    static jmethodID MOS6502_init;

    static jclass CNGPIO;
    static jmethodID CNGPIO_init;

    static jclass CNRAM;
    static jmethodID CNRAM_init;

    static jclass CNROM;
    static jmethodID CNROM_init;

    static jclass CNEL;
    static jmethodID CNEL_init;

    static jclass CNUART;
    static jmethodID CNUART_init;

    static bool initialized;

    static void init(JNIEnv* env);
    static void printf(const char* format, ...);
};