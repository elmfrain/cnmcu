#include "MeshLoader.hpp"

#include "cnmcuJava.h"

#include <cmath>

void MeshLoader::loadPLY(JNIEnv* env, const char* modelBuffer, size_t bufferSize, jobject mesh)
{
    membuf sbuf(const_cast<char*>(modelBuffer), const_cast<char*>(modelBuffer + bufferSize));
    std::istream stream(&sbuf);

    happly::PLYData plyData(stream);

    if(!plyData.hasElement("vertex"))
    {
        env->ThrowNew(cnmcuJava::IllegalArgumentException, "Vertex element is missing");
        return;
    }

    happly::Element& vertices = plyData.getElement("vertex");
    jint numVertices = static_cast<jint>(vertices.count);

    // Get position data
    std::unique_ptr<float[]> positions = loadProperties(vertices, "x,y,z", env, mesh, cnmcuJava::Mesh_loadPositions);
    if(!positions)
    {
        env->ThrowNew(cnmcuJava::IllegalArgumentException, "Position properties are missing");
        return;
    }

    // Get UV data
    std::unique_ptr<float[]> uvs = loadProperties(vertices, "u,v|s,t", env, mesh, cnmcuJava::Mesh_loadUvs);
    uvs.reset();

    // Get (or generate) normal data
    std::unique_ptr<float[]> normals = loadProperties(vertices, "nx,ny,nz", env, mesh, cnmcuJava::Mesh_loadNormals);
    if(!normals)
    {
        std::unique_ptr<float[]> normals(new float[numVertices * 3]);
        genNormals(numVertices, positions.get(), normals.get());
        jobject bytebuffer = env->NewDirectByteBuffer(normals.get(), sizeof(float) * numVertices * 3);
        env->CallVoidMethod(mesh, cnmcuJava::Mesh_loadNormals, bytebuffer, numVertices);
        env->DeleteLocalRef(bytebuffer);
    }
    normals.reset();

    // Get color data
    std::unique_ptr<float[]> colors = loadColors(vertices, "r,g,b,a|red,green,blue,alpha", env, mesh, cnmcuJava::Mesh_loadColors);
    colors.reset();

    // Get index data
    if(plyData.hasElement("face") && plyData.getElement("face").hasProperty("vertex_indices"))
    {
        std::vector<std::vector<int>> faces = plyData.getElement("face").getListProperty<int>("vertex_indices");
        jint numIndices = static_cast<jint>(faces.size()) * 4;
        std::unique_ptr<int[]> buffer = std::make_unique<int[]>(numIndices);

        for(jint i = 0; i < faces.size(); i++)
        {
            std::vector<int> face = faces[i];

            if(face.size() != 4)
            {
                env->ThrowNew(cnmcuJava::IllegalArgumentException, "Only quads are supported");
                return;
            }

            buffer[i * 4] = face[0];
            buffer[i * 4 + 1] = face[1];
            buffer[i * 4 + 2] = face[2];
            buffer[i * 4 + 3] = face[3];
        }

        jobject bytebuffer = env->NewDirectByteBuffer(buffer.get(), sizeof(int) * numIndices);
        env->CallVoidMethod(mesh, cnmcuJava::Mesh_loadIndices, bytebuffer, numIndices);
        env->DeleteLocalRef(bytebuffer);
    }
    else
    {
        jint numIndices = numVertices - (numVertices % 4);
        std::unique_ptr<int[]> indices(new int[numIndices]);
        for(jint i = 0; i < numIndices; i++)
            indices[i] = i;

        jobject bytebuffer = env->NewDirectByteBuffer(indices.get(), sizeof(int) * numIndices);
        env->CallVoidMethod(mesh, cnmcuJava::Mesh_loadIndices, bytebuffer, numIndices);
        env->DeleteLocalRef(bytebuffer);
    }
}

