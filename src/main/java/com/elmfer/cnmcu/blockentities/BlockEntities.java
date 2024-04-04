package com.elmfer.cnmcu.blockentities;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;
import com.elmfer.cnmcu.blocks.Blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BlockEntities {
    public static void init() {
        
    }
    
    public static final BlockEntityType<CNnanoBlockEntity> CN_NANO = Registry.register(Registries.BLOCK_ENTITY_TYPE,
            CodeNodeMicrocontrollers.id("nano"),
            FabricBlockEntityTypeBuilder.create(CNnanoBlockEntity::new, Blocks.CN_NANO_BLOCK).build(null));
}
