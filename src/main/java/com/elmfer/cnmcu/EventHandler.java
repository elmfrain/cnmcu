package com.elmfer.cnmcu;

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

public class EventHandler {

    public final static ImGuiImplGlfw IMGUI_GLFW = new ImGuiImplGlfw();
    public final static ImGuiImplGl3 IMGUI_GL3 = new ImGuiImplGl3();
    
    public static void registerClientEventHandlers() {
        ClientLifecycleEvents.CLIENT_STARTED.register(EventHandler::onClientStarted);
        ClientLifecycleEvents.CLIENT_STOPPING.register(EventHandler::onClientStopping);
        WorldRenderEvents.START.register(EventHandler::onStartRenderWorld);
        WorldRenderEvents.END.register(EventHandler::onEndRenderWorld);
    }
    
    private static void onStartRenderWorld(WorldRenderContext context) {
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
        
        io.setIniFilename(CodeNodeMicrocontrollers.MOD_ID + "/imgui.ini");
        io.setDisplaySize(client.getWindow().getWidth(), client.getWindow().getHeight());
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
    }
    
    private static void onClientStopping(MinecraftClient client) {
        Toolchain.saveConfig();
        Toolchain.waitForSave();
    }
}
