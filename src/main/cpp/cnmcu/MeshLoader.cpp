#include "MeshLoader.hpp"

#include "cnmcuJava.h"

#include <memory>

#include <assimp/Importer.hpp>
#include <assimp/scene.h>
#include <assimp/postprocess.h>

void MeshLoader::loadPLY(JNIEnv* env, const char* modelBuffer, size_t bufferSize, jobject mesh)
{
    // cnmcuJava::printf("Loading model\n");

    Assimp::Importer importer;

    const aiScene* scene = importer.ReadFileFromMemory(modelBuffer, bufferSize, aiProcess_GenNormals, "ply");

    if(!scene)
    {
        env->ThrowNew(cnmcuJava::RuntimeException, "Failed to load model");
        return;
    }

    const aiMesh* meshData = scene->mMeshes[0];
    jint numVertices = meshData->mNumVertices;

    // Get position data
    jobject bytebuffer = env->NewDirectByteBuffer(meshData->mVertices, sizeof(aiVector3D) * numVertices);
    env->CallVoidMethod(mesh, cnmcuJava::Mesh_loadPositions, bytebuffer, numVertices);
    env->DeleteLocalRef(bytebuffer);

    // Get UV data
    if (meshData->HasTextureCoords(0))
    {
        bytebuffer = env->NewDirectByteBuffer(meshData->mTextureCoords[0], sizeof(aiVector3D) * numVertices);
        env->CallVoidMethod(mesh, cnmcuJava::Mesh_loadUvs, bytebuffer, numVertices);
        env->DeleteLocalRef(bytebuffer);
    }

    // Get normal data
    bytebuffer = env->NewDirectByteBuffer(meshData->mNormals, sizeof(aiVector3D) * numVertices);
    env->CallVoidMethod(mesh, cnmcuJava::Mesh_loadNormals, bytebuffer, numVertices);
    env->DeleteLocalRef(bytebuffer);

    // Get color data
    if (meshData->HasVertexColors(0))
    {
        bytebuffer = env->NewDirectByteBuffer(meshData->mColors[0], sizeof(aiColor4D) * numVertices);
        env->CallVoidMethod(mesh, cnmcuJava::Mesh_loadColors, bytebuffer, numVertices);
        env->DeleteLocalRef(bytebuffer);
    }

    // Get index data
    std::unique_ptr<jint[]> meshIndices(new jint[meshData->mNumFaces * 4]);
    for(unsigned int i = 0; i < meshData->mNumFaces; i++)
    {
        aiFace& face = meshData->mFaces[i];
        if (face.mNumIndices != 4)
        {
            env->ThrowNew(cnmcuJava::IllegalArgumentException, "Only quad faces are supported");
            return;
        }

        meshIndices[i * 4] = face.mIndices[0];
        meshIndices[i * 4 + 1] = face.mIndices[1];
        meshIndices[i * 4 + 2] = face.mIndices[2];
        meshIndices[i * 4 + 3] = face.mIndices[3];
    }
    bytebuffer = env->NewDirectByteBuffer(meshIndices.get(), sizeof(jint) * meshData->mNumFaces * 4);
    env->CallVoidMethod(mesh, cnmcuJava::Mesh_loadIndices, bytebuffer, meshData->mNumFaces * 4);
    env->DeleteLocalRef(bytebuffer);
}