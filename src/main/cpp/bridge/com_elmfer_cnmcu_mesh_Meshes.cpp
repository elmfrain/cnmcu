#include <com_elmfer_cnmcu_mesh_Meshes.h>

//@line:101

        #include "MeshLoader.hpp"
        #include "cnmcuJava.h"
        #include <exception>
    JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mesh_Meshes_parsePLY(JNIEnv* env, jclass clazz, jbyteArray obj_data, jobject mesh) {
	char* data = (char*)env->GetPrimitiveArrayCritical(obj_data, 0);


//@line:107

        cnmcuJava::init(env);
        size_t dataSize = static_cast<size_t>(env->GetArrayLength(obj_data));
        try {
            MeshLoader::loadPLY(env, data, dataSize, mesh);
        } catch (const std::exception& e) {
            env->ThrowNew(cnmcuJava::IllegalArgumentException, e.what());
        }
    
	env->ReleasePrimitiveArrayCritical(obj_data, data, 0);

}