void MeshLoader::genNormals(int numVertices, const float* positions, float* normals)
{
    for(int i = 0; i < numVertices; i += 4)
    {
        float x1 = positions[i * 3];
        float y1 = positions[i * 3 + 1];
        float z1 = positions[i * 3 + 2];

        float x2 = positions[i * 3 + 3];
        float y2 = positions[i * 3 + 4];
        float z2 = positions[i * 3 + 5];

        float x3 = positions[i * 3 + 6];
        float y3 = positions[i * 3 + 7];
        float z3 = positions[i * 3 + 8];

        float x4 = positions[i * 3 + 9];
        float y4 = positions[i * 3 + 10];
        float z4 = positions[i * 3 + 11];

        float nx = (y1 - y2) * (z1 + z2) + (y2 - y3) * (z2 + z3) + (y3 - y4) * (z3 + z4) + (y4 - y1) * (z4 + z1);
        float ny = (z1 - z2) * (x1 + x2) + (z2 - z3) * (x2 + x3) + (z3 - z4) * (x3 + x4) + (z4 - z1) * (x4 + x1);
        float nz = (x1 - x2) * (y1 + y2) + (x2 - x3) * (y2 + y3) + (x3 - x4) * (y3 + y4) + (x4 - x1) * (y4 + y1);

        float length = sqrt(nx * nx + ny * ny + nz * nz);
        nx /= length;
        ny /= length;
        nz /= length;

        for(int j = 0; j < 4; j++)
        {
            normals[i * 3 + j * 3] = nx;
            normals[i * 3 + j * 3 + 1] = ny;
            normals[i * 3 + j * 3 + 2] = nz;
        }
    }
}

std::unique_ptr<float[]> MeshLoader::loadProperties(happly::Element& vertData, const char* properites, JNIEnv* env, jobject mesh, jmethodID method)
{
    std::istringstream iss(properites);
    std::string line;

    while(std::getline(iss, line, '|'))
    {
        std::vector<float> data[4];
        std::istringstream iss2(line);
        int index = 0;
        std::string properites[4];
        std::string property;

        while(std::getline(iss2, property, ','))
        {
            if(index >= 4 || !vertData.hasProperty(property))
            {
                index = 0;
                break;
            }

            properites[index++] = property;
        }

        if(index == 0)
            continue;

        for(int i = 0; i < index; i++)
            data[i] = vertData.getProperty<float>(properites[i]);

        float* buffer = new float[vertData.count * index];

        for(jint i = 0; i < vertData.count; i++)
            for(int j = 0; j < index; j++)
                buffer[i * index + j] = data[j][i];

        jobject bytebuffer = env->NewDirectByteBuffer(buffer, sizeof(float) * vertData.count * index);
        env->CallVoidMethod(mesh, method, bytebuffer, vertData.count);
        env->DeleteLocalRef(bytebuffer);

        return std::unique_ptr<float[]>(buffer);
    }

    return nullptr;
}

std::unique_ptr<float[]> MeshLoader::loadColors(happly::Element& vertData, const char* properites, JNIEnv* env, jobject mesh, jmethodID method)
{
    std::istringstream iss(properites);
    std::string line;

    while(std::getline(iss, line, '|'))
    {
        std::vector<uint8_t> data[4];
        std::istringstream iss2(line);
        int index = 0;
        std::string properites[4];
        std::string property;

        while(std::getline(iss2, property, ','))
        {
            if(index >= 4 || !vertData.hasProperty(property))
            {
                index = 0;
                break;
            }

            properites[index++] = property;
        }

        if(index == 0)
            continue;

        for(int i = 0; i < index; i++)
            data[i] = vertData.getProperty<uint8_t>(properites[i]);

        float* buffer = new float[vertData.count * index];

        for(jint i = 0; i < vertData.count; i++)
            for(int j = 0; j < index; j++)
                buffer[i * index + j] = data[j][i] / 255.0f;

        jobject bytebuffer = env->NewDirectByteBuffer(buffer, sizeof(float) * vertData.count * index);
        env->CallVoidMethod(mesh, method, bytebuffer, vertData.count);
        env->DeleteLocalRef(bytebuffer);

        return std::unique_ptr<float[]>(buffer);
    }

    return nullptr;
}