#include <com_elmfer_cnmcu_cpp_NativesUtils.h>
static inline jlong wrapped_Java_com_elmfer_cnmcu_cpp_NativesUtils_getByteBufferAddress
(JNIEnv* env, jclass clazz, jobject obj_buffer, char* buffer) {

//@line:12

        return (jlong) env->GetDirectBufferAddress(obj_buffer);
    
}

JNIEXPORT jlong JNICALL Java_com_elmfer_cnmcu_cpp_NativesUtils_getByteBufferAddress(JNIEnv* env, jclass clazz, jobject obj_buffer) {
	char* buffer = (char*)(obj_buffer?env->GetDirectBufferAddress(obj_buffer):0);

	jlong JNI_returnValue = wrapped_Java_com_elmfer_cnmcu_cpp_NativesUtils_getByteBufferAddress(env, clazz, obj_buffer, buffer);


	return JNI_returnValue;
}

