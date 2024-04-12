#include <com_elmfer_cnmcu_mcu_modules_CNRAM.h>

#include <cnmcuJava.h>

#include <Nano.hpp>

/*
 * Class:     com_elmfer_cnmcu_mcu_modules_CNRAM
 * Method:    size
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNRAM_size
    (JNIEnv*, jclass, jlong nanoId)
{
    return static_cast<jlong>(CodeNodeNano::RAM_SIZE);
}

/*
 * Class:     com_elmfer_cnmcu_mcu_modules_CNRAM
 * Method:    data
 * Signature: (J)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNRAM_data
    (JNIEnv* env, jclass, jlong nanoId)
{
    CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(nanoId);
    CNRAM<CodeNodeNano::RAM_SIZE>& ram = nano->RAM();
    return env->NewDirectByteBuffer(ram.data(), CodeNodeNano::RAM_SIZE);
}

/*
 * Class:     com_elmfer_cnmcu_mcu_modules_CNRAM
 * Method:    read
 * Signature: (JI)B
 */
JNIEXPORT jbyte JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNRAM_read
    (JNIEnv*, jclass, jlong nanoId, jint address)
{
    CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(nanoId);
    CNRAM<CodeNodeNano::RAM_SIZE>& ram = nano->RAM();
    return static_cast<jbyte>(ram.read(static_cast<uint16_t>(address)));
}

/*
 * Class:     com_elmfer_cnmcu_mcu_modules_CNRAM
 * Method:    write
 * Signature: (JIB)V
 */
JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNRAM_write
    (JNIEnv*, jclass, jlong nanoId, jint address, jbyte data)
{
    CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(nanoId);
    CNRAM<CodeNodeNano::RAM_SIZE>& ram = nano->RAM();
    ram.write(static_cast<uint16_t>(address), static_cast<uint8_t>(data));
}