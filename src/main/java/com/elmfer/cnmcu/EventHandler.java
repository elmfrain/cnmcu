package com.elmfer.cnmcu;

import imgui.ImGui;
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
        
    }
    
    private static void onEndRenderWorld(WorldRenderContext context) {
//        ImGui.render();
//        IMGUI_GL3.renderDrawData(ImGui.getDrawData());
    }
    
    private static void onClientStarted(MinecraftClient client) {
        ImGui.createContext();
        
        IMGUI_GLFW.init(client.getWindow().getHandle(), true);
        IMGUI_GL3.init("#version 150");
        ImGui.getIO().setDisplaySize(client.getWindow().getWidth(), client.getWindow().getHeight());
    }
    
    private static void onClientStopping(MinecraftClient client) {
        // Do something when the client is stopping
    }
}
