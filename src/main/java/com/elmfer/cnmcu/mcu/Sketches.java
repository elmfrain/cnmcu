package com.elmfer.cnmcu.mcu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.concurrent.CompletableFuture;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;
import com.elmfer.cnmcu.config.Config;
import com.ibm.icu.util.Calendar;

import imgui.ImGui;
import imgui.extension.texteditor.TextEditor;

public class Sketches {

    public static final String SKETCHES_PATH = CodeNodeMicrocontrollers.MOD_ID + "/sketches";
    public static final String BACKUP_PATH = SKETCHES_PATH + "/backups";

    private static CompletableFuture<Void> backupSaveTask;
    
    private static TextEditor filePreview = new TextEditor();
    private static String[] backups = new String[0];
    private static long[] backupTimes = new long[0];
    
    static {
        filePreview.setShowWhitespaces(false);
        filePreview.setReadOnly(true);
    }

    public static void saveBackup(String code) {
        saveBackup(code, "s");
    }

    public static void saveBackup(String code, String fileExtension) {
        if (backupSaveTask != null && !backupSaveTask.isDone())
            backupSaveTask.join();

        backupSaveTask = CompletableFuture.runAsync(() -> {
            Path path = Paths.get(BACKUP_PATH, getBackupFileName() + "." + fileExtension);

            try {
                deleteOldestBackup();

                Files.write(path, code.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static String[] listBackups() {
        try {
            Path[] list = Files.list(Paths.get(BACKUP_PATH)).toArray(Path[]::new);
            
            backups = new String[list.length];
            backupTimes = new long[list.length];
            
            for (int i = 0; i < list.length; i++) {
                backups[i] = list[i].getFileName().toString();
                backupTimes[i] = Files.getLastModifiedTime(list[i]).toMillis();
            }
            
            // Sort backups by last modified time
            for (int i = 0; i < backups.length - 1; i++) {
                for (int j = i + 1; j < backups.length; j++) {
                    if (backupTimes[i] < backupTimes[j]) {
                        String tempBackup = backups[i];
                        backups[i] = backups[j];
                        backups[j] = tempBackup;

                        long tempTime = backupTimes[i];
                        backupTimes[i] = backupTimes[j];
                        backupTimes[j] = tempTime;
                    }
                }
            }
            
            return backups;
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return backups;
    }
    
    private static int selectedBackup = -1;
    public static void genLoadBackupUI() {
        float windowWidth = ImGui.getWindowWidth();
        
        ImGui.beginChild("##SketchBackups", windowWidth / 2, 0, false);
        
        ImGui.text("Latest Backups");
        ImGui.separator();
        
        ImGui.beginTable("##BackupsTable", 2);
        
        ImGui.tableSetupColumn("File Name", 0);
        ImGui.tableSetupColumn("From", 0);
        
        ImGui.tableHeadersRow();
        
        for (int i = 0; i < backups.length; i++) {
            ImGui.tableNextRow();

            ImGui.tableSetColumnIndex(0);
            if (ImGui.selectable(String.format("%02d %s", i + 1, backups[i]), selectedBackup == i)) {
                selectedBackup = i;
                filePreview.setText(loadBackup(backups[i]));
            }

            ImGui.tableSetColumnIndex(1);
            ImGui.text(formatTimeElapsed(backupTimes[i]));
        }
        
        ImGui.endTable();
        
        ImGui.endChild();
        
        ImGui.sameLine();
        
        filePreview.render("SketchBackupPreview");
    }
    
    public static String loadSketch(String path) {
        try {
            String code = Files.readString(Paths.get(path));
            return code;
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return "";
    }
    
    public static void saveSketch(String code, String path) {
        try {
            Files.write(Paths.get(path), code.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String loadBackup(String backupName) {
        try {
            return Files.readString(Paths.get(BACKUP_PATH, backupName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }
    
    public static String getSelectedBackup() {
        return filePreview.getText();
    }

    private static String formatTimeElapsed(long lastTime) {
        long time = System.currentTimeMillis() - lastTime;
        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0)
            return days + " days ago";
        if (hours > 0)
            return hours + " hours ago";
        if (minutes > 0)
            return minutes + " minutes ago";
        if (seconds > 0)
            return seconds + " seconds ago";

        return "Just now";
    }
    
    private static String getBackupFileName() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime());
    }

    private static void deleteOldestBackup() throws IOException {
        Path[] backups = Files.list(Paths.get(BACKUP_PATH)).toArray(Path[]::new);

        // If there are more than the maximum number of backups, delete the oldest one
        if (backups.length < Config.maxNumBackups())
            return;

        Path oldest = backups[0];
        long oldestTime = Files.getLastModifiedTime(oldest).toMillis();

        for (Path backup : backups) {
            long time = Files.getLastModifiedTime(backup).toMillis();

            if (time < oldestTime) {
                oldest = backup;
                oldestTime = time;
            }
        }

        Files.delete(oldest);
    }
}
