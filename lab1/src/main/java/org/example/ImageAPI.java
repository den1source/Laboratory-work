package org.example;

import java.nio.file.Paths;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageAPI {

    private static final Logger log = LoggerFactory.getLogger(ImageAPI.class);

    public enum OSType {
        WINDOWS, MACOS, LINUX, OTHER
    }

    public OSType getOperatingSystemType() {
        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((os.contains("mac")) || (os.contains("darwin"))) {
            return OSType.MACOS;
        } else if (os.contains("win")) {
            return OSType.WINDOWS;
        } else if (os.contains("nux")) {
            return OSType.LINUX;
        } else {
            return OSType.OTHER;
        }
    }

    public ImageAPI() throws Exception {
        OSType osType = getOperatingSystemType();
        String nativeLibraryPath;
        log.info("Checking OS.....");
        switch (osType) {
            case WINDOWS -> nativeLibraryPath = Config.getProp("pathToNativeLibWin");
            case MACOS -> nativeLibraryPath = Config.getProp("pathToNativeLibMac");
            case LINUX -> nativeLibraryPath = Config.getProp("pathToNativeLibLinux");
            default -> throw new Exception("Unsupported operating system");
        }

        if (nativeLibraryPath == null || nativeLibraryPath.isEmpty()) {
            throw new Exception("Native library path not found in configuration for OS: " + osType);
        }

        // Преобразование относительного пути в абсолютный
        if (!Paths.get(nativeLibraryPath).isAbsolute()) {
            nativeLibraryPath = Paths.get(System.getProperty("user.dir"), nativeLibraryPath).toAbsolutePath().toString();
        }

        System.load(nativeLibraryPath);
        log.info("OpenCV library loaded successfully");
    }
}