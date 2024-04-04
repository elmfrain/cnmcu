package com.elmfer.cnmcu.blocks;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class Blocks {
    public static final CNnanoBlock CN_NANO_BLOCK = new CNnanoBlock(FabricBlockSettings.create().strength(0.5f));
    
    public static <T extends Block> T register(String name, T block) {
        T b = Registry.register(Registries.BLOCK, CodeNodeMicrocontrollers.id(name), block);
        BlockItem item = new BlockItem(b, new FabricItemSettings());
        item.appendBlocks(Item.BLOCK_ITEMS, item);
        Registry.register(Registries.ITEM, CodeNodeMicrocontrollers.id(name), item);
        return b;
    }
    
    public static void init() {
        register("nano", CN_NANO_BLOCK);
    }

}
