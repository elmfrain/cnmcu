package com.elmfer.cnmcu.mesh;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import com.elmfer.cnmcu.CodeNodeMicrocontrollersClient;

import net.minecraft.client.MinecraftClient;
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

            InputStream modelStream = MinecraftClient.getInstance().getResourceManager().getResource(model).get()
                    .getInputStream();
            BufferedInputStream modelFile = new BufferedInputStream(modelStream);
            modelFile.mark(Integer.MAX_VALUE);
            String[] modelPath = model.getPath().split("/");
            String modelName = modelPath[modelPath.length - 1].replace(".ply", "");
            try (Scanner scanner = new Scanner(modelStream)) {
                int vertexCount = 0;
                int vertexTotal = 0;
                int faceCount = 0;
                int faceTotal = 0;
                boolean headerDone = false;

                Mesh mesh = new Mesh(modelName);

                while (scanner.hasNextLine()) {
                    if (!headerDone) {
                        String line = scanner.nextLine();
                        if (line.contains("element vertex ")) {
                            vertexTotal = Integer.parseInt(line.substring(15));
                            continue;
                        } else if (line.contains("element face ")) {
                            faceTotal = Integer.parseInt(line.substring(13));
                            continue;
                        } else if (line.contains("end_header")) {
                            headerDone = true;
                            mesh.positions.ensureCapacity(vertexTotal * POSITION_COMPONENTS);
                            mesh.uvs = Optional.of(new ArrayList<Float>());
                            mesh.uvs.get().ensureCapacity(vertexTotal * UV_COMPONENTS);
                            mesh.normals.ensureCapacity(vertexTotal * NORMAL_COMPONENTS);
                            mesh.colors = Optional.of(new ArrayList<Float>());
                            mesh.colors.get().ensureCapacity(vertexTotal * COLOR_COMPONENTS);
                            mesh.indices.ensureCapacity(faceTotal * FACE_INDICIES);
                            continue;
                        }
                    } else {
                        if (vertexCount < vertexTotal) {
                            mesh.positions.add(scanner.nextFloat()); // pos x
                            mesh.positions.add(scanner.nextFloat()); // pos y
                            mesh.positions.add(scanner.nextFloat()); // pos z
                            mesh.normals.add(scanner.nextFloat()); // norm x
                            mesh.normals.add(scanner.nextFloat()); // norm y
                            mesh.normals.add(scanner.nextFloat()); // norm z
                            mesh.colors.get().add(scanner.nextFloat() / 255.0f); // color r
                            mesh.colors.get().add(scanner.nextFloat() / 255.0f); // color g
                            mesh.colors.get().add(scanner.nextFloat() / 255.0f); // color b
                            mesh.colors.get().add(scanner.nextFloat() / 255.0f); // color a
                            mesh.uvs.get().add(scanner.nextFloat()); // tex s
                            mesh.uvs.get().add(scanner.nextFloat()); // tex t
                            vertexCount++;
                        } else if (faceCount < faceTotal) {
                            int a = 0, b = 0, c = 0, d = 0;
                            if (scanner.nextInt() != 4)
                                throw new Exception("Only quads are supported");
                            a = scanner.nextInt();
                            b = scanner.nextInt();
                            c = scanner.nextInt();
                            d = scanner.nextInt();
                            mesh.indices.add(a);
                            mesh.indices.add(b);
                            mesh.indices.add(c);
                            mesh.indices.add(d);
                            faceCount++;
                        } else
                            break;
                    }
                }

                meshes.put(modelName, mesh);

                scanner.close();
                modelFile.close();

                return mesh;
            }
        } catch (Exception e) {
            CodeNodeMicrocontrollersClient.LOGGER.error("Failed to load \"{}\" mesh because of: {}", model.getPath(),
                    e.getMessage());
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
}