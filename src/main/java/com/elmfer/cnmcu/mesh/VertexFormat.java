package com.elmfer.cnmcu.mesh;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public final class VertexFormat {
    public static final VertexFormat POS = new VertexFormat();
    public static final VertexFormat POS_UV = new VertexFormat();
    public static final VertexFormat POS_COLOR = new VertexFormat();
    public static final VertexFormat POS_UV_COLOR = new VertexFormat();
    public static final VertexFormat POS_UV_COLOR_TEXID = new VertexFormat();
    public static final VertexFormat POS_UV_COLOR_NORM = new VertexFormat();

    static {
        POS.addAttrib(AttribUsage.POSITION, AttribType.FLOAT, 3);

        POS_UV.addAttrib(AttribUsage.POSITION, AttribType.FLOAT, 3).addAttrib(AttribUsage.UV, AttribType.FLOAT, 2);

        POS_COLOR.addAttrib(AttribUsage.POSITION, AttribType.FLOAT, 3).addAttrib(AttribUsage.COLOR,
                AttribType.UNSIGNED_BYTE, 4, true);

        POS_UV_COLOR.addAttrib(AttribUsage.POSITION, AttribType.FLOAT, 3).addAttrib(AttribUsage.UV, AttribType.FLOAT, 2)
                .addAttrib(AttribUsage.COLOR, AttribType.UNSIGNED_BYTE, 4, true);

        POS_UV_COLOR_TEXID.addAttrib(AttribUsage.POSITION, AttribType.FLOAT, 3)
                .addAttrib(AttribUsage.UV, AttribType.FLOAT, 2)
                .addAttrib(AttribUsage.COLOR, AttribType.UNSIGNED_BYTE, 4, true)
                .addAttrib(AttribUsage.TEXID, AttribType.UNSIGNED_BYTE, 1, false);

        POS_UV_COLOR_NORM.addAttrib(AttribUsage.POSITION, AttribType.FLOAT, 3)
                .addAttrib(AttribUsage.UV, AttribType.FLOAT, 2)
                .addAttrib(AttribUsage.COLOR, AttribType.UNSIGNED_BYTE, 4, true)
                .addAttrib(AttribUsage.NORMAL, AttribType.FLOAT, 3);
    }

    private final List<VertexAttribute> attributes = new ArrayList<VertexAttribute>();

    public List<VertexAttribute> getAttributes() {
        return attributes;
    }

    public VertexFormat addAttrib(AttribUsage usage, AttribType type, int size) {
        attributes.add(new VertexAttribute(usage, type, size));
        return this;
    }

    public VertexFormat addAttrib(AttribUsage usage, AttribType type, int size, boolean normalized) {
        attributes.add(new VertexAttribute(usage, type, size, normalized));
        return this;
    }

    public void apply() {
        int stride = 0;
        for (VertexAttribute attrib : attributes)
            stride += attrib.numBytes();

        long pointer = 0;
        for (int i = 0; i < attributes.size(); i++) {
            VertexAttribute attrib = attributes.get(i);
            GL20.glEnableVertexAttribArray(i);
            GL20.glVertexAttribPointer(i, attrib.size, attrib.type.glType, attrib.normalized, stride, pointer);
            pointer += attrib.numBytes();
        }
    }

    public void unapply() {
        for (int i = 0; i < attributes.size(); i++)
            GL20.glDisableVertexAttribArray(i);

    }

    public int getStride() {
        int stride = 0;
        for (VertexAttribute attrib : attributes)
            stride += attrib.numBytes();
        return stride;
    }

    public static enum AttribUsage {
        POSITION, UV, COLOR, NORMAL, TEXID, OTHER
    }

    public static enum AttribType {
        FLOAT(GL11.GL_FLOAT, 4), INT(GL11.GL_INT, 4), UNSIGNED_INT(GL11.GL_UNSIGNED_INT, 4), SHORT(GL11.GL_SHORT, 2),
        UNSIGNED_SHORT(GL11.GL_UNSIGNED_SHORT, 2), BYTE(GL11.GL_BYTE, 1), UNSIGNED_BYTE(GL11.GL_UNSIGNED_BYTE, 1);

        private final int glType;
        private final int bytes;

        private AttribType(int glType, int bytes) {
            this.glType = glType;
            this.bytes = bytes;
        }

        public int getGLType() {
            return glType;
        }

        public int numBytes() {
            return bytes;
        }
    }

    public static class VertexAttribute {
        private final AttribUsage usage;
        private final AttribType type;
        private final int size;
        private boolean normalized = false;

        public VertexAttribute(AttribUsage usage, AttribType type, int size) {
            this.usage = usage;
            this.type = type;
            this.size = size;
        }

        public VertexAttribute(AttribUsage usage, AttribType type, int size, boolean normalized) {
            this(usage, type, size);
            this.normalized = normalized;
        }

        public AttribUsage getUsage() {
            return usage;
        }

        public AttribType getType() {
            return type;
        }

        public int getSize() {
            return size;
        }

        public int numBytes() {
            return size * type.bytes;
        }

        public boolean isNormalized() {
            return normalized;
        }

        public VertexAttribute setNormalized(boolean normalized) {
            this.normalized = normalized;
            return this;
        }
    }
}