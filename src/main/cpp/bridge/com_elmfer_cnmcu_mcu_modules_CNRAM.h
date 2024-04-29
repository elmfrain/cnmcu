/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_elmfer_cnmcu_mcu_modules_CNRAM */

#ifndef _Included_com_elmfer_cnmcu_mcu_modules_CNRAM
#define _Included_com_elmfer_cnmcu_mcu_modules_CNRAM
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_elmfer_cnmcu_mcu_modules_CNRAM
 * Method:    size
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNRAM_size
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_elmfer_cnmcu_mcu_modules_CNRAM
 * Method:    data
 * Signature: (J)Ljava/nio/ByteBuffer;
 */
JNIEXPORT jobject JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNRAM_data
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_elmfer_cnmcu_mcu_modules_CNRAM
 * Method:    read
 * Signature: (JI)B
 */
JNIEXPORT jbyte JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNRAM_read
  (JNIEnv *, jclass, jlong, jint);

/*
 * Class:     com_elmfer_cnmcu_mcu_modules_CNRAM
 * Method:    write
 * Signature: (JIB)V
 */
JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mcu_modules_CNRAM_write
  (JNIEnv *, jclass, jlong, jint, jbyte);

#ifdef __cplusplus
}
#endif
#endif