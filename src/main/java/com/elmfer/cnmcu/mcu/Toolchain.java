package com.elmfer.cnmcu.mcu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;
import com.elmfer.cnmcu.CodeNodeMicrocontrollersClient;
import com.elmfer.cnmcu.cpp.NativesLoader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import imgui.ImGui;
import imgui.type.ImString;

public class Toolchain {

	public static final String TOOLCHAIN_PATH = CodeNodeMicrocontrollers.MOD_ID + "/toolchain";
	public static final String TEMP_PATH = TOOLCHAIN_PATH + "/temp";
	public static final File CONFIG_FILE = new File(TOOLCHAIN_PATH + "/config.json");
	
	private static JsonObject config = new JsonObject();
	private static JsonObject buildVariables = new JsonObject();
	private static JsonObject envVariables = new JsonObject();
	private static CompletableFuture<Void> saveOperation = null;

	private static StringBuffer buildStdout = new StringBuffer();

	static {
        loadConfig();
    }
	
	public static void loadConfig() {
		try {
            BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE));

            config = JsonParser.parseReader(reader).getAsJsonObject();
            
           	if(config.has("buildVariables"))
           		buildVariables = config.getAsJsonObject("buildVariables");
           	else
           		loadDefaultBuildVariables();
           	
           	if(config.has("envVariables"))
           		envVariables = config.getAsJsonObject("envVariables");
        } catch (Exception e) {
            loadDefaults();
            saveConfig();
        }
	}
	
	public static void loadDefaults() {
	    config.addProperty("buildCommand", getBuildCommand());
	    config.addProperty("workingDirectory", TOOLCHAIN_PATH);
	    buildVariables = new JsonObject();
	    loadDefaultBuildVariables();
        envVariables = new JsonObject();
	}
	
	private static void loadDefaultBuildVariables() {
		buildVariables.addProperty("input", "temp/program.s");
        buildVariables.addProperty("output", "temp/output.bin");
	}

	public static CompletableFuture<byte[]> build(String code) {
		CompletableFuture<byte[]> future = new CompletableFuture<>();

		File workingDir = new File(getWorkingDirectory());
		
		CompletableFuture.runAsync(() -> {
			try {
				File codeFile = new File(workingDir, buildVariables.get("input").getAsString());
				Files.write(codeFile.toPath(), code.getBytes());

				File outputFile = new File(workingDir, buildVariables.get("output").getAsString());

				String shell = NativesLoader.NATIVES_OS.equals("windows") ? "cmd" : "sh";
				String shellFlag = NativesLoader.NATIVES_OS.equals("windows") ? "/c" : "-c";
				String buildCommand = getBuildCommand();
				
                for (Map.Entry<String, JsonElement> entry : buildVariables.entrySet()) {
                    buildCommand = buildCommand.replace("${" + entry.getKey() + "}",
                            Matcher.quoteReplacement(entry.getValue().getAsString()));
                }

				ProcessBuilder builder = new ProcessBuilder(shell, shellFlag, buildCommand);
				builder.directory(new File(getWorkingDirectory()));
				builder.redirectErrorStream(true);
				Map<String, String> env = builder.environment();
                for (Map.Entry<String, JsonElement> entry : envVariables.entrySet())
                    env.put(entry.getKey(), entry.getValue().getAsString());

				Process process = builder.start();

				Thread outThread = new Thread(() -> {
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
						String line;
						while ((line = reader.readLine()) != null)
							appendBuildStdout(line);
					} catch (Exception e) {
						appendBuildStdout("build", "Failed to read build output");
					}
				});
				outThread.start();

				int exitCode = process.waitFor();
				outThread.join();

				if (exitCode == 0) {
					byte[] output = Files.readAllBytes(outputFile.toPath());

					future.complete(output);

					appendBuildStdout("build", "Build successful");
				} else {
					future.completeExceptionally(new Exception("Build failed"));

					appendBuildStdout("build", "Build failed");
				}
			} catch (Exception e) {
				future.completeExceptionally(e);

				appendBuildStdout("build", "Build failed with exception: \n" + e.getMessage());
			}
		});

		return future;
	}

	public static String getBuildStdout() {
		return buildStdout.toString();
	}

	public static void appendBuildStdout(String module, String output) {
		buildStdout.append("[").append(module).append("] ").append(output).append("\n");
	}

	public static void appendBuildStdout(String output) {
		buildStdout.append(output).append("\n");
	}

	public static void clearBuildStdout() {
		buildStdout.setLength(0);
	}

	public static String getBuildCommand() {
		if (config.has("buildCommand"))
			return config.get("buildCommand").getAsString();

		return NativesLoader.NATIVES_OS.equals("windows") ? "vasm6502_oldstyle -Fbin -dotdir ${input} -o ${output}"
				: "./vasm6502_oldstyle -Fbin -dotdir ${input} -o ${output}";
	}

	public static void setBuildCommand(String command) {
		config.addProperty("buildCommand", command);
	}
	
	public static String getWorkingDirectory() {
        if (config.has("workingDirectory"))
            return config.get("workingDirectory").getAsString();
        
        return TOOLCHAIN_PATH;
	}
	
    public static void setWorkingDirectory(String directory) {
        config.addProperty("workingDirectory", directory);
    }
	
    public static String getBuildVariable(String name) {
        if (buildVariables.has(name))
            return buildVariables.get(name).getAsString();
        
        return "";
    }
    
    public static void setBuildVariable(String name, String value) {
        buildVariables.addProperty(name, value);
    }

    public static void removeBuildVariable(String name) {
        buildVariables.remove(name);
    }
    
    public static String getEnvVariable(String name) {
        if (envVariables.has(name))
            return envVariables.get(name).getAsString();

        return "";
    }
    
    public static void setEnvVariable(String name, String value) {
        envVariables.addProperty(name, value);
    }
    
    public static void removeEnvVariable(String name) {
        envVariables.remove(name);
    }
    
    public static void saveConfig() {
        waitForSave();
        
        config.add("buildVariables", buildVariables);
        config.add("envVariables", envVariables);

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
    
    private static ImString buildCommand = new ImString(getBuildCommand(), 2048);
    private static ImString workingDirectory = new ImString(getWorkingDirectory(), 2048);
    public static void genToolchainConfigUI() {
        float windowWidth = ImGui.getContentRegionAvailX();
        buildCommand.set(getBuildCommand());
        workingDirectory.set(getWorkingDirectory());
        
        ImGui.text("Build Command");
        ImGui.setNextItemWidth(windowWidth);
        if (ImGui.inputText("##Build Command", buildCommand))
            setBuildCommand(buildCommand.get());
        
        ImGui.text("Working Directory");
        ImGui.setNextItemWidth(windowWidth);
        
        if (ImGui.inputText("##Working Directory", workingDirectory))
            setWorkingDirectory(workingDirectory.get());
        ImGui.newLine();
        
        if (ImGui.collapsingHeader("Build Variables"))
            genBuildVariables();
        
        if (ImGui.collapsingHeader("Environment Variables"))
            genEnvVariables();
    }
    
    private static ImString newBuildVariableName = new ImString("", 64);
    private static ImString buildVariablesInputs[] = new ImString[0];
    private static boolean showBuildVariableWarning = false;
    private static void genBuildVariables() {
        float windowWidth = ImGui.getContentRegionAvailX();
        
        ImGui.indent();
        
        ImGui.textWrapped("Create and use build variables for them to be use in your build command."
                + " You can use the variables in your build command by wrapping the variable name in ${}.");
        ImGui.newLine();
        
        if (buildVariablesInputs.length != buildVariables.size())
            buildVariablesInputs = new ImString[buildVariables.size()];
        
        int i = 0;
        for (Map.Entry<String, JsonElement> entry : buildVariables.entrySet()) {
            if (buildVariablesInputs[i] == null)
                buildVariablesInputs[i] = new ImString(entry.getValue().getAsString(), 1024);
            buildVariablesInputs[i].set(entry.getValue().getAsString());
            
            if (ImGui.inputText(entry.getKey() + "##BuildVar" + i, buildVariablesInputs[i]))
                setBuildVariable(entry.getKey(), buildVariablesInputs[i].get());
            ImGui.sameLine();
            ImGui.setCursorPosX(windowWidth - 7);
            if (ImGui.button("x##BuildVar" + entry.getKey()))
                removeBuildVariable(entry.getKey());
            
            i++;
        }
        
        ImGui.text("New Build Variable");
        if (ImGui.inputText("Name##BuildVar", newBuildVariableName))
            showBuildVariableWarning = true;
        ImGui.sameLine();
        ImGui.setCursorPosX(windowWidth - 7);
        ImGui.beginDisabled(newBuildVariableName.isEmpty() || buildVariables.has(newBuildVariableName.get()));
        if (ImGui.button("+##BuildVar")) {
            setBuildVariable(newBuildVariableName.get(), "");
            newBuildVariableName.set("");
            showBuildVariableWarning = false;
        }
        ImGui.endDisabled();
        if (showBuildVariableWarning) {
            if (newBuildVariableName.isEmpty())
                ImGui.textColored(0xFF8888FF, "Name cannot be empty");
            else if (buildVariables.has(newBuildVariableName.get()))
                ImGui.textColored(0xFF8888FF, "Name already exists");
            else
                ImGui.newLine();
        } else
            ImGui.newLine();
        
        ImGui.unindent();
    }
    
    private static ImString newEnvVariableName = new ImString("", 64);
    private static ImString envVariablesInputs[] = new ImString[envVariables.size()];
    private static boolean showEnvVariableWarning = false;
    private static void genEnvVariables() {
        float windowWidth = ImGui.getContentRegionAvailX();

        ImGui.indent();

        ImGui.textWrapped("Create and use environment variables for your command's process."
                + " They will also apply the child processes of the command.");
        ImGui.newLine();

        if (envVariablesInputs.length != envVariables.size())
            envVariablesInputs = new ImString[envVariables.size()];

        int i = 0;
        for (Map.Entry<String, JsonElement> entry : envVariables.entrySet()) {
            if (envVariablesInputs[i] == null)
                envVariablesInputs[i] = new ImString(entry.getValue().getAsString(), 1024);

            if (ImGui.inputText(entry.getKey() + "##EnvVar" + i, envVariablesInputs[i]))
                setEnvVariable(entry.getKey(), envVariablesInputs[i].get());
            ImGui.sameLine();
            ImGui.setCursorPosX(windowWidth - 7);
            if (ImGui.button("x##EnvVar" + entry.getKey()))
                removeEnvVariable(entry.getKey());

            i++;
        }

        ImGui.text("New Environment Variable");
        if (ImGui.inputText("Name##EnvVar", newEnvVariableName))
            showEnvVariableWarning = true;
        ImGui.sameLine();
        ImGui.setCursorPosX(windowWidth - 7);
        ImGui.beginDisabled(newEnvVariableName.isEmpty() || envVariables.has(newEnvVariableName.get()));
        if (ImGui.button("+##EnvVar")) {
            setEnvVariable(newEnvVariableName.get(), "");
            newEnvVariableName.set("");
            showEnvVariableWarning = false;
        }
        ImGui.endDisabled();
        if (showEnvVariableWarning) {
            if (newEnvVariableName.isEmpty())
                ImGui.textColored(0xFF8888FF, "Name cannot be empty");
            else if (envVariables.has(newEnvVariableName.get()))
                ImGui.textColored(0xFF8888FF, "Name already exists");
            else
                ImGui.newLine();
        } else
            ImGui.newLine();

        ImGui.unindent();
    }
}
