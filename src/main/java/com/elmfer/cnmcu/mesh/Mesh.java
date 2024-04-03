package com.elmfer.cnmcu.mesh;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Optional;

import org.lwjgl.opengl.GL30;

import com.elmfer.cnmcu.CodeNodeMicrocontrollersClient;
import com.elmfer.cnmcu.mesh.VertexFormat.VertexAttribute;

public class Mesh {
    public final String name;
    public final ArrayList<Float> positions = new ArrayList<Float>();
    public final ArrayList<Float> normals = new ArrayList<Float>();
    public final ArrayList<Integer> indices = new ArrayList<Integer>();

    public Optional<ArrayList<Float>> uvs = Optional.empty();
    public Optional<ArrayList<Float>> colors = Optional.empty();

    private boolean isRenderable = false;
    private boolean hasWarnedAboutNotBeingRenderable = false;

    private int glVBO;
    private int glEBO;
    private int glVAO;

    public Mesh(String name) {
        this.name = name;
    }

    public int numVertices() {
        return positions.size() / 3;
    }

    public boolean hasUvs() {
        return uvs.isPresent();
    }

    public boolean hasColors() {
        return colors.isPresent();
    }

    public boolean isRenderable() {
        return isRenderable;
    }

    public void putMeshArrays(MeshBuilder builder) {
        VertexFormat format = builder.getVertexFormat();
        int indexCount = indices.size();

        if (positions.isEmpty() || indexCount == 0)
            return;

        for (int i = 0; i < indexCount; i++) {
            int index = indices.get(i);

            for (VertexAttribute attribute : format.getAttributes()) {
                switch (attribute.getUsage()) {
                case POSITION:
                    putPosition(builder, index);
                    break;
                case UV:
                    putUV(builder, index);
                    break;
                case NORMAL:
                    putNormal(builder, index);
                    break;
                case COLOR:
                    putColor(builder, index);
                    break;
                default:
                    break;
                }
            }
        }
    }

    public void putMeshElements(MeshBuilder builder) {
        VertexFormat format = builder.getVertexFormat();

        if (positions.isEmpty() || indices.isEmpty())
            return;

        int[] indexArray = indices.stream().mapToInt(i -> i).toArray();
        builder.index(indexArray);

        int numVertices = numVertices();

        for (int i = 0; i < numVertices; i++) {
            for (VertexAttribute attribute : format.getAttributes()) {
                switch (attribute.getUsage()) {
                case POSITION:
                    putPosition(builder, i);
                    break;
                case UV:
                    putUV(builder, i);
                    break;
                case NORMAL:
                    putNormal(builder, i);
                    break;
                case COLOR:
                    putColor(builder, i);
                    break;
                default:
                    break;
                }
            }
        }
    }

    public void makeRenderable(VertexFormat format) {
        if (isRenderable)
            return;

        MeshBuilder builder = new MeshBuilder(format);

        putMeshElements(builder);

        ByteBuffer vertexData = builder.getVertexData();
        ByteBuffer indexData = builder.getIndexData();

        glVAO = GL30.glGenVertexArrays();
        glVBO = GL30.glGenBuffers();
        glEBO = GL30.glGenBuffers();

        GL30.glBindVertexArray(glVAO);
        {
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, glVBO);
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexData, GL30.GL_STATIC_DRAW);
            GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, glEBO);
            GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, indexData, GL30.GL_STATIC_DRAW);

            format.apply();
        }
        GL30.glBindVertexArray(0);

        isRenderable = true;
    }

    public void render(int mode) {
        if (!isRenderable) {
            if (!hasWarnedAboutNotBeingRenderable) {
                CodeNodeMicrocontrollersClient.LOGGER.warn("Attempted to render mesh \"%s\" that is not renderable", name);
                hasWarnedAboutNotBeingRenderable = true;
            }
            return;
        }

        GL30.glBindVertexArray(glVAO);
        GL30.glDrawElements(mode, indices.size(), GL30.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
    }

    private void putPosition(MeshBuilder builder, int index) {
        index *= 3;
        builder.position(positions.get(index), positions.get(index + 1), positions.get(index + 2));
    }

    private void putUV(MeshBuilder builder, int index) {
        index *= 2;
        ArrayList<Float> uvs = this.uvs.orElse(null);

        if (uvs != null)
            builder.uv(uvs.get(index), uvs.get(index + 1));
        else
            builder.uv();
    }

    private void putNormal(MeshBuilder builder, int index) {
        index *= 3;
        builder.normal(normals.get(index), normals.get(index + 1), normals.get(index + 2));
    }

    private void putColor(MeshBuilder builder, int index) {
        index *= 4;
        ArrayList<Float> colors = this.colors.orElse(null);

        if (colors != null)
            builder.color(colors.get(index), colors.get(index + 1), colors.get(index + 2), colors.get(index + 3));
        else
            builder.color();
    }
}