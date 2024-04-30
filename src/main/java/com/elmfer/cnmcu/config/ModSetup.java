package com.elmfer.cnmcu.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;
import com.elmfer.cnmcu.cpp.NativesLoader;
import com.elmfer.cnmcu.mcu.Toolchain;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class ModSetup {
    
    public static final String IMGUI_INI_FILE = CodeNodeMicrocontrollers.MOD_ID + "/imgui.ini";
    
    private static final String GITHUB_REPO_URL = "https://api.github.com/repos/elmfrain/cnmcu";
    
    private ModSetup() {
    }
    
    public static void createDirectories() {
        try {
            Files.createDirectories(Paths.get(Toolchain.TOOLCHAIN_PATH));
            Files.createDirectories(Paths.get(NativesLoader.BINARIES_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void imguiIniFile() {
        final Identifier imguiIniId = CodeNodeMicrocontrollers.id("setup/imgui.ini");
        
        if(Files.exists(Paths.get(IMGUI_INI_FILE)))
            return;
        
        try {
            InputStream imguiIni = MinecraftClient.getInstance().getResourceManager().getResource(imguiIniId).get().getInputStream();
            Files.copy(imguiIni, Paths.get(IMGUI_INI_FILE));
            
            imguiIni.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void downloadNatives() {
        
    }
    
    private static long getGitHubReleaseTag() {
        return 0;
    }
}
