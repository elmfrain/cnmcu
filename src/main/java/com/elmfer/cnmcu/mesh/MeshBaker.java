package com.elmfer.cnmcu.mesh;

import java.nio.ByteBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

public class MeshBaker {

    public static final Vector3f UP = new Vector3f(0, 1, 0);
    public static final Vector3f DOWN = new Vector3f(0, -1, 0);
    public static final Vector3f NORTH = new Vector3f(0, 0, -1);
    public static final Vector3f SOUTH = new Vector3f(0, 0, 1);
    public static final Vector3f WEST = new Vector3f(-1, 0, 0);
    public static final Vector3f EAST = new Vector3f(1, 0, 0);

    private static final float EPSILON = 0.03125f;

    public static QuadEmitter outputFromMesh(Mesh mesh, QuadEmitter emitter, Sprite sprite, Matrix4f transform) {

        MeshBuilder meshTransformer = new MeshBuilder(VertexFormat.POS_UV_COLOR_NORM);
        meshTransformer.getModelView().set(transform);
        mesh.putMeshElements(meshTransformer);

        return outputFromMeshBuilder(meshTransformer, emitter, sprite);
    }

    public static QuadEmitter outputFromMesh(Mesh mesh, QuadEmitter emitter, Sprite sprite) {
        int quadCount = mesh.indices.size() / 4;

        for (int i = 0; i < quadCount; i++) {
            Quad quad = new Quad(mesh, sprite, i);

            emitter.cullFace(quad.cull ? quad.dir : null);
            emitter.nominalFace(quad.dir);

            emitter.pos(0, quad.p1);
            emitter.pos(1, quad.p2);
            emitter.pos(2, quad.p3);
            emitter.pos(3, quad.p4);

            emitter.uv(0, quad.uv1);
            emitter.uv(1, quad.uv2);
            emitter.uv(2, quad.uv3);
            emitter.uv(3, quad.uv4);
            emitter.spriteBake(sprite, MutableQuadView.BAKE_NORMALIZED | MutableQuadView.BAKE_FLIP_V);

            emitter.normal(0, quad.n1);
            emitter.normal(1, quad.n2);
            emitter.normal(2, quad.n3);
            emitter.normal(3, quad.n4);

            emitter.color(quad.c1, quad.c2, quad.c3, quad.c4);

            emitter.emit();
        }

        return emitter;
    }

    public static QuadEmitter outputFromMeshBuilder(MeshBuilder meshBuilder, QuadEmitter emitter, Sprite sprite,
            Matrix4f transform) {
        if (meshBuilder.getVertexFormat() != VertexFormat.POS_UV_COLOR_NORM)
            throw new IllegalArgumentException("MeshBuilder must have a VertexFormat of POS_UV_COLOR_NORMAL");

        meshBuilder.getModelView().set(transform);

        int quadCount = meshBuilder.getIndexCount() / 4;

        for (int i = 0; i < quadCount; i++) {
            Quad quad = new Quad(meshBuilder, sprite, i);

            emitter.cullFace(quad.cull ? quad.dir : null);
            emitter.nominalFace(quad.dir);

            emitter.pos(0, quad.p1);
            emitter.pos(1, quad.p2);
            emitter.pos(2, quad.p3);
            emitter.pos(3, quad.p4);

            emitter.uv(0, quad.uv1);
            emitter.uv(1, quad.uv2);
            emitter.uv(2, quad.uv3);
            emitter.uv(3, quad.uv4);
            emitter.spriteBake(sprite, MutableQuadView.BAKE_NORMALIZED | MutableQuadView.BAKE_FLIP_V);

            emitter.normal(0, quad.n1);
            emitter.normal(1, quad.n2);
            emitter.normal(2, quad.n3);
            emitter.normal(3, quad.n4);

            emitter.color(quad.c1, quad.c2, quad.c3, quad.c4);

            emitter.emit();
        }

        return emitter;
    }

