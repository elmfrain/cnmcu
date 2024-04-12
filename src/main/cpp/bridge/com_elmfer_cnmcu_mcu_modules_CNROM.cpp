#include <com_elmfer_cnmcu_mcu_modules_CNROM.h>

#include <cnmcuJava.h>

#include <Nano.hpp>

/*
 * Class:     com_elmfer_cnmcu_mcu_modules_CNROM
 * Method:    size
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_size
    (JNIEnv*, jclass, jlong nanoId)
{
    return static_cast<jlong>(CodeNodeNano::ROM_SIZE);
}

/*
 * Class:     com_elmfer_cnmcu_mcu_modules_CNROM
 * Method:    data
 * Signature: (J)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_data
    (JNIEnv* env, jclass, jlong nanoId)
{
    CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(nanoId);
    CNROM<CodeNodeNano::ROM_SIZE>& rom = nano->ROM();
    return env->NewDirectByteBuffer(rom.data(), CodeNodeNano::ROM_SIZE);
}

/*
 * Class:     com_elmfer_cnmcu_mcu_modules_CNROM
 * Method:    read
 * Signature: (JI)B
 */
JNIEXPORT jbyte JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_read
    (JNIEnv*, jclass, jlong nanoId, jint address)
{
    CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(nanoId);
    CNROM<CodeNodeNano::ROM_SIZE>& rom = nano->ROM();
    return static_cast<jbyte>(rom.read(static_cast<uint16_t>(address)));
}

/*
 * Class:     com_elmfer_cnmcu_mcu_modules_CNROM
 * Method:    write
 * Signature: (JIB)V
 */
JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_write
    (JNIEnv*, jclass, jlong nanoId, jint address, jbyte data)
{
    CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(nanoId);
    CNROM<CodeNodeNano::ROM_SIZE>& rom = nano->ROM();
    rom.write(static_cast<uint16_t>(address), static_cast<uint8_t>(data));
}

/*
 * Class:     com_elmfer_cnmcu_mcu_modules_CNROM
 * Method:    isWriteProtected
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_isWriteProtected
    (JNIEnv*, jclass, jlong nanoId)
{
    CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(nanoId);
    CNROM<CodeNodeNano::ROM_SIZE>& rom = nano->ROM();
    return static_cast<jboolean>(rom.isWriteProtected());
}

/*
 * Class:     com_elmfer_cnmcu_mcu_modules_CNROM
 * Method:    setWriteProtected
 * Signature: (JZ)V
 */
JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNROM_setWriteProtected
    (JNIEnv*, jclass, jlong nanoId, jboolean writeProtected)
{
    CodeNodeNano* nano = reinterpret_cast<CodeNodeNano*>(nanoId);
    CNROM<CodeNodeNano::ROM_SIZE>& rom = nano->ROM();
    rom.setWriteProtect(static_cast<bool>(writeProtected));
}