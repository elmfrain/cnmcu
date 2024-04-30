package com.elmfer.cnmcu.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;
import com.elmfer.cnmcu.cpp.NativesLoader;
import com.elmfer.cnmcu.mcu.Toolchain;
import com.elmfer.cnmcu.util.HTTPSFetcher;
import com.google.gson.JsonArray;

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

        if (Files.exists(Paths.get(IMGUI_INI_FILE)))
            return;

        try {
            InputStream imguiIni = MinecraftClient.getInstance().getResourceManager().getResource(imguiIniId).get()
                    .getInputStream();
            Files.copy(imguiIni, Paths.get(IMGUI_INI_FILE));

            imguiIni.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void downloadNatives() {
        if (Files.exists(Paths.get(NativesLoader.BINARIES_PATH + "/" + NativesLoader.getBinaryFilename()))) {
            CodeNodeMicrocontrollers.LOGGER.info("Native library is already installed! Skipping download...");
            return;
        }
        
        CodeNodeMicrocontrollers.LOGGER.info("Native library not installed! Downloading...");
        
        byte rawBinary[] = getGitHubBinaryAsset();
        
        if (rawBinary == null)
            throw new RuntimeException("Failed to download native library!");
        
        try {
            Files.write(Paths.get(NativesLoader.BINARIES_PATH + "/" + NativesLoader.getBinaryFilename()), rawBinary);
        } catch (IOException e) {
           throw new RuntimeException("Failed to write native library to disk!", e);
        }
    }

    private static byte[] getGitHubBinaryAsset() {
        CodeNodeMicrocontrollers.LOGGER.info("Downloading native library... {}", NativesLoader.getBinaryFilename());
        
        HTTPSFetcher fetcher = new HTTPSFetcher(
                GITHUB_REPO_URL + "/releases/tags/" + CodeNodeMicrocontrollers.MOD_VERSION);
        fetcher.addHeader("Accept", "application/vnd.github.v3+json");
        fetcher.start();
        fetcher.waitForCompletion();

        if (fetcher.statusCode() == 0)
            throw new RuntimeException("Failed to connect to GitHub API! Check your internet connection.");
        
        if (fetcher.statusCode() != 200)
            return null;

        String assetDownloadUrl = null;
        
        try {
            JsonArray assets = fetcher.jsonContent().get("assets").getAsJsonArray();
            
            for (int i = 0; i < assets.size(); i++) {
                String assetName = assets.get(i).getAsJsonObject().get("name").getAsString();
                if (assetName.equals(NativesLoader.getBinaryFilename())) {
                    assetDownloadUrl = assets.get(i).getAsJsonObject().get("url").getAsString();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        if (assetDownloadUrl == null)
            return null;
        
        fetcher = new HTTPSFetcher(assetDownloadUrl);
        fetcher.addHeader("Accept", "application/octet-stream");
        fetcher.start();
        fetcher.waitForCompletion();
        
        if (fetcher.statusCode() != 200)
            return null;

        return fetcher.byteContent();
    }
}
