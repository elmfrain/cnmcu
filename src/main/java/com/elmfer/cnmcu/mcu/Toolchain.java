package com.elmfer.cnmcu.mcu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;
import com.elmfer.cnmcu.CodeNodeMicrocontrollersClient;
import com.elmfer.cnmcu.cpp.NativesLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Toolchain {

    public static final String TOOLCHAIN_PATH = CodeNodeMicrocontrollers.MOD_ID + "/toolchain";
    public static final String TEMP_PATH = TOOLCHAIN_PATH + "/temp";
    public static final File CONFIG_FILE = new File(TOOLCHAIN_PATH + "/config.json");

    private static JsonObject config = new JsonObject();
    private static CompletableFuture<Void> saveOperation = null;

    private static StringBuffer buildOutput = new StringBuffer();

    static {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE));

            config = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (FileNotFoundException e) {
            config.addProperty("buildCommand", getBuildCommand());

            saveConfig();
        }
    }

    public static CompletableFuture<byte[]> build(String code) {
        CompletableFuture<byte[]> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                File tempDir = new File(TEMP_PATH);
                Files.createDirectories(tempDir.toPath());

                File codeFile = new File(tempDir, "program.s");
                Files.write(codeFile.toPath(), code.getBytes());

                File outputFile = new File(tempDir, "output.bin");

                String shell = NativesLoader.NATIVES_OS.equals("windows") ? "cmd" : "sh";
                String shellFlag = NativesLoader.NATIVES_OS.equals("windows") ? "/c" : "-c";
                String buildCommand = getBuildCommand();
                buildCommand = buildCommand.replaceAll("\\$\\{input\\}", codeFile.toPath().toAbsolutePath().toString());
                buildCommand = buildCommand.replaceAll("\\$\\{output\\}", outputFile.toPath().toAbsolutePath().toString());

                ProcessBuilder builder = new ProcessBuilder(shell, shellFlag, buildCommand);
                builder.directory(new File(TOOLCHAIN_PATH));
                builder.redirectErrorStream(true);

                Process process = builder.start();

                Thread outThread = new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null)
                            appendBuildOutput(line);
                    } catch (Exception e) {
                        appendBuildOutput("build", "Failed to read build output");
                    }
                });
                outThread.start();

                int exitCode = process.waitFor();
                outThread.join();

                if (exitCode == 0) {
                    byte[] output = Files.readAllBytes(outputFile.toPath());

                    future.complete(output);

                    appendBuildOutput("build", "Build successful");
                } else {
                    future.completeExceptionally(new Exception("Build failed"));

                    appendBuildOutput("build", "Build failed");
                }
            } catch (Exception e) {
                future.completeExceptionally(e);

                appendBuildOutput("build", "Build failed with exception: " + e.getLocalizedMessage());
            }
        });

        return future;
    }

    public static String getBuildOutput() {
        return buildOutput.toString();
    }

    public static void appendBuildOutput(String module, String output) {
        buildOutput.append("[").append(module).append("] ").append(output).append("\n");
    }

    public static void appendBuildOutput(String output) {
        buildOutput.append(output).append("\n");
    }

    public static void clearBuildOutput() {
        buildOutput.setLength(0);
    }

    public static String getBuildCommand() {
        if (config.has("buildCommand"))
            return config.get("buildCommand").getAsString();

        return NativesLoader.NATIVES_OS.equals("windows") ?
                "vasm6502_oldstyle -Fbin -dotdir ${input} -o ${output}"
                : "./vasm6502_oldstyle -Fbin -dotdir ${input} -o ${output}";
    }

    public static void setBuildCommand(String command) {
        config.addProperty("buildCommand", command);
    }

    public static void saveConfig() {
        waitForSave();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(config);

        saveOperation = CompletableFuture.runAsync(() -> {
            try {
                Files.createDirectories(CONFIG_FILE.toPath().getParent());

                Files.write(CONFIG_FILE.toPath(), json.getBytes());
            } catch (Exception e) {
                CodeNodeMicrocontrollersClient.LOGGER.error("Failed to save toolchain config", e);
            }
            saveOperation = null;
        });
    }

    public static void waitForSave() {
        if (saveOperation != null)
            saveOperation.join();
    }
}
