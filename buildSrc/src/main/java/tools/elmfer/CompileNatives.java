package tools.elmfer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.Predicate;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

public class CompileNatives extends DefaultTask {

    private static final Predicate<Path> LIBS_FILTER = path -> {
        String name = path.getFileName().toString();
        return name.endsWith(".so") || name.endsWith(".dll") || name.endsWith(".dylib");
    };

    @Internal
    private String sourceDir;
    @Internal
    private String buildDir;
    @Internal
    private String cmakeTarget;
    @Internal
    private String targetDir;
    @Internal
    private String buildType;

    private IOException copyError;

    public CompileNatives() {
        this.setGroup("build");
        this.setDescription("Compiles native source files using CMake.");

        cmakeTarget = "cnmcu-natives";
        buildType = "Release";
    }

    @TaskAction
    void execute() throws Exception {
        if (sourceDir == null)
            throw new RuntimeException("You must specify source directory for generating native source files!");

        if (buildDir == null)
            throw new RuntimeException("You must specify build directory for generating native source files!");

        executeCommand("cmake --version", "CMake is not installed on your system!");

        String absSourceDir = getProject().file(sourceDir).getAbsolutePath();
        String absBuildDir = getProject().file(buildDir).getAbsolutePath();

        executeCommand("cmake -S " + absSourceDir + " -B " + absBuildDir + " -Wno-dev -DCMAKE_BUILD_TYPE=" + buildType,
                "Error configuring CMake project!");

        executeCommand("cmake --build " + absBuildDir + " --parallel --target " + cmakeTarget + " --config " + buildType,
                "Error compiling native source files!");

        boolean inProduction = System.getenv("PRODUCTION") != null;
        if (!inProduction && targetDir != null)
            copyBinaries();
    }

    public String getBuildType() {
        return buildType;
    }

    public void setBuildType(String buildType) {
        this.buildType = buildType;
    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getBuildDir() {
        return buildDir;
    }

    public void setBuildDir(String buildDir) {
        this.buildDir = buildDir;
    }

    public String getCmakeTarget() {
        return cmakeTarget;
    }

    public void setCmakeTarget(String cmakeTarget) {
        this.cmakeTarget = cmakeTarget;
    }

    public String getTargetDir() {
        return targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    private int executeCommand(String command, String failMessage) throws Exception {
        String os = getOS();
        String shell = os.equals("windows") ? "cmd" : "sh";
        String shellFlag = os.equals("windows") ? "/c" : "-c";

        ProcessBuilder builder = new ProcessBuilder(shell, shellFlag, command);
        builder.redirectErrorStream(true);

        Process process = builder.start();

        Thread outThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null)
                    System.out.println("[cmake] " + line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        outThread.start();

        int exitCode = process.waitFor();
        outThread.join();

        if (exitCode != 0)
            throw new RuntimeException(failMessage);

        return exitCode;
    }

    // Copy binaries to target directory, if specified.
    // This is useful when you want to copy the compiled binaries to
    // quickly to aid in development
    private void copyBinaries() throws IOException {
        copyError = null;

        Path targetPath = getProject().file(targetDir).toPath();

        Path buildDir = getProject().file(this.buildDir).toPath();
        
        

        Files.walk(buildDir).filter(LIBS_FILTER).forEach(source -> {
            try {
                Path target = targetPath.resolve(source.getFileName());

                Files.createDirectories(target.getParent());
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                copyError = e;
            }
        });

        if (copyError != null)
            throw copyError;
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
