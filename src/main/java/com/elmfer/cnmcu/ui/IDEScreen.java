package com.elmfer.cnmcu.ui;

import com.elmfer.cnmcu.EventHandler;

import imgui.ImGui;
import imgui.ImGuiIO;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableTextContent;

public class IDEScreen extends Screen {
    public IDEScreen() {
        super(MutableText.of(new TranslatableTextContent("com.cnmcu.ui.ide_screen.title", "IDE Screen", new Object[0])));
    }
    
    @Override
    public void render(DrawContext stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack, mouseX, mouseY, delta);
        ImGui.newFrame();
        EventHandler.IMGUI_GLFW.newFrame();
        
        ImGuiIO io = ImGui.getIO();
        
        ImGui.begin("IDE Screen");
        ImGui.text("Display Size: " + io.getDisplaySizeX() + "x" + io.getDisplaySizeY());
        ImGui.end();
        
        ImGui.render();
        EventHandler.IMGUI_GL3.renderDrawData(ImGui.getDrawData());
    }
}
