package com.elmfer.cnmcu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elmfer.cnmcu.config.ModSetup;
import com.elmfer.cnmcu.network.Packets;
import com.elmfer.cnmcu.ui.IDEScreen;
import com.elmfer.cnmcu.ui.handler.ScreenHandlers;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class CodeNodeMicrocontrollersClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(CodeNodeMicrocontrollers.MOD_ID + "-client");
    
    @Override
    public void onInitializeClient() {
        ModSetup.imguiIniFile();
        
        HandledScreens.register(ScreenHandlers.IDE_SCREEN_HANDLER, IDEScreen::new);
        
        Packets.initClientPackets();
        
        EventHandler.registerClientEventHandlers();
    }
}
