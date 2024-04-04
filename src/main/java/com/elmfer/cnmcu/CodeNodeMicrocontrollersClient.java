package com.elmfer.cnmcu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elmfer.cnmcu.model.CodeNodeModelLoadingPlugin;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

public class CodeNodeMicrocontrollersClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(CodeNodeMicrocontrollers.MOD_ID + "-client");
    
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(new CodeNodeModelLoadingPlugin());
    }
}
