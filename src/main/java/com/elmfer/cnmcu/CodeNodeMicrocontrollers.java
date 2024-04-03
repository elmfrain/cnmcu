package com.elmfer.cnmcu;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodeNodeMicrocontrollers implements ModInitializer {

    public static final String MOD_ID = "cnmcu";
    
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        LOGGER.info("Hello Fabric world!");
    }
    
    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}