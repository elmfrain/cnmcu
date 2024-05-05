package com.elmfer.cnmcu.mesh;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.elmfer.cnmcu.CodeNodeMicrocontrollersClient;
import com.elmfer.cnmcu.util.ResourceLoader;

import net.minecraft.util.Identifier;

public class Meshes {

    private static final Map<String, Mesh> meshes = new HashMap<String, Mesh>();

    private static final int POSITION_COMPONENTS = 3;
    private static final int NORMAL_COMPONENTS = 3;
    private static final int COLOR_COMPONENTS = 4;
    private static final int UV_COMPONENTS = 2;
    private static final int FACE_INDICIES = 4;

    public static Mesh get(String name) {
        return meshes.get(name);
    }

    public static boolean hasMesh(String name) {
        return meshes.containsKey(name);
    }

    public static Mesh load(Identifier model) {
        try {
            // Load the model file
            InputStream modelStream = ResourceLoader.getInputStream(model);
            byte[] modelData = modelStream.readAllBytes();
            modelStream.close();
            
            // Get the model name from the path
            String[] modelPath = model.getPath().split("/");
            String modelName = modelPath[modelPath.length - 1].replace(".ply", "");
            
            Mesh mesh = new Mesh(modelName);
            
            // Parse the PLY file
            parsePLY(modelData, mesh);
            
            meshes.put(modelName, mesh);
            return mesh;
        } catch (Exception e) {
            CodeNodeMicrocontrollersClient.LOGGER.error("Failed to load mesh: " + model, e);
            e.printStackTrace();
        }

        return null;
    }

    public static Mesh meshFromBuilder(MeshBuilder builder) {
        if (builder.getVertexFormat() != VertexFormat.POS_UV_COLOR_NORM)
            throw new IllegalArgumentException("MeshBuilder must have a VertexFormat of POS_UV_COLOR_NORMAL");

        Mesh mesh = new Mesh("");

        ByteBuffer buffer = builder.getVertexData();
        int vertexCount = builder.getVertexCount();

        mesh.positions.ensureCapacity(vertexCount * POSITION_COMPONENTS);
        mesh.uvs = Optional.of(new ArrayList<Float>());
        mesh.uvs.get().ensureCapacity(vertexCount * UV_COMPONENTS);
        mesh.normals.ensureCapacity(vertexCount * NORMAL_COMPONENTS);
        mesh.colors = Optional.of(new ArrayList<Float>());
        mesh.colors.get().ensureCapacity(vertexCount * COLOR_COMPONENTS);

        for (int i = 0; i < vertexCount; i++) {
            mesh.positions.add(buffer.getFloat()); // pos x
            mesh.positions.add(buffer.getFloat()); // pos y
            mesh.positions.add(buffer.getFloat()); // pos z
            mesh.uvs.get().add(buffer.getFloat()); // tex s
            mesh.uvs.get().add(buffer.getFloat()); // tex t
            mesh.colors.get().add((buffer.get() & 0xFF) / 255.0f); // color r
            mesh.colors.get().add((buffer.get() & 0xFF) / 255.0f); // color g
            mesh.colors.get().add((buffer.get() & 0xFF) / 255.0f); // color b
            mesh.colors.get().add((buffer.get() & 0xFF) / 255.0f); // color a
            mesh.normals.add(buffer.getFloat()); // norm x
            mesh.normals.add(buffer.getFloat()); // norm y
            mesh.normals.add(buffer.getFloat()); // norm z
        }

        mesh.indices.ensureCapacity(builder.getIndexCount());
        buffer = builder.getIndexData();

        while (buffer.hasRemaining())
            mesh.indices.add(buffer.getInt());

        return mesh;
    }
    
    // @formatter:off
    
    /*JNI
        #include "MeshLoader.hpp"
        #include "cnmcuJava.h"
        #include <exception>
    */
    
    private static native void parsePLY(byte[] data, Mesh mesh); /*
        cnmcuJava::init(env);
        size_t dataSize = static_cast<size_t>(env->GetArrayLength(obj_data));
        try {
            MeshLoader::loadPLY(env, data, dataSize, mesh);
        } catch (const std::exception& e) {
            env->ThrowNew(cnmcuJava::IllegalArgumentException, e.what());
        }
    */
}