    public static QuadEmitter outputFromMeshBuilder(MeshBuilder meshBuilder, QuadEmitter emitter, Sprite sprite) {
        return outputFromMeshBuilder(meshBuilder, emitter, sprite, new Matrix4f());
    }

    // Returns the closest cardinal direction with respect to a normal vector
    // if the dot product of the normal vector and a cardinal direction is greater
    // than 0.96875.
    // Returns null if the normal vector is not close enough to any cardinal
    // direction
    public static Direction getCardinalDirection(Vector3f vec) {

        vec.normalize();

        float dotUp = UP.dot(vec);
        float dotDown = DOWN.dot(vec);
        float dotNorth = NORTH.dot(vec);
        float dotSouth = SOUTH.dot(vec);
        float dotWest = WEST.dot(vec);
        float dotEast = EAST.dot(vec);

        Direction closest = Direction.UP;

        float dotMax = Math.max(dotUp,
                Math.max(dotDown, Math.max(dotNorth, Math.max(dotSouth, Math.max(dotWest, dotEast)))));

        if (dotMax < 1.0f - EPSILON)
            return null;

        if (dotMax == dotUp)
            closest = Direction.UP;
        else if (dotMax == dotDown)
            closest = Direction.DOWN;
        else if (dotMax == dotNorth)
            closest = Direction.NORTH;
        else if (dotMax == dotSouth)
            closest = Direction.SOUTH;
        else if (dotMax == dotWest)
            closest = Direction.WEST;
        else if (dotMax == dotEast)
            closest = Direction.EAST;

        return closest;
    }

    // Returns true if a quad's center is within 0.03125 of a face of a block
    public static boolean shouldQuadCull(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, Direction dir) {
        if (dir == null)
            return false;

        Vector3f center = new Vector3f(p1).add(p2).add(p3).add(p4).mul(0.25f);

        switch (dir) {
        case UP:
            return center.y > 1.0f - EPSILON;
        case DOWN:
            return center.y < EPSILON;
        case NORTH:
            return center.z < EPSILON;
        case SOUTH:
            return center.z > 1.0f - EPSILON;
        case WEST:
            return center.x < EPSILON;
        case EAST:
            return center.x > 1.0f - EPSILON;
        }

        return false;
    }

    public static Vector3f getCardinalDirectionVector(Direction dir) {
        switch (dir) {
        case UP:
            return UP;
        case DOWN:
            return DOWN;
        case NORTH:
            return NORTH;
        case SOUTH:
            return SOUTH;
        case WEST:
            return WEST;
        case EAST:
            return EAST;
        default:
            return null;
        }
    }

    private static class Quad {
        public final Vector3f p1, p2, p3, p4;
        public final Vector3f n1, n2, n3, n4;
        public final Vector2f uv1, uv2, uv3, uv4;
        public final int c1, c2, c3, c4;
        public final Direction dir;
        public final boolean cull;

