#include <cnmcuJava.h>

JNIEnv* cnmcuJava::env;
JavaVM* cnmcuJava::vm;

jclass cnmcuJava::NullPointerException;
jclass cnmcuJava::IllegalArgumentException;
jclass cnmcuJava::IllegalStateException;
jclass cnmcuJava::RuntimeException;



jclass cnmcuJava::System;
jfieldID cnmcuJava::System_out_id;
jobject cnmcuJava::System_out;

jclass cnmcuJava::PrintStream;
jmethodID cnmcuJava::PrintStream_print;



jclass cnmcuJava::Mesh;
jmethodID cnmcuJava::Mesh_loadPositions;
jmethodID cnmcuJava::Mesh_loadNormals;
jmethodID cnmcuJava::Mesh_loadIndices;
jmethodID cnmcuJava::Mesh_loadColors;
jmethodID cnmcuJava::Mesh_loadUvs;



jclass cnmcuJava::NanoMCU;

jclass cnmcuJava::MOS6502;
jmethodID cnmcuJava::MOS6502_init;

jclass cnmcuJava::CNGPIO;
jmethodID cnmcuJava::CNGPIO_init;

jclass cnmcuJava::CNRAM;
jmethodID cnmcuJava::CNRAM_init;

jclass cnmcuJava::CNROM;
jmethodID cnmcuJava::CNROM_init;

jclass cnmcuJava::CNEL;
jmethodID cnmcuJava::CNEL_init;

jclass cnmcuJava::CNUART;
jmethodID cnmcuJava::CNUART_init;

bool cnmcuJava::initialized = false;

void cnmcuJava::init(JNIEnv* env)
{
    if(initialized)
        return;

    cnmcuJava::env = env;
    env->GetJavaVM(&vm);


    // Exceptions
    GET_CLASS(NullPointerException, "java/lang/NullPointerException");
    GET_CLASS(IllegalArgumentException, "java/lang/IllegalArgumentException");
    GET_CLASS(IllegalStateException, "java/lang/IllegalStateException");
    GET_CLASS(RuntimeException, "java/lang/RuntimeException");


    // System.out
    GET_CLASS(System, "java/lang/System");
    GET_STATIC_FIELD_ID(System_out_id, System, "out", "Ljava/io/PrintStream;");
    System_out = env->GetStaticObjectField(System, System_out_id);
    System_out = env->NewGlobalRef(System_out);

    GET_CLASS(PrintStream, "java/io/PrintStream");
    GET_METHOD_ID(PrintStream_print, PrintStream, "print", "(Ljava/lang/String;)V");


    // For mesh loading
    GET_CLASS(Mesh, "com/elmfer/cnmcu/mesh/Mesh");
    GET_METHOD_ID(Mesh_loadPositions, Mesh, "loadPositions", "(Ljava/nio/ByteBuffer;I)V");
    GET_METHOD_ID(Mesh_loadNormals, Mesh, "loadNormals", "(Ljava/nio/ByteBuffer;I)V");
    GET_METHOD_ID(Mesh_loadIndices, Mesh, "loadIndices", "(Ljava/nio/ByteBuffer;I)V");
    GET_METHOD_ID(Mesh_loadColors, Mesh, "loadColors", "(Ljava/nio/ByteBuffer;I)V");
    GET_METHOD_ID(Mesh_loadUvs, Mesh, "loadUvs", "(Ljava/nio/ByteBuffer;I)V");


    // For CNMCU
    GET_CLASS(NanoMCU, "com/elmfer/cnmcu/mcu/NanoMCU");

    GET_CLASS(MOS6502, "com/elmfer/cnmcu/mcu/cpu/MOS6502");
    GET_METHOD_ID(MOS6502_init, MOS6502, "<init>", "(J)V");

    GET_CLASS(CNGPIO, "com/elmfer/cnmcu/mcu/modules/CNGPIO");
    GET_METHOD_ID(CNGPIO_init, CNGPIO, "<init>", "(J)V");

    GET_CLASS(CNRAM, "com/elmfer/cnmcu/mcu/modules/CNRAM");
    GET_METHOD_ID(CNRAM_init, CNRAM, "<init>", "(J)V");

    GET_CLASS(CNROM, "com/elmfer/cnmcu/mcu/modules/CNROM");
    GET_METHOD_ID(CNROM_init, CNROM, "<init>", "(J)V");

    GET_CLASS(CNEL, "com/elmfer/cnmcu/mcu/modules/CNEL");
    GET_METHOD_ID(CNEL_init, CNEL, "<init>", "(J)V");

    GET_CLASS(CNUART, "com/elmfer/cnmcu/mcu/modules/CNUART");
    GET_METHOD_ID(CNUART_init, CNUART, "<init>", "(J)V");

    initialized = true;
}

void cnmcuJava::printf(const char* format, ...)
{
    if(!initialized)
        return;

    va_list args;
    va_start(args, format);

    char buffer[512] = {0};
    vsnprintf(buffer, sizeof(buffer) - 1, format, args);

    va_end(args);

    jstring str = env->NewStringUTF(buffer);
    env->CallVoidMethod(System_out, PrintStream_print, str);
    env->DeleteLocalRef(str);
}