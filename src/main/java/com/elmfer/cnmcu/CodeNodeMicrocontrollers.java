package com.elmfer.cnmcu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elmfer.cnmcu.blockentities.BlockEntities;
import com.elmfer.cnmcu.blocks.Blocks;
import com.elmfer.cnmcu.config.Config;
import com.elmfer.cnmcu.config.ModSetup;
import com.elmfer.cnmcu.cpp.NativesLoader;
import com.elmfer.cnmcu.network.Packets;
import com.elmfer.cnmcu.ui.handler.ScreenHandlers;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class CodeNodeMicrocontrollers implements ModInitializer {

    public static final String MOD_NAME = "CodeNode Microcontrollers";
    public static final String MOD_ID = "cnmcu";
    public static final String MOD_VERSION = "0.0.9a-1.20.4";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModSetup.createDirectories();
        ModSetup.downloadNatives();
        ModSetup.downloadToolchain();

        checkForUpdates();

        NativesLoader.loadNatives();

        Blocks.init();
        BlockEntities.init();
        ScreenHandlers.init();

        Packets.initServerPackets();
    }

    public static void checkForUpdates() {
        if (!Config.adviseUpdates())
            return;

        ModSetup.checkForUpdates();

        if (ModSetup.isUpdateAvailable()) {
            String latestForMCVersions = String.join(", ", ModSetup.getLatestForMinecraftVersions());
            LOGGER.info("An update is available for CodeNode Microcontrollers: " + ModSetup.getLatestVersion()
                    + " for Minecraft " + latestForMCVersions);
        } else if (!ModSetup.wasAbleToCheckForUpdates()) {
            LOGGER.info("CodeNode Microcontrollers was unable to check for updates.");
        } else {
            LOGGER.info("CodeNode Microcontrollers is up to date.");
        }
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}