package com.elmfer.cnmcu.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Config {
    public static final File CONFIG_FILE = new File("config/" + CodeNodeMicrocontrollers.MOD_ID + ".json");

    private static JsonObject config = new JsonObject();
    private static CompletableFuture<Void> saveTask = new CompletableFuture<>();
    private static boolean firstTimeUse = false;

    static {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE));

            config = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (FileNotFoundException e) {
            CodeNodeMicrocontrollers.LOGGER.warn("Config file not found, creating new one...");

            firstTimeUse = true;
            save();
        }
    }

    public static boolean isFirstTimeUse() {
        return firstTimeUse;
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