        public Quad(Mesh mesh, Sprite sprite, int index) {
            int idx = index * 4, id0 = mesh.indices.get(idx), id1 = mesh.indices.get(idx + 1),
                    id2 = mesh.indices.get(idx + 2), id3 = mesh.indices.get(idx + 3);

            p1 = new Vector3f(mesh.positions.get(id0 * 3), mesh.positions.get(id0 * 3 + 1),
                    mesh.positions.get(id0 * 3 + 2));
            p2 = new Vector3f(mesh.positions.get(id1 * 3), mesh.positions.get(id1 * 3 + 1),
                    mesh.positions.get(id1 * 3 + 2));
            p3 = new Vector3f(mesh.positions.get(id2 * 3), mesh.positions.get(id2 * 3 + 1),
                    mesh.positions.get(id2 * 3 + 2));
            p4 = new Vector3f(mesh.positions.get(id3 * 3), mesh.positions.get(id3 * 3 + 1),
                    mesh.positions.get(id3 * 3 + 2));

            n1 = new Vector3f(mesh.normals.get(id0 * 3), mesh.normals.get(id0 * 3 + 1), mesh.normals.get(id0 * 3 + 2));
            n2 = new Vector3f(mesh.normals.get(id1 * 3), mesh.normals.get(id1 * 3 + 1), mesh.normals.get(id1 * 3 + 2));
            n3 = new Vector3f(mesh.normals.get(id2 * 3), mesh.normals.get(id2 * 3 + 1), mesh.normals.get(id2 * 3 + 2));
            n4 = new Vector3f(mesh.normals.get(id3 * 3), mesh.normals.get(id3 * 3 + 1), mesh.normals.get(id3 * 3 + 2));

            uv1 = new Vector2f(mesh.uvs.get().get(id0 * 2), mesh.uvs.get().get(id0 * 2 + 1));
            uv2 = new Vector2f(mesh.uvs.get().get(id1 * 2), mesh.uvs.get().get(id1 * 2 + 1));
            uv3 = new Vector2f(mesh.uvs.get().get(id2 * 2), mesh.uvs.get().get(id2 * 2 + 1));
            uv4 = new Vector2f(mesh.uvs.get().get(id3 * 2), mesh.uvs.get().get(id3 * 2 + 1));

            Vector4f v4c1 = new Vector4f(mesh.colors.get().get(id0 * 4), mesh.colors.get().get(id0 * 4 + 1),
                    mesh.colors.get().get(id0 * 4 + 2), mesh.colors.get().get(id0 * 4 + 3));
            Vector4f v4c2 = new Vector4f(mesh.colors.get().get(id1 * 4), mesh.colors.get().get(id1 * 4 + 1),
                    mesh.colors.get().get(id1 * 4 + 2), mesh.colors.get().get(id1 * 4 + 3));
            Vector4f v4c3 = new Vector4f(mesh.colors.get().get(id2 * 4), mesh.colors.get().get(id2 * 4 + 1),
                    mesh.colors.get().get(id2 * 4 + 2), mesh.colors.get().get(id2 * 4 + 3));
            Vector4f v4c4 = new Vector4f(mesh.colors.get().get(id3 * 4), mesh.colors.get().get(id3 * 4 + 1),
                    mesh.colors.get().get(id3 * 4 + 2), mesh.colors.get().get(id3 * 4 + 3));

            c1 = (int) (v4c1.w * 255) << 24 | (int) (v4c1.x * 255) << 16 | (int) (v4c1.y * 255) << 8
                    | (int) (v4c1.z * 255);
            c2 = (int) (v4c2.w * 255) << 24 | (int) (v4c2.x * 255) << 16 | (int) (v4c2.y * 255) << 8
                    | (int) (v4c2.z * 255);
            c3 = (int) (v4c3.w * 255) << 24 | (int) (v4c3.x * 255) << 16 | (int) (v4c3.y * 255) << 8
                    | (int) (v4c3.z * 255);
            c4 = (int) (v4c4.w * 255) << 24 | (int) (v4c4.x * 255) << 16 | (int) (v4c4.y * 255) << 8
                    | (int) (v4c4.z * 255);

            dir = getCardinalDirection(new Vector3f(n1).add(n2).add(n3).add(n4));
            cull = shouldQuadCull(p1, p2, p3, p4, dir);
        }

