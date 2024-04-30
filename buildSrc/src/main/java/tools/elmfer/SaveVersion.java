package tools.elmfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class SaveVersion extends DefaultTask {

    public static final String GROUP = "build";
    public static final String DESCRIPTION = "Saves the version of the project to a list of files.";

    private Map<String, String> params = new HashMap<>();

    // @formatter:off
    private FileEntry[] files = {
            
        new FileEntry("src/main/java/com/elmfer/cnmcu/CodeNodeMicrocontrollers.java",
                new Entry("public static final String MOD_VERSION", ";",
                        "public static final String MOD_VERSION = \"%s-%s\"", "version", "mc_version")),
        
        new FileEntry("src/main/resources/fabric.mod.json",
                new Entry("\"version\":", ",",
                        "\"version\": \"%s-%s\"", "version", "mc_version"),
                new Entry("\"minecraft\":", ",",
                        "\"minecraft\": \"~%s\"", "mc_version")),
        
        new FileEntry("gradle.properties",
                new Entry("mod_version=", "\n",
                        "mod_version=%s", "version")),
    };
    // @formatter:on

    public SaveVersion() {
        this.setGroup(GROUP);
        this.setDescription(DESCRIPTION);
    }

    @TaskAction
    public void execute() throws Exception {
        if(System.getenv("MINECRAFT_VERSION") != null)
            params.put("mc_version", System.getenv("MINECRAFT_VERSION"));
        else
            params.put("mc_version", getProject().property("minecraft_version").toString());
        
        if(System.getenv("MOD_VERSION") != null)
            params.put("version", System.getenv("MOD_VERSION"));
        else
            params.put("version", getProject().property("mod_version").toString());

        for (FileEntry file : files) {
            File f = getProject().file(file.path);
            String content = readFile(f);
            
            if(f == null) {
                System.out.println("File not found: " + file.path + ", skipping...");
                continue;
            }

            for (Entry entry : file.entries) {
                String start = entry.start;
                String end = entry.end;

                String value = entry.format(params);
                content = content.replaceAll(start + ".*?" + end, value + end);
            }

            writeFile(f, content);
        }
    }

    private static String readFile(File file) {
        try {
            int fileSize = (int) file.length();
            byte[] buffer = new byte[fileSize];
            
            FileInputStream fis = new FileInputStream(file);
            fis.read(buffer);
            fis.close();
            
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private static void writeFile(File file, String content) {
        try {
            Files.write(file.toPath(), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Entry {
        private String start;
        private String end;
        private String format;
        private String params[];

        Entry(String start, String end, String format, String... params) {
            this.start = start;
            this.end = end;
            this.format = format;
            this.params = params;
        }
        
        String format(Map<String, String> globalParams) {
            ArrayList<String> params = new ArrayList<>();
            
            for(String param : this.params) {
                if(globalParams.containsKey(param))
                    params.add(globalParams.get(param));
                else
                    params.add("");
            }
            
            return String.format(format, params.toArray());
        }
    }

    private static class FileEntry {
        private String path;
        private Entry entries[];

        FileEntry(String path, Entry... entries) {
            this.path = path;
            this.entries = entries;
        }
    }
}
