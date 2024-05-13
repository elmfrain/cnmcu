package com.elmfer.cnmcu.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;
import com.elmfer.cnmcu.mcu.Sketches;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Config {
    public static final File CONFIG_FILE = new File("config/" + CodeNodeMicrocontrollers.MOD_ID + ".json");

    private static JsonObject config = new JsonObject();
    private static CompletableFuture<Void> saveTask;
    private static boolean firstTimeUse = false;

    static {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE));

            config = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (FileNotFoundException e) {
            CodeNodeMicrocontrollers.LOGGER.warn("Config file not found, creating new one...");

            config.addProperty("adviseUpdates", adviseUpdates());
            config.addProperty("showRegistersInHex", showRegistersInHex());
            config.addProperty("showDocs", showDocs());
            config.addProperty("maxNumBackups", maxNumBackups());
            config.addProperty("lastSaveFilePath", lastSaveFilePath());
            
            firstTimeUse = true;
            save();
        }
    }

    private Config() {
    }
    
    public static boolean isFirstTimeUse() {
        return firstTimeUse;
    }
    
    public static boolean adviseUpdates() {
        if (config.has("adviseUpdates"))
            return config.get("adviseUpdates").getAsBoolean();
        
        return true;
    }
    
    public static void setAdviseUpdates(boolean adviseUpdates) {
        config.addProperty("adviseUpdates", adviseUpdates);
    }
    
    public static boolean showRegistersInHex() {
        if (config.has("showRegistersInHex"))
            return config.get("showRegistersInHex").getAsBoolean();

        return true;
    }
    
    public static void setShowRegistersInHex(boolean showRegistersInHex) {
        config.addProperty("showRegistersInHex", showRegistersInHex);
    }
    
    public static boolean showDocs() {
        if (config.has("showDocs"))
            return config.get("showDocs").getAsBoolean();

        return false;
    }
    
    public static void setShowDocs(boolean showDocs) {
        config.addProperty("showDocs", showDocs);
    }
    
    public static int maxNumBackups() {
        if (config.has("maxNumBackups"))
            return config.get("maxNumBackups").getAsInt();
        
        return 30;
    }
    
    public static void setMaxNumBackups(int maxNumBackups) {
        config.addProperty("maxNumBackups", maxNumBackups);
    }
    
    public static String lastSaveFilePath() {
        if (config.has("lastSaveFilePath"))
            return config.get("lastSaveFilePath").getAsString();

        return Paths.get(Sketches.SKETCHES_PATH, "untitled.s").toAbsolutePath().toString();
    }
    
    public static void setLastSaveFilePath(String lastSaveFilePath) {
        try {
        	String path = Paths.get(lastSaveFilePath).toAbsolutePath().toString();
        	config.addProperty("lastSaveFilePath", path);
        } catch (Exception e) {
        	
        }
    }
    
    public static void save() {
        waitForSave();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(config);

        saveTask = CompletableFuture.runAsync(() -> {
            try {
                Files.write(CONFIG_FILE.toPath(), json.getBytes());
            } catch (IOException e) {
                CodeNodeMicrocontrollers.LOGGER.error("Failed to save config file", e);
            }
            saveTask = null;
        });
    }
    
    public static void waitForSave() {
        if (saveTask != null)
            saveTask.join();
    }
}
