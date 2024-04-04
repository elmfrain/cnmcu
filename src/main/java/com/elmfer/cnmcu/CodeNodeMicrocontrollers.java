package com.elmfer.cnmcu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elmfer.cnmcu.blockentities.BlockEntities;
import com.elmfer.cnmcu.blocks.Blocks;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class CodeNodeMicrocontrollers implements ModInitializer {

    public static final String MOD_ID = "cnmcu";
    
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        Blocks.init();
        BlockEntities.init();
    }
    
    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}