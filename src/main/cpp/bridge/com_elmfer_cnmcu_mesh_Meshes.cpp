#include <com_elmfer_cnmcu_mesh_Meshes.h>

//@line:101

        #include "MeshLoader.hpp"
        #include "cnmcuJava.h"
    JNIEXPORT void JNICALL Java_com_elmfer_cnmcu_mesh_Meshes_parsePLY(JNIEnv* env, jclass clazz, jbyteArray obj_data, jobject mesh) {
	char* data = (char*)env->GetPrimitiveArrayCritical(obj_data, 0);


//@line:106

        cnmcuJava::init(env);
        size_t dataSize = static_cast<size_t>(env->GetArrayLength(obj_data));
        MeshLoader::loadPLY(env, data, dataSize, mesh);
    
	env->ReleasePrimitiveArrayCritical(obj_data, data, 0);

}

