#pragma once

#include <jni.h>

#include <istream>
#include <memory>

#include <happly.h>

class MeshLoader
{
public:
    // From https://stackoverflow.com/questions/7781898
    struct membuf : std::streambuf
    {
        membuf(char* begin, char* end) {
            this->setg(begin, begin, end);
        }
    };

    static void loadPLY(JNIEnv* env, const char* modelBuffer, size_t bufferSize, jobject mesh);
private:
    static void genNormals(int numVertices, const float* positions, float* normals);
    static std::unique_ptr<float[]> loadProperties(happly::Element& vertData, const char* properties, JNIEnv* env, jobject mesh, jmethodID method);
    static std::unique_ptr<float[]> loadColors(happly::Element& vertData, const char* properties, JNIEnv* env, jobject mesh, jmethodID method);
};