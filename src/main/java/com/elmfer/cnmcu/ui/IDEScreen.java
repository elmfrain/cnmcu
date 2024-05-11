package com.elmfer.cnmcu.ui;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import com.elmfer.cnmcu.EventHandler;
import com.elmfer.cnmcu.animation.ClockTimer;
import com.elmfer.cnmcu.config.Config;
import com.elmfer.cnmcu.cpp.NativesUtils;
import com.elmfer.cnmcu.mcu.Toolchain;
import com.elmfer.cnmcu.network.IDEScreenHeartbeatC2SPacket;
import com.elmfer.cnmcu.network.IDEScreenMCUControlC2SPacket;
import com.elmfer.cnmcu.network.IDEScreenMCUControlC2SPacket.Control;
import com.elmfer.cnmcu.network.IDEScreenSaveCodeC2SPacket;
import com.elmfer.cnmcu.network.IDEScreenSyncS2CPacket.BusStatus;
import com.elmfer.cnmcu.network.IDEScreenSyncS2CPacket.CPUStatus;
import com.elmfer.cnmcu.network.UploadROMC2S2CPacket;
import com.elmfer.cnmcu.ui.handler.IDEScreenHandler;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.memedit.MemoryEditor;
import imgui.extension.texteditor.TextEditor;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiDockNodeFlags;
import imgui.flag.ImGuiFocusedFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class IDEScreen extends HandledScreen<IDEScreenHandler> {

    private static final String DOCKSPACE_NAME = "DockSpace";
    private static final String CODE_EDITOR_NAME = "Code Editor";
    private static final String CONSOLE_NAME = "Console";

    private static ImGuiIO IO = ImGui.getIO();

    private TextEditor textEditor;
    private MemoryEditor memoryEditor;
    private IDEScreenHandler handler;

    private boolean saved = true;

    public CPUStatus cpuStatus = new CPUStatus();
    public BusStatus busStatus = new BusStatus();
    public boolean isPowered = false;
    public boolean isClockPaused = false;
    public ByteBuffer zeroPage = BufferUtils.createByteBuffer(256);

    private ClockTimer heartbeatTimer = new ClockTimer(1);
    private IDEScreenHeartbeatC2SPacket heartbeatPacket;

    private CompletableFuture<byte[]> compileFuture;
    private UploadROMC2S2CPacket uploadPacket;
    private boolean shouldUpload = false;

    private boolean showAbout = false;
    private boolean showDocs = false;
    private boolean showUpdates = false;
    private boolean showToolchainSettings = false;
    private boolean shouldLoadDefaults = false;
    private boolean showRegistersInHex = Config.showRegistersInHex();

    public IDEScreen(IDEScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        textEditor = new TextEditor();
        memoryEditor = new MemoryEditor();
        heartbeatPacket = new IDEScreenHeartbeatC2SPacket(handler.getMcuID());

        textEditor.setText(handler.getCode());

        this.handler = handler;
    }

    @Override
    public void render(DrawContext stack, int mouseX, int mouseY, float delta) {
        sendHeartbeat();

        float height = UIRender.getUIheight();
        int width = UIRender.getUIwidth();
        UIRender.drawGradientRect(0, height * 0.66f, width, height, 0x00, 0x7D000000);

        ImGui.newFrame();

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

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if (IO.getWantCaptureKeyboard())
            return true;

        if (keyCode == GLFW.GLFW_KEY_ESCAPE || client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            close();
            return true;
        }

        return true;
    }

    private void genTextEditor() {
        if (!ImGui.begin(CODE_EDITOR_NAME, ImGuiWindowFlags.MenuBar)) {
            ImGui.end();
            return;
        }

        if (ImGui.beginMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Save", "CTRL+S"))
                    save();

                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Edit")) {
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

            if (ImGui.beginMenu("Select")) {
                if (ImGui.menuItem("Select All", "CTRL+A"))
                    textEditor.selectAll();
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Tools")) {
                if (ImGui.menuItem("Build"))
                    build();
                if (ImGui.menuItem("Upload"))
                    upload();
                if (ImGui.menuItem("Settings"))
                    showToolchainSettings = true;
                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Help")) {
                if (ImGui.menuItem("About"))
                    showAbout = true;
                if (ImGui.menuItem("Documentation"))
                    showDocs = true;
                if (ImGui.menuItem("Updates"))
                    showUpdates = true;
                ImGui.endMenu();
            }

            ImGui.endMenuBar();
        }

        if (ctrlKeyCombo('S'))
            save();

        ImGui.beginDisabled(shouldUpload || compileFuture != null);
        if (ImGui.button("Build"))
            build();
        ImGui.sameLine();
        if (ImGui.button("Upload"))
            upload();
        ImGui.endDisabled();

        if (compileFuture != null && compileFuture.isDone() && !shouldUpload)
            compileFuture = null;

        if (shouldUpload && compileFuture.isDone() && uploadPacket == null) {
            try {
                uploadPacket = new UploadROMC2S2CPacket(handler.getMcuID(), compileFuture.get());
                uploadPacket.request();
            } catch (Exception e) {
                shouldUpload = false;
            }
        }

        if (uploadPacket != null && uploadPacket.isReady()) {
            Toolchain.appendBuildStdout("Upload", uploadPacket.message);
            uploadPacket = null;
            shouldUpload = false;
        }

        ImGui.text(String.format("%s %s", Toolchain.getBuildVariable("input"),  saved ? "[Saved]" : "[Unsaved]"));
        ImGui.setNextWindowSize(0, 400);
        textEditor.render("TextEditor");

        if (ImGui.isWindowFocused(ImGuiFocusedFlags.RootAndChildWindows) && ImGui.isKeyPressed(GLFW.GLFW_KEY_ESCAPE))
            ImGui.setWindowFocus(null);

        if (textEditor.isTextChanged())
            saved = false;

        if (showAbout) {
            ImGui.openPopup("About");
            showAbout = false;
        }

        if (showDocs) {
            ImGui.openPopup("Documentation");
            showDocs = false;
        }
        
        if (showUpdates) {
            ImGui.openPopup("Updates");
            showUpdates = false;
        }
        
        if (showToolchainSettings) {
            ImGui.openPopup("Toolchain Settings");
            showToolchainSettings = false;
        }

        float width = UIRender.getWindowWidth();
        float height = UIRender.getWindowHeight();
        float centerX = width / 2;
        float centerY = height / 2;
        ImGui.setNextWindowPos(centerX, centerY, ImGuiCond.Always, 0.5f, 0.5f);
        ImGui.setNextWindowSize(800, 322, ImGuiCond.Once);
        ImGui.setNextWindowSizeConstraints(0, 0, width, height);
        
        if (ImGui.beginPopupModal("About")) {
            float windowHeight = ImGui.getContentRegionAvailY();
            QuickReferences.genAbout();
            ImGui.newLine();
            ImGui.setCursorPosY(Math.max(windowHeight, ImGui.getCursorPosY()));
            if (ImGui.button("Close")) 
                ImGui.closeCurrentPopup();
            ImGui.endPopup();
        }
        
        
        ImGui.setNextWindowPos(centerX, centerY, ImGuiCond.Always, 0.5f, 0.5f);
        ImGui.setNextWindowSize(800, 400, ImGuiCond.Once);
        ImGui.setNextWindowSizeConstraints(0, 0, width, height);

        if (ImGui.beginPopupModal("Documentation")) {
            float windowHeight = ImGui.getContentRegionAvailY();
            QuickReferences.genNanoDocumentation();
            ImGui.newLine();
            ImGui.setCursorPosY(Math.max(windowHeight, ImGui.getCursorPosY()));
            if (ImGui.button("Close"))
                ImGui.closeCurrentPopup();
            ImGui.endPopup();
        }
        
        ImGui.setNextWindowPos(centerX, centerY, ImGuiCond.Always, 0.5f, 0.5f);
        ImGui.setNextWindowSize(800, 322, ImGuiCond.Once);
        ImGui.setNextWindowSizeConstraints(0, 0, width, height);
        
        if (ImGui.beginPopupModal("Updates")) {
            float windowHeight = ImGui.getContentRegionAvailY();
            QuickReferences.genUpdates();
            ImGui.newLine();
            ImGui.setCursorPosY(Math.max(windowHeight, ImGui.getCursorPosY()));
            if (ImGui.button("Close"))
                ImGui.closeCurrentPopup();
            ImGui.sameLine();
            if (ImGui.checkbox("Notify me of updates", Config.adviseUpdates()))
                Config.setAdviseUpdates(!Config.adviseUpdates());
            ImGui.endPopup();
        }
        
        ImGui.setNextWindowPos(centerX, centerY, ImGuiCond.Always, 0.5f, 0.5f);
        ImGui.setNextWindowSize(500, 300, ImGuiCond.Once);
        ImGui.setNextWindowSizeConstraints(0, 0, width, height);
        
        if (ImGui.beginPopupModal("Toolchain Settings")) {
            float windowHeight = ImGui.getContentRegionAvailY();
            Toolchain.genToolchainConfigUI();
            ImGui.newLine();
            ImGui.setCursorPosY(Math.max(windowHeight, ImGui.getCursorPosY()));
            if (ImGui.button("Close"))
                ImGui.closeCurrentPopup();
            ImGui.sameLine();
            if (ImGui.button("Refresh"))
            	Toolchain.loadConfig();
            ImGui.pushStyleColor(ImGuiCol.Text, shouldLoadDefaults ? 0xFF5555FF : 0xFFFFFFFF);
            ImGui.sameLine();
            if (ImGui.button(!shouldLoadDefaults ? "Load Defaults" : "Are you sure?")) {
                if (shouldLoadDefaults)
                    Toolchain.loadDefaults();
                shouldLoadDefaults = !shouldLoadDefaults;
            }
            ImGui.popStyleColor();
            ImGui.endPopup();
        }

        ImGui.end();
    }

    private void genConsole() {
        if (!ImGui.begin(CONSOLE_NAME)) {
            ImGui.end();
            return;
        }

        ImGui.text("Output");
        ImGui.sameLine();
        if (ImGui.button("Clear"))
            Toolchain.clearBuildStdout();
        ImGui.separator();

        ImGui.beginChild("OutputBody");
        ImGui.textWrapped(Toolchain.getBuildStdout());
        ImGui.endChild();

        ImGui.end();
    }

    private void genMemoryViewer() {
        if (!ImGui.begin("Zero Page Viewer")) {
            ImGui.end();
            return;
        }

        memoryEditor.drawContents(NativesUtils.getByteBufferAddress(zeroPage), zeroPage.capacity());

        ImGui.end();
    }

    private void genMCUStatus() {
        if (!ImGui.begin("MCU Status")) {
            ImGui.end();
            return;
        }

        ImGui.text(String.format("MCU ID: %s", handler.getMcuID()));

        ImGui.text("Specs");
        ImGui.sameLine();
        ImGui.separator();

        ImGui.text("CPU: mos6502");
        ImGui.text("RAM: 0.5KB");
        ImGui.text("ROM: 8KB");
        ImGui.text("Clock: 800Hz");
        ImGui.text("Modules: GPIO, EL, UART");

        ImGui.text("Controls");
        ImGui.sameLine();
        ImGui.separator();

        if (ImGui.checkbox("Power", isPowered))
            new IDEScreenMCUControlC2SPacket(handler.getMcuID(), isPowered ? Control.POWER_OFF : Control.POWER_ON)
                    .send();
        ImGui.sameLine();
        if (ImGui.button("Reset"))
            new IDEScreenMCUControlC2SPacket(handler.getMcuID(), Control.RESET).send();
        ImGui.sameLine();
        ImGui.beginDisabled(!isPowered);
        if (ImGui.checkbox("Pause", isClockPaused))
            new IDEScreenMCUControlC2SPacket(handler.getMcuID(),
                    isClockPaused ? Control.RESUME_CLOCK : Control.PAUSE_CLOCK).send();
        ImGui.sameLine();
        ImGui.beginDisabled(!isClockPaused);
        if (ImGui.button("Step"))
            new IDEScreenMCUControlC2SPacket(handler.getMcuID(), Control.CYCLE).send();
        ImGui.endDisabled();
        ImGui.endDisabled();

        ImGui.end();
    }

    private void genCPUStatus() {
        if (!ImGui.begin("CPU Status")) {
            ImGui.end();
            return;
        }

        if (ImGui.checkbox("Registers in Hex", showRegistersInHex))
            showRegistersInHex = !showRegistersInHex;

        if (showRegistersInHex) {
            ImGui.text(String.format("A: 0x%02X", cpuStatus.A));
            ImGui.text(String.format("X: 0x%02X", cpuStatus.X));
            ImGui.text(String.format("Y: 0x%02X", cpuStatus.Y));
        } else {
            ImGui.text(String.format("A: %d", cpuStatus.A));
            ImGui.text(String.format("X: %d", cpuStatus.X));
            ImGui.text(String.format("Y: %d", cpuStatus.Y));
        }

        ImGui.text(String.format("PC: 0x%04X", cpuStatus.PC));
        ImGui.text(String.format("SP: 0x%02X", cpuStatus.SP));
        ImGui.text(String.format("Flags: %c%c%c%c%c%c%c%c", (cpuStatus.Flags & 0x80) != 0 ? 'N' : '-',
                (cpuStatus.Flags & 0x40) != 0 ? 'V' : '-', (cpuStatus.Flags & 0x20) != 0 ? 'U' : '-',
                (cpuStatus.Flags & 0x10) != 0 ? 'B' : '-', (cpuStatus.Flags & 0x08) != 0 ? 'D' : '-',
                (cpuStatus.Flags & 0x04) != 0 ? 'I' : '-', (cpuStatus.Flags & 0x02) != 0 ? 'Z' : '-',
                (cpuStatus.Flags & 0x01) != 0 ? 'C' : '-'));
        ImGui.text(String.format("Cycles: %d", cpuStatus.Cycles));

        ImGui.text("Bus");
        ImGui.sameLine();
        ImGui.separator();

        ImGui.text(String.format("Address: 0x%02X", busStatus.Address));
        ImGui.text(String.format("Data: 0x%02X", busStatus.Data));
        ImGui.text(String.format("RW: %s", !busStatus.RW ? "Read" : "Write"));

        ImGui.end();
    }

    private void sendHeartbeat() {
        if (heartbeatTimer.ticksPassed() != 0)
            heartbeatPacket.send();
    }

    private void build() {
        saved = true;
        new IDEScreenSaveCodeC2SPacket(textEditor.getText(), handler.getMcuID()).send();

        Toolchain.clearBuildStdout();
        compileFuture = Toolchain.build(textEditor.getText());
    }
    
    private void upload() {
        saved = true;
        new IDEScreenSaveCodeC2SPacket(textEditor.getText(), handler.getMcuID()).send();

        Toolchain.clearBuildStdout();
        compileFuture = Toolchain.build(textEditor.getText());
        shouldUpload = true;
    }
    
    private void save() {
        saved = true;
        new IDEScreenSaveCodeC2SPacket(textEditor.getText(), handler.getMcuID()).send();
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removed() {
        Config.setShowRegistersInHex(showRegistersInHex);
        Config.save();
        Toolchain.saveConfig();
        Toolchain.clearBuildStdout();

        if (compileFuture != null)
            compileFuture.cancel(true);
    }

    private boolean ctrlKeyCombo(char key) {
        boolean ctrl = IO.getConfigMacOSXBehaviors() ? IO.getKeySuper() : IO.getKeyCtrl();
        return ctrl && ImGui.isKeyPressed(key);
    }
}
