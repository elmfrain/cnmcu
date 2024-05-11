package com.elmfer.cnmcu.ui;

import java.net.URL;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;
import com.elmfer.cnmcu.animation.Timer;
import com.elmfer.cnmcu.config.ModSetup;
import com.elmfer.cnmcu.cpp.NativesLoader;

import imgui.ImGui;
import imgui.flag.ImGuiTableFlags;

public class QuickReferences {

    private static final Timer vasmLicenseLinkTimer = new Timer(3).expire();
    private static final Timer docsLinkTimer = new Timer(3).expire();

    private QuickReferences() {

    }

    public static void genUpdates() {
        if (!ModSetup.wasAbleToCheckForUpdates()) {
            ImGui.textColored(0xFFA2A2FF, "Failed to check for updates!");
            ImGui.text("No internet connection or GitHub is down.");
            return;
        }
        
        if (!ModSetup.hasCheckedForUpdates()) {
            ImGui.text("Checking for updates...");
            ModSetup.checkForUpdatesAsync();
            return;
        }

        if (ModSetup.isUpdateAvailable())
            ImGui.textColored(0xFF00FF00, "An update is available!");
        else
            ImGui.textColored(0xFFFFA2A2, "CodeNode Microcontrollers is up to date!");
        
        ImGui.newLine();

        String title = String.format("CodeNode Microcontrollers v%s for Minecraft %s", ModSetup.getLatestVersion(),
                String.join(", ", ModSetup.getLatestForMinecraftVersions()));
        ImGui.text(title);

        ImGui.separator();
        
        ImGui.textWrapped(ModSetup.getChangelog());
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
        
        if (ImGui.collapsingHeader("EL (Event Listener)")) {
            ImGui.text("The Event Listener allows for the microcontroller to listen to Minecraft events.");
            ImGui.text("Currently, the module is only used for notifying game ticks.");
            ImGui.text("Register Table");
            
            if (ImGui.beginTable("EL", 4, ImGuiTableFlags.Borders)) {
                ImGui.tableSetupColumn("Address");
                ImGui.tableSetupColumn("Name");
                ImGui.tableSetupColumn("Description");
                ImGui.tableSetupColumn("Read/Write");
                ImGui.tableHeadersRow();

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7100");
                ImGui.tableNextColumn();
                ImGui.text("ELICL");
                ImGui.tableNextColumn();
                ImGui.text("Interrupt Control");
                ImGui.tableNextColumn();
                ImGui.text("R/W");
                
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7108");
                ImGui.tableNextColumn();
                ImGui.text("ELIFL");
                ImGui.tableNextColumn();
                ImGui.text("Interrupt Flag");
                ImGui.tableNextColumn();
                ImGui.text("R/W");
                ImGui.endTable();
            }
            
            ImGui.newLine();
            ImGui.text("ELICL Register - (N/U - Not Used)");
            ImGui.text("If the bit of the corresponding event is set, the interrupt is enabled.");
            ImGui.newLine();
            
            if (ImGui.beginTable("ELICL", 8, ImGuiTableFlags.Borders)) {
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
                ImGui.text("N/U");
                ImGui.tableNextColumn();
                ImGui.text("N/U");
                ImGui.tableNextColumn();
                ImGui.text("N/U");
                ImGui.tableNextColumn();
                ImGui.text("Game Tick");
                
                
                ImGui.endTable();
            }
            
            ImGui.newLine();
            ImGui.text("ELIFL Register - (N/U - Not Used)");
            ImGui.text("If the bit of the corresponding event is set, the event has occurred.");
            ImGui.text("Write to clear the flag, it does not automatically clear.");
            ImGui.newLine();
            
            if (ImGui.beginTable("ELIFL", 8, ImGuiTableFlags.Borders)) {
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
                ImGui.text("N/U");
                ImGui.tableNextColumn();
                ImGui.text("N/U");
                ImGui.tableNextColumn();
                ImGui.text("N/U");
                ImGui.tableNextColumn();
                ImGui.text("Game Tick");
                
                ImGui.endTable();
            }
        }
        
        if (ImGui.collapsingHeader("UART (Universal Asynchronous Receiver/Transmitter)")) {
            ImGui.text("The UART module allows for serial communication between microcontrollers.");
            ImGui.text("This mostly follows the same design as the 6551 ACIA.");
            ImGui.newLine();
            ImGui.text("Register Table");
            
            if (ImGui.beginTable("UART", 4, ImGuiTableFlags.Borders)) {
                ImGui.tableSetupColumn("Address");
                ImGui.tableSetupColumn("Name");
                ImGui.tableSetupColumn("Description");
                ImGui.tableSetupColumn("Read/Write");
                ImGui.tableHeadersRow();

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7200");
                ImGui.tableNextColumn();
                ImGui.text("UARTSTS");
                ImGui.tableNextColumn();
                ImGui.text("Status Register");
                ImGui.tableNextColumn();
                ImGui.text("R");

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7201");
                ImGui.tableNextColumn();
                ImGui.text("UARTCTL");
                ImGui.tableNextColumn();
                ImGui.text("Control Register");
                ImGui.tableNextColumn();
                ImGui.text("R/W");

                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7202");
                ImGui.tableNextColumn();
                ImGui.text("UARTCMD");
                ImGui.tableNextColumn();
                ImGui.text("Command Register");
                ImGui.tableNextColumn();
                ImGui.text("R/W");
                
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7203");
                ImGui.tableNextColumn();
                ImGui.text("UARTTXD");
                ImGui.tableNextColumn();
                ImGui.text("Transmit Data Register");
                ImGui.tableNextColumn();
                ImGui.text("R/W");
                
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7204");
                ImGui.tableNextColumn();
                ImGui.text("UARTRXD");
                ImGui.tableNextColumn();
                ImGui.text("Receive Data Register");
                ImGui.tableNextColumn();
                ImGui.text("R/W");
                
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7205");
                ImGui.tableNextColumn();
                ImGui.text("UARTTXP");
                ImGui.tableNextColumn();
                ImGui.text("TX Pin Register");
                ImGui.tableNextColumn();
                ImGui.text("R/W");
                
                ImGui.tableNextRow();
                ImGui.tableNextColumn();
                ImGui.textColored(0xFF00FF00, "$7206");
                ImGui.tableNextColumn();
                ImGui.text("UARTRXP");
                ImGui.tableNextColumn();
                ImGui.text("RX Pin Register");
                ImGui.tableNextColumn();
                ImGui.text("R/W");

                ImGui.endTable();
                
                ImGui.newLine();
                ImGui.text("UARTSTS Register");
                ImGui.newLine();
                
                if (ImGui.beginTable("UARTSTS", 8, ImGuiTableFlags.Borders)) {
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
                    ImGui.text("IRQ");
                    ImGui.tableNextColumn();
                    ImGui.text("N/U");
                    ImGui.tableNextColumn();
                    ImGui.text("N/U");
                    ImGui.tableNextColumn();
                    ImGui.text("TX Empty");
                    ImGui.tableNextColumn();
                    ImGui.text("RX Full");
                    ImGui.tableNextColumn();
                    ImGui.text("Overrun Error");
                    ImGui.tableNextColumn();
                    ImGui.text("Frame Error");
                    ImGui.tableNextColumn();
                    ImGui.text("Parity Error");
                    
                    ImGui.endTable();
                }
                
                ImGui.newLine();
                ImGui.text("UARTCTL Register");
                ImGui.text("Configure module's baud rate, word length, and stop bit.");
                ImGui.textColored(0xFF00FF00, "Two microcontrollers must have the same settings to communicate.");
                ImGui.newLine();
                
                if (ImGui.beginTable("UARTCTL", 3, ImGuiTableFlags.Borders)) {
                    ImGui.tableSetupColumn("Bit 7");
                    ImGui.tableSetupColumn("6 - 4 (WRDLEN)");
                    ImGui.tableSetupColumn("3 - 0");
                    ImGui.tableHeadersRow();
                    
                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.text("Stop Bit");
                    ImGui.tableNextColumn();
                    ImGui.text("Word Length");
                    ImGui.tableNextColumn();
                    ImGui.text("Baud Rate");
                    
                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.text("1 - 2 Stop Bits");
                    ImGui.tableNextColumn();
                    ImGui.text("1 - 8 Bits (WRDLEN + 1)");
                    ImGui.tableNextColumn();
                    ImGui.text("Reference Baud Rate Table");
                    
                    ImGui.endTable();
                }
                
                ImGui.newLine();
                ImGui.text("Baud Rate Table");
                ImGui.newLine();
                
                if (ImGui.beginTable("BaudRate", 2, ImGuiTableFlags.Borders)) {
                    ImGui.tableSetupColumn("Value");
                    ImGui.tableSetupColumn("Baud Rate");
                    ImGui.tableHeadersRow();

                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$0");
                    ImGui.tableNextColumn();
                    ImGui.text("1");

                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$1");
                    ImGui.tableNextColumn();
                    ImGui.text("2");

                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$2");
                    ImGui.tableNextColumn();
                    ImGui.text("5");

                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$3");
                    ImGui.tableNextColumn();
                    ImGui.text("10");

                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$4");
                    ImGui.tableNextColumn();
                    ImGui.text("20");
                    
                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$5");
                    ImGui.tableNextColumn();
                    ImGui.text("50");
                    
                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$6");
                    ImGui.tableNextColumn();
                    ImGui.text("150");
                    
                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$7");
                    ImGui.tableNextColumn();
                    ImGui.text("300");
                    
                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$8");
                    ImGui.tableNextColumn();
                    ImGui.text("600");
                    
                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$9");
                    ImGui.tableNextColumn();
                    ImGui.text("1200");
                    
                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$A");
                    ImGui.tableNextColumn();
                    ImGui.text("1800");
                    
                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$B");
                    ImGui.tableNextColumn();
                    ImGui.text("2400");
                    
                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$C");
                    ImGui.tableNextColumn();
                    ImGui.text("3600");
                    
                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$D");
                    ImGui.tableNextColumn();
                    ImGui.text("4800");
                    
                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$E");
                    ImGui.tableNextColumn();
                    ImGui.text("7200");
                    
                    ImGui.tableNextRow();
                    ImGui.tableNextColumn();
                    ImGui.textColored(0xFF00FF00, "$F");
                    ImGui.tableNextColumn();
                    ImGui.text("9600");

                    ImGui.endTable();
                }
            }
            
            ImGui.newLine();
            ImGui.text("UARTCMD Register");
            ImGui.text("Configure how module deals with data.");
            ImGui.text("TX Pin Enable: Pin must be set to output in GPIODIR0 register.");
            ImGui.text("If TX Pin Enable is set, the module will output data to the pin, overriding the GPIO module.");
            ImGui.text("RX Pin Enable: Pin must be set to input in GPIODIR0 register.");
            ImGui.textColored(0xFF00FF00, "Two microcontrollers must have the parity settings to communicate.");
            ImGui.newLine();
            
            if (ImGui.beginTable("UARTCMD", 8, ImGuiTableFlags.Borders)) {
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
                ImGui.text("TX Pin Enable");
                ImGui.tableNextColumn();
                ImGui.text("RX Pin Enable");
                ImGui.tableNextColumn();
                ImGui.text("Parity Type");
                ImGui.tableNextColumn();
                ImGui.text("Parity Enable");
                ImGui.tableNextColumn();
                ImGui.text("Echo Enable");
                ImGui.tableNextColumn();
                ImGui.text("TX Interrupts");
                ImGui.tableNextColumn();
                ImGui.text("RX Interrupts");
                
                ImGui.endTable();
            }
            
            ImGui.newLine();
            ImGui.text("UARTTXD Register");
            ImGui.text("Write to start transmitting data. Causes overrun error if TX buffer is not empty.");
            ImGui.newLine();
            ImGui.text("UARTRXD Register");
            ImGui.text("Read to get received data. Causes overrun error if RX buffer if a byte was not read in time.");
            ImGui.newLine();
            ImGui.text("UARTTXP Register");
            ImGui.text("Set which pin is used for transmitting data.");
            ImGui.newLine();
            ImGui.text("UARTRXP Register");
            ImGui.text("Set which pin is used for receiving data.");
            
            ImGui.textColored(0xFF00FF00, "More info is coming soon to the mod's official docs!");
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