        // Looks kinda ugly, but it's the only way to access the buffer without having
        // to
        // copy a bunch of data around
        public Quad(MeshBuilder builder, Sprite sprite, int index) {
            ByteBuffer indexBuffer = builder.getIndexData();
            ByteBuffer vertexBuffer = builder.getVertexData();
            int vertexSize = builder.getVertexFormat().getStride();

            int idx = index * 4;
            int i0 = indexBuffer.getInt(idx * 4);
            int i1 = indexBuffer.getInt((idx + 1) * 4);
            int i2 = indexBuffer.getInt((idx + 2) * 4);
            int i3 = indexBuffer.getInt((idx + 3) * 4);

            p1 = new Vector3f(vertexBuffer.getFloat(i0 * vertexSize), vertexBuffer.getFloat(i0 * vertexSize + 4),
                    vertexBuffer.getFloat(i0 * vertexSize + 8));
            p2 = new Vector3f(vertexBuffer.getFloat(i1 * vertexSize), vertexBuffer.getFloat(i1 * vertexSize + 4),
                    vertexBuffer.getFloat(i1 * vertexSize + 8));
            p3 = new Vector3f(vertexBuffer.getFloat(i2 * vertexSize), vertexBuffer.getFloat(i2 * vertexSize + 4),
                    vertexBuffer.getFloat(i2 * vertexSize + 8));
            p4 = new Vector3f(vertexBuffer.getFloat(i3 * vertexSize), vertexBuffer.getFloat(i3 * vertexSize + 4),
                    vertexBuffer.getFloat(i3 * vertexSize + 8));

            uv1 = new Vector2f(vertexBuffer.getFloat(i0 * vertexSize + 12),
                    vertexBuffer.getFloat(i0 * vertexSize + 16));
            uv2 = new Vector2f(vertexBuffer.getFloat(i1 * vertexSize + 12),
                    vertexBuffer.getFloat(i1 * vertexSize + 16));
            uv3 = new Vector2f(vertexBuffer.getFloat(i2 * vertexSize + 12),
                    vertexBuffer.getFloat(i2 * vertexSize + 16));
            uv4 = new Vector2f(vertexBuffer.getFloat(i3 * vertexSize + 12),
                    vertexBuffer.getFloat(i3 * vertexSize + 16));

            c1 = (vertexBuffer.get(i0 * vertexSize + 20) & 0xFF) << 24
                    | (vertexBuffer.get(i0 * vertexSize + 21) & 0xFF) << 16
                    | (vertexBuffer.get(i0 * vertexSize + 22) + 0xFF) << 8
                    | (vertexBuffer.get(i0 * vertexSize + 23) + 0xFF);
            c2 = (vertexBuffer.get(i1 * vertexSize + 20) & 0xFF) << 24
                    | (vertexBuffer.get(i1 * vertexSize + 21) & 0xFF) << 16
                    | (vertexBuffer.get(i1 * vertexSize + 22) + 0xFF) << 8
                    | (vertexBuffer.get(i1 * vertexSize + 23) + 0xFF);
            c3 = (vertexBuffer.get(i2 * vertexSize + 20) & 0xFF) << 24
                    | (vertexBuffer.get(i2 * vertexSize + 21) & 0xFF) << 16
                    | (vertexBuffer.get(i2 * vertexSize + 22) + 0xFF) << 8
                    | (vertexBuffer.get(i2 * vertexSize + 23) + 0xFF);
            c4 = (vertexBuffer.get(i3 * vertexSize + 20) & 0xFF) << 24
                    | (vertexBuffer.get(i3 * vertexSize + 21) & 0xFF) << 16
                    | (vertexBuffer.get(i3 * vertexSize + 22) + 0xFF) << 8
                    | (vertexBuffer.get(i3 * vertexSize + 23) + 0xFF);

            n1 = new Vector3f(vertexBuffer.getFloat(i0 * vertexSize + 24), vertexBuffer.getFloat(i0 * vertexSize + 28),
                    vertexBuffer.getFloat(i0 * vertexSize + 32));
            n2 = new Vector3f(vertexBuffer.getFloat(i1 * vertexSize + 24), vertexBuffer.getFloat(i1 * vertexSize + 28),
                    vertexBuffer.getFloat(i1 * vertexSize + 32));
            n3 = new Vector3f(vertexBuffer.getFloat(i2 * vertexSize + 24), vertexBuffer.getFloat(i2 * vertexSize + 28),
                    vertexBuffer.getFloat(i2 * vertexSize + 32));
            n4 = new Vector3f(vertexBuffer.getFloat(i3 * vertexSize + 24), vertexBuffer.getFloat(i3 * vertexSize + 28),
                    vertexBuffer.getFloat(i3 * vertexSize + 32));

            dir = getCardinalDirection(new Vector3f(n1).add(n2).add(n3).add(n4));
            cull = shouldQuadCull(p1, p2, p3, p4, dir);
        }
    }
}