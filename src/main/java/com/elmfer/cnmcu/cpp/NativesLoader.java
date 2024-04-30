package com.elmfer.cnmcu.cpp;

import com.elmfer.cnmcu.CodeNodeMicrocontrollers;

public class NativesLoader {

    private NativesLoader() {
    }

    public static final String NATIVES_OS = getOS();
    public static final String NATIVES_PLATFORM = getPlatform();
    public static final String NATIVES_BITS = getBits();
    public static final String NATIVES_EXT = getExtension();
    public static final String EXE_EXT = NATIVES_OS.equals("windows") ? ".exe" : "";
    public static final String BINARIES_PATH = CodeNodeMicrocontrollers.MOD_ID + "/natives";

    private static boolean loaded = false;

    public static void loadNatives() {
        if (loaded)
            return;

        CodeNodeMicrocontrollers.LOGGER.info("Loading native library...");

        if (NATIVES_OS.equals("unknown") || NATIVES_PLATFORM.equals("unknown") || NATIVES_BITS.equals("unknown"))
            throw new RuntimeException("Unable to use " + CodeNodeMicrocontrollers.MOD_NAME + " on this platform!");

        String libName = getBinaryFilename();
        String workingDir = System.getProperty("user.dir") + "/";
        String libPath = workingDir + BINARIES_PATH + "/" + libName;

        try {
            System.load(libPath);
            loaded = true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load native library: " + libPath, e);
        }
    }

    public static String getBinaryFilename() {
        return "lib" + CodeNodeMicrocontrollers.MOD_ID + "-" + NATIVES_OS + "-" + NATIVES_PLATFORM + NATIVES_BITS
                + NATIVES_EXT;
    }

    public static String getExecutableFilename(String name) {
        return name + "-" + NATIVES_OS + "-" + NATIVES_PLATFORM + NATIVES_BITS + EXE_EXT;
    }
    
    private static String getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win"))
            return "windows";
        else if (os.contains("mac"))
            return "macos";
        else if (os.contains("nix") || os.contains("nux"))
            return "linux";

        return "unknown";
    }

    private static String getPlatform() {
        String arch = System.getProperty("os.arch").toLowerCase();

        if (arch.matches("arm") || arch.matches("aarch64"))
            return "arm";
        else if (arch.matches("x86") || arch.matches("i386") || arch.matches("i686") || arch.matches("amd64")
                || arch.matches("x86_64"))
            return "x";

        return "unknown";
    }

    private static String getBits() {
        String arch = System.getProperty("os.arch").toLowerCase();

        if (arch.matches("x86") || arch.matches("i386") || arch.matches("arm")) {
            if (NATIVES_PLATFORM.equals("x"))
                return "86";
            return "32";
        } else if (arch.matches("amd64") || arch.matches("x86_64") || arch.matches("aarch64"))
            return "64";

        return "unknown";
    }

    private static String getExtension() {
        if (NATIVES_OS.equals("windows"))
            return ".dll";
        else if (NATIVES_OS.equals("macos"))
            return ".dylib";

        return ".so";
    }
}
