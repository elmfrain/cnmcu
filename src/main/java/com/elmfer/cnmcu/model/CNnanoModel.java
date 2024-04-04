package com.elmfer.cnmcu.model;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.joml.Matrix4f;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;
import com.elmfer.cnmcu.blocks.CNnanoBlock;
import com.elmfer.cnmcu.mesh.MeshBaker;
import com.elmfer.cnmcu.mesh.Meshes;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

public class CNnanoModel implements UnbakedModel, BakedModel, FabricBakedModel {

    @SuppressWarnings("deprecation")
    private static final SpriteIdentifier SPRITE_PCB = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
            CodeNodeMicrocontrollers.id("block/nano_pcb"));

    @SuppressWarnings("deprecation")
    private static final SpriteIdentifier SPRITE_SMD = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,
            CodeNodeMicrocontrollers.id("block/nano_smd"));

    private static Sprite particleSprite;

    private boolean hasBaked = false;
    private Mesh northMesh;
    private Mesh southMesh;
    private Mesh westMesh;
    private Mesh eastMesh;

    @Override
    public List<BakedQuad> getQuads(BlockState var1, Direction var2, Random var3) {
        return List.of();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return true;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return particleSprite;
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelHelper.MODEL_TRANSFORM_BLOCK;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return List.of();
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> var1) {
    }

    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> textureGetter,
            ModelBakeSettings rotationContainer, Identifier modelID) {
        if (hasBaked) {
            return this;
        }

        Sprite pcbSprite = particleSprite = textureGetter.apply(SPRITE_PCB);
        Sprite smdSprite = textureGetter.apply(SPRITE_SMD);

        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        MeshBuilder builder = renderer.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();

        com.elmfer.cnmcu.mesh.Mesh boardMesh = Meshes
                .load(CodeNodeMicrocontrollers.id("meshes/nano_pcb.ply"));
        com.elmfer.cnmcu.mesh.Mesh smdMesh = Meshes
                .load(CodeNodeMicrocontrollers.id("meshes/nano_smd.ply"));

        MeshBaker.outputFromMesh(boardMesh, emitter, pcbSprite);
        MeshBaker.outputFromMesh(smdMesh, emitter, smdSprite);
        northMesh = builder.build();

        Matrix4f transform = new Matrix4f().translate(0.5f, 0, 0.5f).rotate((float) -Math.PI / 2, 0, 1, 0)
                .translate(-0.5f, 0, -0.5f);
        MeshBaker.outputFromMesh(boardMesh, emitter, pcbSprite, transform);
        MeshBaker.outputFromMesh(smdMesh, emitter, smdSprite, transform);
        eastMesh = builder.build();

        transform.identity().translate(0.5f, 0, 0.5f).rotate((float) Math.PI, 0, 1, 0).translate(-0.5f, 0, -0.5f);
        MeshBaker.outputFromMesh(boardMesh, emitter, pcbSprite, transform);
        MeshBaker.outputFromMesh(smdMesh, emitter, smdSprite, transform);
        southMesh = builder.build();
        
        transform.identity().translate(0.5f, 0, 0.5f).rotate((float) Math.PI / 2, 0, 1, 0).translate(-0.5f, 0, -0.5f);
        MeshBaker.outputFromMesh(boardMesh, emitter, pcbSprite, transform);
        MeshBaker.outputFromMesh(smdMesh, emitter, smdSprite, transform);
        westMesh = builder.build();

        hasBaked = true;
        return this;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockRenderView, BlockState blockState, BlockPos blockPos,
            Supplier<Random> supplier, RenderContext context) {
        Direction direction = blockState.get(CNnanoBlock.FACING);

        switch (direction) {
        case NORTH:
            northMesh.outputTo(context.getEmitter());
            break;
        case SOUTH:
            southMesh.outputTo(context.getEmitter());
            break;
        case WEST:
            westMesh.outputTo(context.getEmitter());
            break;
        case EAST:
            eastMesh.outputTo(context.getEmitter());
            break;
        default:
            northMesh.outputTo(context.getEmitter());
            break;
        }
    }

    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<Random> supplier, RenderContext renderContext) {
        northMesh.outputTo(renderContext.getEmitter());
    }
}
