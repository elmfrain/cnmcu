package com.elmfer.cnmcu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;

public class CodeNodeMicrocontrollersClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(CodeNodeMicrocontrollers.MOD_ID + "-client");
    
    @Override
    public void onInitializeClient() {
        CodeNodeMicrocontrollers.LOGGER.info("Hello Fabric world! (Client)");
    }
}
