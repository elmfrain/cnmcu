package com.elmfer.cnmcu.ui;

import com.elmfer.cnmcu.EventHandler;
import com.elmfer.cnmcu.ui.UIRender.Direction;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.memedit.MemoryEditor;
import imgui.extension.texteditor.TextEditor;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImString;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableTextContent;

public class IDEScreen extends Screen {
    
    private static ImGuiIO IO = ImGui.getIO();
    
    private static final String DOCKSPACE_NAME = "DockSpace";
    private static final String CODE_EDITOR_NAME = "Code Editor";
    private static final String CONSOLE_NAME = "Console";
    
    private TextEditor textEditor;
    private MemoryEditor memoryEditor;
    private ImString compileCommand = new ImString();
    
    public IDEScreen() {
        super(MutableText
                .of(new TranslatableTextContent("com.cnmcu.ui.ide_screen.title", "IDE Screen", new Object[0])));
        
        textEditor = new TextEditor();
        memoryEditor = new MemoryEditor();
    }

    @Override
    public void render(DrawContext stack, int mouseX, int mouseY, float delta) {
        float height = UIRender.getUIheight();
        int width = UIRender.getUIwidth();
        UIRender.drawGradientRect(0, height * 0.66f, width, height, 0x00, 0x7D000000);
        
        ImGui.newFrame();
        EventHandler.IMGUI_GLFW.newFrame();

        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.setNextWindowSize(IO.getDisplaySizeX(), IO.getDisplaySizeY(), ImGuiCond.Always);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        
        int windowFlags = ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove
                | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus
                | ImGuiWindowFlags.NoBackground;

        ImGui.begin(DOCKSPACE_NAME, windowFlags);
        ImGui.dockSpace(ImGui.getID(DOCKSPACE_NAME), 0, 0, ImGuiDockNodeFlags.PassthruCentralNode);
        ImGui.popStyleVar(3);
        
        genTextEditor();
        genConsole();
        genMCUStatus();
        genCPUStatus();
        genMemoryViewer(); 
        
        ImGui.end();
        
        ImGui.render();
        EventHandler.IMGUI_GL3.renderDrawData(ImGui.getDrawData());
    }

    private void genTextEditor() {
        if (!ImGui.begin(CODE_EDITOR_NAME, ImGuiWindowFlags.MenuBar)) {
            ImGui.end();
            return;
        }

        if(ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Save")) {

                }
                ImGui.endMenu();
            }
            
            if(ImGui.beginMenu("Edit")) {
                if (ImGui.menuItem("Undo", "CTRL+Z"))
                    textEditor.undo(1);
                if (ImGui.menuItem("Redo", "CTRL+Y"))
                    textEditor.redo(1);
                ImGui.separator();
                if (ImGui.menuItem("Cut", "CTRL+X"))
                    textEditor.cut();
                if (ImGui.menuItem("Copy", "CTRL+C"))
                    textEditor.copy();
                if (ImGui.menuItem("Paste", "CTRL+V"))
                    textEditor.paste();
                ImGui.endMenu();
            }
            
            if(ImGui.beginMenu("Select")) {
                if (ImGui.menuItem("Select All", "CTRL+A"))
                    textEditor.selectAll();
                ImGui.endMenu();
            }
            
            if(ImGui.beginMenu("Tools")) {
                if(ImGui.menuItem("Compile")) {}
                if(ImGui.menuItem("Upload")) {}
                ImGui.endMenu();
            }
            
            ImGui.endMenuBar();
        }
        
        ImGui.beginDisabled(false);
        if(ImGui.button("Compile")) {}
        ImGui.sameLine();
        if(ImGui.button("Upload")) {}
        ImGui.sameLine();
        if(ImGui.inputText("##", compileCommand)) {}
        ImGui.endDisabled();
        
        ImGui.text("res/program.s");
        ImGui.setNextWindowSize(0, 400);
        textEditor.render("TextEditor");

        ImGui.end();
    }
    
    private void genConsole() {
        if (!ImGui.begin(CONSOLE_NAME)) {
            ImGui.end();
            return;
        }
        
        ImGui.text("Output");
        ImGui.sameLine();
        if(ImGui.button("Clear")) {}
        ImGui.separator();
        
        ImGui.end();
    }
    
    private void genMemoryViewer() {
        if (!ImGui.begin("Zero Page Viewer")) {
            ImGui.end();
            return;
        }
        
        ImGui.text("Zero Page");
        
        ImGui.end();
    }
    
    private void genMCUStatus() {
        if (!ImGui.begin("MCU Status")) {
            ImGui.end();
            return;
        }
        
        ImGui.text("Specs");
        ImGui.sameLine();
        ImGui.separator();
        
        ImGui.text("CPU: mos6502");
        ImGui.text("RAM: 0.5KB");
        ImGui.text("ROM: 8KB");
        ImGui.text("Clock: 800Hz");
        ImGui.text("Modules: GPIO");
        
        ImGui.text("Controls");
        ImGui.sameLine();
        ImGui.separator();
        
        if(ImGui.checkbox("Power", true)) {}
        ImGui.sameLine();
        if(ImGui.button("Reset")) {}
        ImGui.sameLine();
        if(ImGui.checkbox("Pause", false)) {}
        ImGui.sameLine();
        if(ImGui.button("Step")) {}
        
        ImGui.end();
    }
    
    private void genCPUStatus() {
        if (!ImGui.begin("CPU Status")) {
            ImGui.end();
            return;
        }
        
        ImGui.checkbox("Registers in Hex", true);
        
        ImGui.text("A: 0x00");
        ImGui.text("X: 0x00");
        ImGui.text("Y: 0x00");
        
        ImGui.text("PC: 0x0000");
        ImGui.text("SP: 0x00");
        ImGui.text("Flags: --------");
        ImGui.text("Cycles: 0");
        
        ImGui.text("Bus");
        ImGui.sameLine();
        ImGui.separator();
        
        ImGui.text("Address: 0x0000");
        ImGui.text("Data: 0x00");
        ImGui.text("RW: Read");
        
        ImGui.end();
    }
}
