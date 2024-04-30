package tools.elmfer;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

import com.badlogic.gdx.jnigen.NativeCodeGenerator;

public class GenNativeSources extends DefaultTask {
    
    public static final String GROUP = "build";
    public static final String DESCRIPTION = "Creates JNI bridge .h and .cpp files.";
    
    private final String classPath;
    
    @Internal
    private String sourceDir;
    @Internal
    private String bridgeDir;
    
    public GenNativeSources() {
        String pathSeparator = getOS().equals("windows") ? ";" : ":";
        
        classPath = getProject().getConfigurations().getByName("runtimeClasspath").getAsPath() +
                pathSeparator + "build/classes/java/main";
        
        this.setGroup(GROUP);
        this.setDescription(DESCRIPTION);
    }
    
    @TaskAction
    void execute() throws Exception {
        if(sourceDir == null)
            throw new RuntimeException("You must specify source directory for generating native source files!");
        
        if (bridgeDir == null)
            throw new RuntimeException("You must specify bridge directory for generating native source files!");
        
        NativeCodeGenerator srcGen = new NativeCodeGenerator();     
        srcGen.generate(sourceDir, classPath, bridgeDir);
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getBridgeDir() {
        return bridgeDir;
    }

    public void setBridgeDir(String bridgeDir) {
        this.bridgeDir = bridgeDir;
    }
    
    private String getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win"))
            return "windows";
        else if (os.contains("mac"))
            return "macos";
        else if (os.contains("nix") || os.contains("nux"))
            return "linux";

        return "unknown";
    }
}
