package com.elmfer.cnmcu.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

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

    private static JsonArray githubAssets;

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
        ensureInstall("Native library", Paths.get(NativesLoader.BINARIES_PATH, NativesLoader.getBinaryFilename()),
                NativesLoader.getBinaryFilename());
    }

    public static void downloadToolchain() {
        final String vasmFilename = "vasm6502_oldstyle";

        ensureInstall("vasm", Paths.get(Toolchain.TOOLCHAIN_PATH, vasmFilename + NativesLoader.EXE_EXT),
                NativesLoader.getExecutableFilename(vasmFilename));

        final String vobjFilename = "vobjdump";
        
        ensureInstall("vobjdump", Paths.get(Toolchain.TOOLCHAIN_PATH, vobjFilename + NativesLoader.EXE_EXT),
                NativesLoader.getExecutableFilename(vobjFilename));

        final String cygFilename = "cygwin1.dll";

        if (NativesLoader.NATIVES_OS.equals("windows")) {
            ensureInstall("cygwin1.dll", Paths.get(Toolchain.TOOLCHAIN_PATH, cygFilename),
                    "cygwin1.dll");
        }
    }

    private static byte[] getGitHubAsset(String assetNameTarget) {
        CodeNodeMicrocontrollers.LOGGER.info("Downloading asset from GitHub... {}", assetNameTarget);

        getGitHubAssets();

        String assetDownloadUrl = null;

        try {
            JsonArray assets = ModSetup.githubAssets;

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

        HTTPSFetcher fetcher = new HTTPSFetcher(assetDownloadUrl);
        fetcher.addHeader("Accept", "application/octet-stream");
        fetcher.start();
        fetcher.waitForCompletion();

        if (fetcher.statusCode() != 200)
            return null;

        return fetcher.byteContent();
    }

    private static void getGitHubAssets() {
        if (githubAssets != null)
            return;

        CodeNodeMicrocontrollers.LOGGER.info("Listing assets from GitHub...");

        HTTPSFetcher fetcher = new HTTPSFetcher(
                GITHUB_REPO_URL + "/releases/tags/" + CodeNodeMicrocontrollers.MOD_VERSION);
        fetcher.addHeader("Accept", "application/vnd.github.v3+json");
        fetcher.start();
        fetcher.waitForCompletion();

        if (fetcher.statusCode() == 0)
            throw new RuntimeException("Failed to connect to GitHub API! Check your internet connection.");

        if (fetcher.statusCode() != 200)
            return;

        try {
            githubAssets = fetcher.jsonContent().get("assets").getAsJsonArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse assets from GitHub!", e);
        }
    }

    private static void ensureInstall(String moduleName, Path localPath, String assetName) {
        if (Files.exists(localPath)) {
            CodeNodeMicrocontrollers.LOGGER.info("{} is already installed! Skipping download...", moduleName);
            return;
        }

        CodeNodeMicrocontrollers.LOGGER.info("{} is not installed! Downloading...", moduleName);

        byte rawBinary[] = getGitHubAsset(assetName);

        if (rawBinary == null)
            throw new RuntimeException("Failed to download " + moduleName + "!");

        try {
            if (NativesLoader.NATIVES_OS != "windows") {
                Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
                FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
                Files.createFile(localPath, attr);
            }
            
            Files.write(localPath, rawBinary);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write " + moduleName + " to disk!", e);
        }
    }
}
