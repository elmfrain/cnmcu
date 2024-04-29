package com.elmfer.cnmcu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elmfer.cnmcu.blockentities.BlockEntities;
import com.elmfer.cnmcu.blocks.Blocks;
import com.elmfer.cnmcu.cpp.NativesLoader;
import com.elmfer.cnmcu.network.Packets;
import com.elmfer.cnmcu.ui.handler.ScreenHandlers;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class CodeNodeMicrocontrollers implements ModInitializer {

    public static final String MOD_NAME = "CodeNode Microcontrollers";
    public static final String MOD_ID = "cnmcu";
    
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        NativesLoader.loadNatives();
        
        Blocks.init();
        BlockEntities.init();
        ScreenHandlers.init();
        
        Packets.initServerPackets();
    }
    
    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}