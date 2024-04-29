#include <cnmcuJava.h>

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

jclass cnmcuJava::NanoMCU;

jclass cnmcuJava::MOS6502;
jmethodID cnmcuJava::MOS6502_init;

jclass cnmcuJava::CNGPIO;
jmethodID cnmcuJava::CNGPIO_init;

jclass cnmcuJava::CNRAM;
jmethodID cnmcuJava::CNRAM_init;

jclass cnmcuJava::CNROM;
jmethodID cnmcuJava::CNROM_init;

bool cnmcuJava::initialized = false;

void cnmcuJava::init(JNIEnv* env)
{
    if(initialized)
        return;

    cnmcuJava::env = env;
    env->GetJavaVM(&vm);

    GET_CLASS(NullPointerException, "java/lang/NullPointerException");
    GET_CLASS(IllegalArgumentException, "java/lang/IllegalArgumentException");
    GET_CLASS(IllegalStateException, "java/lang/IllegalStateException");
    GET_CLASS(RuntimeException, "java/lang/RuntimeException");

    GET_CLASS(System, "java/lang/System");
    GET_STATIC_FIELD_ID(System_out_id, System, "out", "Ljava/io/PrintStream;");
    System_out = env->GetStaticObjectField(System, System_out_id);

    GET_CLASS(PrintStream, "java/io/PrintStream");
    GET_METHOD_ID(PrintStream_print, PrintStream, "print", "(Ljava/lang/String;)V");

    GET_CLASS(NanoMCU, "com/elmfer/cnmcu/mcu/NanoMCU");

    GET_CLASS(MOS6502, "com/elmfer/cnmcu/mcu/cpu/MOS6502");
    GET_METHOD_ID(MOS6502_init, MOS6502, "<init>", "(J)V");

    GET_CLASS(CNGPIO, "com/elmfer/cnmcu/mcu/modules/CNGPIO");
    GET_METHOD_ID(CNGPIO_init, CNGPIO, "<init>", "(J)V");

    GET_CLASS(CNRAM, "com/elmfer/cnmcu/mcu/modules/CNRAM");
    GET_METHOD_ID(CNRAM_init, CNRAM, "<init>", "(J)V");

    GET_CLASS(CNROM, "com/elmfer/cnmcu/mcu/modules/CNROM");
    GET_METHOD_ID(CNROM_init, CNROM, "<init>", "(J)V");

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