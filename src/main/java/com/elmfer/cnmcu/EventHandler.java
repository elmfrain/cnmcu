package com.elmfer.cnmcu;

import com.elmfer.cnmcu.config.Config;
import com.elmfer.cnmcu.config.ModSetup;
import com.elmfer.cnmcu.mcu.Toolchain;
import com.elmfer.cnmcu.ui.UIRender;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.PlainTextContent;

public class EventHandler {

    public final static ImGuiImplGlfw IMGUI_GLFW = new ImGuiImplGlfw();
    public final static ImGuiImplGl3 IMGUI_GL3 = new ImGuiImplGl3();

    private static boolean hasNotifiedPlayerAboutUpdate = false;

    public static void registerClientEventHandlers() {
        ClientLifecycleEvents.CLIENT_STARTED.register(EventHandler::onClientStarted);
        ClientLifecycleEvents.CLIENT_STOPPING.register(EventHandler::onClientStopping);
        WorldRenderEvents.START.register(EventHandler::onStartRenderWorld);
        WorldRenderEvents.END.register(EventHandler::onEndRenderWorld);
    }

    private static void onStartRenderWorld(WorldRenderContext context) {
        if (!hasNotifiedPlayerAboutUpdate)
            notifyPlayerAboutUpdate();

        UIRender.newFrame();

        EventHandler.IMGUI_GLFW.newFrame();
    }

    private static void onEndRenderWorld(WorldRenderContext context) {
        UIRender.renderBatch();
    }

    private static void onClientStarted(MinecraftClient client) {
        ImGui.createContext();

        IMGUI_GLFW.init(client.getWindow().getHandle(), true);
        IMGUI_GL3.init("#version 150");

        ImGuiIO io = ImGui.getIO();

        io.setIniFilename(ModSetup.IMGUI_INI_FILE);
        io.setDisplaySize(client.getWindow().getWidth(), client.getWindow().getHeight());
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable | ImGuiConfigFlags.NavEnableKeyboard);
    }

    private static void onClientStopping(MinecraftClient client) {
        Config.save();
        Config.waitForSave();
        Toolchain.saveConfig();
        Toolchain.waitForSave();
    }

    private static void notifyPlayerAboutUpdate() {
        if (hasNotifiedPlayerAboutUpdate)
            return;

        hasNotifiedPlayerAboutUpdate = true;

        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            if (!ModSetup.hasCheckedForUpdates() || !ModSetup.isUpdateAvailable())
                return;

            MutableText updateMessage = MutableText
                    .of(new PlainTextContent.Literal("An update is available for CodeNode Microcontrollers: v"));
            updateMessage.setStyle(updateMessage.getStyle().withColor(0xFF5CFF5C));

            MutableText version = MutableText.of(new PlainTextContent.Literal(ModSetup.getLatestVersion() + " "));
            version.setStyle(version.getStyle().withColor(0xFFFFF41F));

            MutableText forMCVersions = MutableText.of(new PlainTextContent.Literal(
                    "for Minecraft " + String.join(", ", ModSetup.getLatestForMinecraftVersions())));
            forMCVersions.setStyle(forMCVersions.getStyle().withColor(0xFF85A9FF));

            updateMessage.getSiblings().add(version);
            updateMessage.getSiblings().add(forMCVersions);

            client.player.sendMessage(updateMessage);
        });
    }
}
