package com.elmfer.cnmcu.ui;

import java.net.URL;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;
import com.elmfer.cnmcu.animation.Timer;
import com.elmfer.cnmcu.cpp.NativesLoader;

import imgui.ImGui;
import imgui.flag.ImGuiTableFlags;

public class QuickReferences {

    private static final Timer vasmLicenseLinkTimer = new Timer(3).expire();
    private static final Timer docsLinkTimer = new Timer(3).expire();

    private QuickReferences() {

    }

    public static void genAbout() {
        ImGui.text(String.format("CodeNode Microcontrollers v%s", CodeNodeMicrocontrollers.MOD_VERSION));
        ImGui.text("Author: elmfer");
        ImGui.text("License: GNU GPL v3");
        ImGui.text("GitHub Repo: elmfrain/cnmcu");
        ImGui.newLine();

        ImGui.text(
                "CodeNode Microcontrollers is a Minecraft mod that adds mos6502 based microcontrollers to the Minecraft.");
        ImGui.text("You can automate your redstone contraptions with assembly code.");
        ImGui.newLine();

        ImGui.textColored(0xFF9292FF, "Legal Disclaimer:");
        ImGui.text("While this mod is under the GNU GPL v3 license, the vasm assembler is under a separate liscense.");
        if (ImGui.button(
                vasmLicenseLinkTimer.hasExpired() ? "View vasm License; Section 1.2" : "Opened page in browser")) {
            openWebpage("http://sun.hasenbraten.de/vasm/release/vasm.html");
            vasmLicenseLinkTimer.reset();
        }
        ImGui.newLine();

        ImGui.text("Docs: https://elmfrain.github.io/code-node-docs/");
        if (ImGui.button(docsLinkTimer.hasExpired() ? "Goto Docs" : "Opened page in browser")) {
            openWebpage("https://elmfrain.github.io/code-node-docs/");
            docsLinkTimer.reset();
        }
    }

