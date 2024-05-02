package com.elmfer.cnmcu.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;
import com.elmfer.cnmcu.cpp.NativesLoader;
import com.elmfer.cnmcu.mcu.Toolchain;
import com.elmfer.cnmcu.util.HTTPSFetcher;
import com.elmfer.cnmcu.util.ResourceLoader;
import com.google.gson.JsonArray;

import net.minecraft.util.Identifier;

public class ModSetup {

    public static final String IMGUI_INI_FILE = CodeNodeMicrocontrollers.MOD_ID + "/imgui.ini";

    private static final String GITHUB_REPO_URL = "https://api.github.com/repos/elmfrain/cnmcu";
    private static final Set<String> LATEST_FOR_MINECRAFT_VERSIONS = new HashSet<>();

    private static JsonArray githubAssets;
    private static String latestVersion = CodeNodeMicrocontrollers.MOD_VERSION.split("-")[0];
    private static String changelog = "No changelog available";
    private static boolean updateAvailable = false;
    private static boolean hasCheckedForUpdates = false;
    private static boolean wasAbleToConnect = true;
    
    static {
        LATEST_FOR_MINECRAFT_VERSIONS.add(CodeNodeMicrocontrollers.MOD_VERSION.split("-")[1]);
    }
    
    private ModSetup() {
    }

    public static String getLatestVersion() {
        return latestVersion;
    }
    
    /**
     * Returns the Minecraft versions the latest release of the mod is available for.
     * @return
     */
    public static String[] getLatestForMinecraftVersions() {
        return LATEST_FOR_MINECRAFT_VERSIONS.toArray(new String[0]);
    }
    
    public static String getChangelog() {
        return changelog;
    }
    
    public static boolean isUpdateAvailable() {
        return updateAvailable;
    }
    
    public static CompletableFuture<Void> checkForUpdatesAsync() {
        if (hasCheckedForUpdates)
            return CompletableFuture.completedFuture(null);
        
        return CompletableFuture.runAsync(() -> checkForUpdates());
    }
    
    public static boolean hasCheckedForUpdates() {
        return hasCheckedForUpdates;
    }
    
    public static boolean wasAbleToCheckForUpdates() {
        return wasAbleToConnect;
    }
    
    public static void checkForUpdates() {
        if (hasCheckedForUpdates)
            return;
        
        HTTPSFetcher fetcher = new HTTPSFetcher(GITHUB_REPO_URL + "/releases");
        fetcher.addHeader("Accept", "application/vnd.github.v3+json");
        fetcher.start();
        fetcher.waitForCompletion();

        if (fetcher.statusCode() != 200) {
            wasAbleToConnect = false;
            hasCheckedForUpdates = true;
            return;
        }
        
        long currentModVersion = numberizeVersion(CodeNodeMicrocontrollers.MOD_VERSION.split("-")[0]);
        long latestVersionId = currentModVersion;
        String latestVersion = ModSetup.latestVersion;
        int latestIndex = -1;
        
        try {
            JsonArray releases = fetcher.jsonContent().getAsJsonArray();
            String[] releaseVersion = null;
            for (int i = 0; i < releases.size(); i++) {
                releaseVersion = releases.get(i).getAsJsonObject().get("tag_name").getAsString().split("-");
                if (isMinecraftSnapshot(releaseVersion[1]))
                    continue;
                
                long versionNumber = numberizeVersion(releaseVersion[0]);
                
                if (Long.compareUnsigned(versionNumber, latestVersionId) < 0)
                    continue;
                else if(Long.compareUnsigned(versionNumber, latestVersionId) > 0)
                    LATEST_FOR_MINECRAFT_VERSIONS.clear();
                
                updateAvailable = true;
                LATEST_FOR_MINECRAFT_VERSIONS.add(releaseVersion[1]);
                
                latestVersionId = versionNumber;
                latestVersion = releaseVersion[0];
                latestIndex = i;
            }
            
            if (latestIndex == -1) {
                hasCheckedForUpdates = true;
                return;
            }
            
            if (currentModVersion >= latestVersionId) {
                updateAvailable = false;
                hasCheckedForUpdates = true;
                return;
            }
            
            wasAbleToConnect = true;
            
            ModSetup.latestVersion = latestVersion;
            ModSetup.changelog = releases.get(latestIndex).getAsJsonObject().get("body").getAsString().replaceAll("[#*>_]", "");
        } catch (Exception e) {
            wasAbleToConnect = false;
            e.printStackTrace();
        }
        
        hasCheckedForUpdates = true;
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
            InputStream imguiIni = ResourceLoader.getInputStream(imguiIniId);
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
        if (NativesLoader.NATIVES_OS.equals("windows"))
            ensureInstall("cygwin1.dll", Paths.get(Toolchain.TOOLCHAIN_PATH, cygFilename),
                    "cygwin1.dll");
    }

    private static byte[] getGitHubAsset(String assetNameTarget) {
        CodeNodeMicrocontrollers.LOGGER.info("Downloading asset from GitHub... {}", assetNameTarget);

        listGitHubAssets();

        String assetDownloadUrl = null;

        try {
            JsonArray assets = ModSetup.githubAssets;

            for (int i = 0; i < assets.size(); i++) {
                String assetName = assets.get(i).getAsJsonObject().get("name").getAsString();
                if (assetName.equals(assetNameTarget)) {
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

    private static void listGitHubAssets() {
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
            githubAssets = fetcher.jsonContent().getAsJsonObject().get("assets").getAsJsonArray();
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
    
    private static long numberizeVersion(String version) {
        version = version.replaceAll("[a-zA-Z]", "");
        
        String[] parts = version.split("\\.");
        
        if (parts.length > 4)
            throw new IllegalArgumentException("Invalid version format!");
        
        long number = 0;
        for (int i = 0; i < parts.length; i++) {
            number |= Integer.parseInt(parts[i]) << (i * 16);
        }
        return number;
    }
    
    private static boolean isMinecraftSnapshot(String version) {
        return version.matches("[0-9]{2}w[0-9]{2}[a-c]");
    }
}
