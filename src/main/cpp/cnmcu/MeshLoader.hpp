#pragma once

#include <jni.h>

class MeshLoader
{
public:
    static void loadPLY(JNIEnv* env, const char* modelBuffer, size_t bufferSize, jobject mesh);
};