    public static void genNanoDocumentation() {
        ImGui.text("Memory Map");
        ImGui.newLine();

        ImGui.text("The CNMCU Nano has a 64KB address space, with 8KB of ROM and 512 bytes of RAM.");

        if (ImGui.beginTable("Memory Map", 3, ImGuiTableFlags.Borders)) {
            ImGui.tableSetupColumn("Addresses");
            ImGui.tableSetupColumn("Name");
            ImGui.tableSetupColumn("Uses");
            ImGui.tableHeadersRow();

            ImGui.tableNextRow();
            ImGui.tableNextColumn();
            ImGui.textColored(0xFF00FF00, "$0000 - $01FF");
            ImGui.tableNextColumn();
            ImGui.text("RAM");
            ImGui.tableNextColumn();
            ImGui.text("256 byte Zero Page | 256 byte Stack");

            ImGui.tableNextRow();
            ImGui.tableNextColumn();
            ImGui.textColored(0xFF00FF00, "$7000 - $7070");
            ImGui.tableNextColumn();
            ImGui.text("GPIO");
            ImGui.tableNextColumn();
            ImGui.text("Pin Control/Interrupts");

            ImGui.tableNextRow();
            ImGui.tableNextColumn();
            ImGui.textColored(0xFF00FF00, "$E000 - $FFFF");
            ImGui.tableNextColumn();
            ImGui.text("ROM");
            ImGui.tableNextColumn();
            ImGui.text("Program Memory | Processor Vectors");

            ImGui.endTable();
        }

        ImGui.newLine();
        ImGui.text("Registers");
        ImGui.newLine();

        if (ImGui.collapsingHeader("GPIOPV (Pin Value Registers)")) {
            ImGui.text("There are 64 GPIOPV registers, each representing a pin. Only 4 are used.");
            ImGui.text("Read: Returns the value of the pin");
            ImGui.text("Write: Sets the value of the pin (if it is an output)");
            ImGui.text("Register Table");
            ImGui.newLine();

            if (ImGui.beginTable("GPIOPV", 4, ImGuiTableFlags.Borders)) {
                ImGui.tableSetupColumn("Address");
                ImGui.tableSetupColumn("Name");
                ImGui.tableSetupColumn("Description");
                ImGui.tableSetupColumn("Read/Write");
                ImGui.tableHeadersRow();

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7000");
                ImGui.tableNextColumn();
                ImGui.text("GPIOPV0");
                ImGui.tableNextColumn();
                ImGui.text("Front pin value");
                ImGui.tableNextColumn();
                ImGui.text("R/W");

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7001");
                ImGui.tableNextColumn();
                ImGui.text("GPIOPV1");
                ImGui.tableNextColumn();
                ImGui.text("Right pin value");
                ImGui.tableNextColumn();
                ImGui.text("R/W");

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7002");
                ImGui.tableNextColumn();
                ImGui.text("GPIOPV2");
                ImGui.tableNextColumn();
                ImGui.text("Back pin value");
                ImGui.tableNextColumn();
                ImGui.text("R/W");

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7003");
                ImGui.tableNextColumn();
                ImGui.text("GPIOPV3");
                ImGui.tableNextColumn();
                ImGui.text("Left pin value");
                ImGui.tableNextColumn();
                ImGui.text("R/W");

                ImGui.endTable();
            }
        }

        if (ImGui.collapsingHeader("GPIODIR (Pin Direction Registers)")) {
            ImGui.text("There are 8 GPIODIR registers, each bit representing a pin. Only 1 is used.");
            ImGui.text("Read: Returns the value of the register");
            ImGui.text("Write: Sets the value of the register");
            ImGui.text("Register Table");
            ImGui.newLine();

            if (ImGui.beginTable("GPIODIR", 4, ImGuiTableFlags.Borders)) {
                ImGui.tableSetupColumn("Address");
                ImGui.tableSetupColumn("Name");
                ImGui.tableSetupColumn("Description");
                ImGui.tableSetupColumn("Read/Write");
                ImGui.tableHeadersRow();

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7040");
                ImGui.tableNextColumn();
                ImGui.text("GPIODIR0");
                ImGui.tableNextColumn();
                ImGui.text("Pins (0 - 7) direction");
                ImGui.tableNextColumn();
                ImGui.text("R/W");

                ImGui.endTable();
            }

            ImGui.newLine();
            ImGui.text("GPIODIR0 Register (N/U - Not Used)");
            ImGui.newLine();

            if (ImGui.beginTable("GPIODIR0", 8, ImGuiTableFlags.Borders)) {
                ImGui.tableSetupColumn("Bit 7");
                ImGui.tableSetupColumn("6");
                ImGui.tableSetupColumn("5");
                ImGui.tableSetupColumn("4");
                ImGui.tableSetupColumn("3");
                ImGui.tableSetupColumn("2");
                ImGui.tableSetupColumn("1");
                ImGui.tableSetupColumn("0");
                ImGui.tableHeadersRow();

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.text("N/U");
                ImGui.tableNextColumn();
                ImGui.text("N/U");
                ImGui.tableNextColumn();
                ImGui.text("N/U");
                ImGui.tableNextColumn();
                ImGui.text("N/U");
                ImGui.tableNextColumn();
                ImGui.text("Left Dir");
                ImGui.tableNextColumn();
                ImGui.text("Back Dir");
                ImGui.tableNextColumn();
                ImGui.text("Right Dir");
                ImGui.tableNextColumn();
                ImGui.text("Front Dir");

                ImGui.endTable();
            }
        }

        if (ImGui.collapsingHeader("GPIOINT (Pin Interrupt Type Registers)")) {
            ImGui.text("There are 32 GPIOINT registers, each nibble representing a pin. Only 4 are used.");
            ImGui.text("Read: Returns the value of the register");
            ImGui.text("Write: Sets the value of the register");
            ImGui.text("Register Table");
            ImGui.newLine();

            if (ImGui.beginTable("GPIOINT", 4, ImGuiTableFlags.Borders)) {
                ImGui.tableSetupColumn("Address");
                ImGui.tableSetupColumn("Name");
                ImGui.tableSetupColumn("Description");
                ImGui.tableSetupColumn("Read/Write");
                ImGui.tableHeadersRow();

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7048");
                ImGui.tableNextColumn();
                ImGui.text("GPIOINT0");
                ImGui.tableNextColumn();
                ImGui.text("Front/Right interrupt type");
                ImGui.tableNextColumn();
                ImGui.text("R/W");

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7049");
                ImGui.tableNextColumn();
                ImGui.text("GPIOINT1");
                ImGui.tableNextColumn();
                ImGui.text("Back/Left interrupt type");
                ImGui.tableNextColumn();
                ImGui.text("R/W");

                ImGui.endTable();
            }

            ImGui.newLine();
            ImGui.text("Interrupt Types");
            ImGui.newLine();

            if (ImGui.beginTable("InterruptTypes", 3, ImGuiTableFlags.Borders)) {
                ImGui.tableSetupColumn("Value");
                ImGui.tableSetupColumn("Name");
                ImGui.tableSetupColumn("Description");
                ImGui.tableHeadersRow();

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$0");
                ImGui.tableNextColumn();
                ImGui.text("NO_INTERRUPT");
                ImGui.tableNextColumn();
                ImGui.text("Disable interupts");

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$1");
                ImGui.tableNextColumn();
                ImGui.text("LOW");
                ImGui.tableNextColumn();
                ImGui.text("Trigger while low");

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$2");
                ImGui.tableNextColumn();
                ImGui.text("HIGH");
                ImGui.tableNextColumn();
                ImGui.text("Trigger while high");

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$3");
                ImGui.tableNextColumn();
                ImGui.text("RISING");
                ImGui.tableNextColumn();
                ImGui.text("On rising edge");

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$4");
                ImGui.tableNextColumn();
                ImGui.text("FALLING");
                ImGui.tableNextColumn();
                ImGui.text("On falling edge");

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$5");
                ImGui.tableNextColumn();
                ImGui.text("CHANGE");
                ImGui.tableNextColumn();
                ImGui.text("Digital change");

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$6");
                ImGui.tableNextColumn();
                ImGui.text("ANALOG_CHANGE");
                ImGui.tableNextColumn();
                ImGui.text("On pin value change");

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7");
                ImGui.tableNextColumn();
                ImGui.text("ANALOG_RISING");
                ImGui.tableNextColumn();
                ImGui.text("On pin value increase");

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$8");
                ImGui.tableNextColumn();
                ImGui.text("ANALOG_FALLING");
                ImGui.tableNextColumn();
                ImGui.text("On pin value decrease");

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$9");
                ImGui.tableNextColumn();
                ImGui.text("NO_CHANGE");
                ImGui.tableNextColumn();
                ImGui.text("Trigger while no change");

                ImGui.endTable();
            }

            ImGui.newLine();
            ImGui.text("GPIOINT0 Register");
            ImGui.newLine();

            if (ImGui.beginTable("GPIOINT0", 2, ImGuiTableFlags.Borders)) {
                ImGui.tableSetupColumn("Bits 7 - 4");
                ImGui.tableSetupColumn("Bits 3 - 0");
                ImGui.tableHeadersRow();

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.text("Right Pin Interrupt Type");
                ImGui.tableNextColumn();
                ImGui.text("Front Pin Interrupt Type");

                ImGui.endTable();
            }

            ImGui.newLine();
            ImGui.text("GPIOINT1 Register");
            ImGui.newLine();

            if (ImGui.beginTable("GPIOINT1", 2, ImGuiTableFlags.Borders)) {
                ImGui.tableSetupColumn("Bits 7 - 4");
                ImGui.tableSetupColumn("Bits 3 - 0");
                ImGui.tableHeadersRow();

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.text("Left Pin Interrupt Type");
                ImGui.tableNextColumn();
                ImGui.text("Back Pin Interrupt Type");

                ImGui.endTable();
            }
        }

        if (ImGui.collapsingHeader("GPIOIFL (Pin Interrupt Flag Registers)")) {
            ImGui.text("There are 8 GPIOIFL registers, each bit representing a pin. Only 1 is used.");
            ImGui.text(
                    "If any flag is set, the IRQ line is held low. The flag must be cleared after the interrupt is handled.");
            ImGui.text("Read: Returns the value of the register");
            ImGui.text("Write: Clear bits of the register");
            ImGui.text("Register Table");
            ImGui.newLine();

            if (ImGui.beginTable("GPIOIFL", 4, ImGuiTableFlags.Borders)) {
                ImGui.tableSetupColumn("Address");
                ImGui.tableSetupColumn("Name");
                ImGui.tableSetupColumn("Description");
                ImGui.tableSetupColumn("Read/Write");
                ImGui.tableHeadersRow();

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7068");
                ImGui.tableNextColumn();
                ImGui.text("GPIOIFL0");
                ImGui.tableNextColumn();
                ImGui.text("Front/Right interrupt flag");
                ImGui.tableNextColumn();
                ImGui.text("R/W");

                ImGui.endTable();
            }

            ImGui.newLine();
            ImGui.text("GPIOIFL0 Register (N/U - Not Used)");
            ImGui.newLine();

            if (ImGui.beginTable("GPIOIFL0", 8, ImGuiTableFlags.Borders)) {
                ImGui.tableSetupColumn("Bit 7");
                ImGui.tableSetupColumn("6");
                ImGui.tableSetupColumn("5");
                ImGui.tableSetupColumn("4");
                ImGui.tableSetupColumn("3");
                ImGui.tableSetupColumn("2");
                ImGui.tableSetupColumn("1");
                ImGui.tableSetupColumn("0");
                ImGui.tableHeadersRow();

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.text("N/U");
                ImGui.tableNextColumn();
                ImGui.text("N/U");
                ImGui.tableNextColumn();
                ImGui.text("N/U");
                ImGui.tableNextColumn();
                ImGui.text("N/U");
                ImGui.tableNextColumn();
                ImGui.text("Left Flag");
                ImGui.tableNextColumn();
                ImGui.text("Back Flag");
                ImGui.tableNextColumn();
                ImGui.text("Right Flag");
                ImGui.tableNextColumn();
                ImGui.text("Front Flag");

                ImGui.endTable();
            }
        }

        ImGui.newLine();
        ImGui.text("More Info");
        ImGui.newLine();
        ImGui.text("For more information, please refer to the CodeNode Microcontrollers documentation.");
        ImGui.text("https://elmfrain.github.io/code-node-docs/");
        ImGui.sameLine();
        if (ImGui.button(docsLinkTimer.hasExpired() ? "Goto Docs" : "Opened page in browser")) {
            openWebpage("https://elmfrain.github.io/code-node-docs/");
            docsLinkTimer.reset();
        }
    }

    public static boolean openWebpage(String page) {
        try {
            URL url = new URL(page);

            String shell = NativesLoader.NATIVES_OS.equals("windows") ? "cmd" : "sh";
            String shellFlag = NativesLoader.NATIVES_OS.equals("windows") ? "/c" : "-c";
            String openBrowserCommand = NativesLoader.NATIVES_OS.equals("windows") ? "start"
                    : NativesLoader.NATIVES_OS.equals("macos") ? "open" : "xdg-open";

            ProcessBuilder builder = new ProcessBuilder(shell, shellFlag, openBrowserCommand + " " + url);
            builder.inheritIO();
            builder.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
