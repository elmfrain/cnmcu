package com.elmfer.cnmcu.mesh;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.joml.Matrix3f;
import org.joml.Matrix4fStack;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class MeshBuilder {
    private final VertexFormat vertexFormat;
    private final Matrix4fStack modelViewMatrix;
    private Matrix3f modelView3x3;

    private int glVBO;
    private int glEBO;
    private int glVAO;
    private int glVertexBufferSize = 256;
    private int glElementBufferSize = 256;

    private int numVerticies = 0;
    private int numIndicies = 0;

    private ByteArrayOutputStream vertexDataStream;
    private ByteArrayOutputStream indexDataStream;
    private ByteBuffer attribBuilder;

    private boolean isRenderable = false;

    private Vector4f colorModulator = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    public MeshBuilder(VertexFormat format) {
        this.vertexFormat = format;

        vertexDataStream = new ByteArrayOutputStream();
        indexDataStream = new ByteArrayOutputStream();
        attribBuilder = ByteBuffer.allocate(256).order(ByteOrder.nativeOrder());

        modelViewMatrix = new Matrix4fStack(128);
        modelView3x3 = new Matrix3f();
    }

    public void reset() {
        vertexDataStream.reset();
        indexDataStream.reset();
        numVerticies = 0;
        numIndicies = 0;
    }

    public void drawArrays(int mode) {
        if (!isRenderable)
            initForRendering();

        ByteBuffer vertexData = BufferUtils.createByteBuffer(vertexDataStream.size());
        vertexData.put(vertexDataStream.toByteArray());
        vertexData.flip();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glVBO);
        if (vertexData.capacity() <= glVertexBufferSize)
            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertexData);
        else {
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_DYNAMIC_DRAW);
            glVertexBufferSize = vertexData.capacity();
        }

        GL30.glBindVertexArray(glVAO);
        GL15.glDrawArrays(mode, 0, numVerticies);
        GL30.glBindVertexArray(0);
    }

    public void drawElements(int mode) {
        if (!isRenderable)
            initForRendering();

        ByteBuffer vertexData = BufferUtils.createByteBuffer(vertexDataStream.size());
        ByteBuffer indexData = BufferUtils.createByteBuffer(indexDataStream.size());
        vertexData.put(vertexDataStream.toByteArray());
        indexData.put(indexDataStream.toByteArray());
        vertexData.flip();
        indexData.flip();

        int lastVertexArray = GL30.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        int lastArrayBuffer = GL15.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
        int lastElementBuffer = GL15.glGetInteger(GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glVBO);
        if (vertexData.capacity() <= glVertexBufferSize)
            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertexData);
        else {
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_DYNAMIC_DRAW);
            glVertexBufferSize = vertexData.capacity();
        }

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, glEBO);
        if (indexData.capacity() <= glElementBufferSize)
            GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, indexData);
        else {
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexData, GL15.GL_DYNAMIC_DRAW);
            glElementBufferSize = indexData.capacity();
        }

        GL30.glBindVertexArray(glVAO);
        GL15.glDrawElements(mode, numIndicies, GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(lastVertexArray);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, lastArrayBuffer);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, lastElementBuffer);
    }

    public MeshBuilder position(float x, float y, float z) {
        Vector4f pos = new Vector4f(x, y, z, 1.0f);
        pos.mul(modelViewMatrix);

        attribBuilder.clear();
        attribBuilder.putFloat(pos.x).putFloat(pos.y).putFloat(pos.z);
        attribBuilder.flip();

        vertexDataStream.write(attribBuilder.array(), 0, attribBuilder.limit());
        numVerticies++;

        return this;
    }

    public MeshBuilder normal(float x, float y, float z) {
        Vector3f normal = new Vector3f(x, y, z);

        modelViewMatrix.get3x3(modelView3x3);

        normal.mul(modelView3x3);
        normal.normalize();

        attribBuilder.clear();
        attribBuilder.putFloat(normal.x).putFloat(normal.y).putFloat(normal.z);
        attribBuilder.flip();

        vertexDataStream.write(attribBuilder.array(), 0, attribBuilder.limit());

        return this;
    }

    public MeshBuilder normal() {
        return normal(0.0f, 1.0f, 0.0f);
    }

    public MeshBuilder uv(float u, float v) {
        attribBuilder.clear();
        attribBuilder.putFloat(u).putFloat(v);
        attribBuilder.flip();

        vertexDataStream.write(attribBuilder.array(), 0, attribBuilder.limit());

        return this;
    }

    public MeshBuilder uv() {
        return uv(0.0f, 0.0f);
    }

    public MeshBuilder color(float r, float g, float b, float a) {
        r *= colorModulator.x;
        g *= colorModulator.y;
        b *= colorModulator.z;
        a *= colorModulator.w;

        attribBuilder.clear();
        attribBuilder.put((byte) (r * 255)).put((byte) (g * 255)).put((byte) (b * 255)).put((byte) (a * 255));
        attribBuilder.flip();

        vertexDataStream.write(attribBuilder.array(), 0, attribBuilder.limit());

        return this;
    }

    public MeshBuilder color(float r, float g, float b) {
        return color(r, g, b, 1.0f);
    }

    public MeshBuilder color() {
        return color(colorModulator.x, colorModulator.y, colorModulator.z, colorModulator.w);
    }

    public MeshBuilder texid(int id) {
        vertexDataStream.write(id);

        return this;
    }

    /** Use this method first before generating verticies */
    public MeshBuilder index(int... indicies) {
        ByteBuffer indexData = ByteBuffer.allocate(indicies.length * Integer.BYTES).order(ByteOrder.nativeOrder());
        for (int i : indicies)
            indexData.putInt(i + numVerticies);
        indexData.flip();

        indexDataStream.write(indexData.array(), 0, indexData.limit());
        numIndicies += indicies.length;
        return this;
    }

    public VertexFormat getVertexFormat() {
        return vertexFormat;
    }

    public void pushMatrix() {
        modelViewMatrix.pushMatrix();
    }

    public void popMatrix() {
        modelViewMatrix.popMatrix();
    }

    public Matrix4fStack getModelView() {
        return modelViewMatrix;
    }

    public ByteBuffer getVertexData() {
        ByteBuffer vertexData = BufferUtils.createByteBuffer(vertexDataStream.size());
        vertexData.put(vertexDataStream.toByteArray());
        vertexData.flip();

        return vertexData;
    }

    public ByteBuffer getIndexData() {
        ByteBuffer indexData = BufferUtils.createByteBuffer(indexDataStream.size());
        indexData.put(indexDataStream.toByteArray());
        indexData.flip();

        return indexData;
    }

    public int getVertexCount() {
        return numVerticies;
    }

    public int getIndexCount() {
        return numIndicies;
    }

    public void setColorModulator(Vector4f color) {
        colorModulator.set(color);
    }

    public void setColorModulator(float r, float g, float b, float a) {
        colorModulator.set(r, g, b, a);
    }

    public void setColorModulator(float r, float g, float b) {
        colorModulator.set(r, g, b, 1.0f);
    }

    public Vector4f getColorModulator() {
        return colorModulator;
    }

    private void initForRendering() {
        if (isRenderable)
            return;

        glVAO = GL30.glGenVertexArrays();
        glVBO = GL15.glGenBuffers();
        glEBO = GL15.glGenBuffers();

        glVertexBufferSize = vertexDataStream.size();
        glElementBufferSize = indexDataStream.size();

        int lastVertexArray = GL30.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        int lastArrayBuffer = GL15.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
        int lastElementBuffer = GL15.glGetInteger(GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING);
        GL30.glBindVertexArray(glVAO);
        {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, glVBO);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, glVertexBufferSize, GL15.GL_DYNAMIC_DRAW);
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, glEBO);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, glElementBufferSize, GL15.GL_DYNAMIC_DRAW);

            vertexFormat.apply();
        }
        GL30.glBindVertexArray(lastVertexArray);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, lastArrayBuffer);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, lastElementBuffer);

        isRenderable = true;
    }
}