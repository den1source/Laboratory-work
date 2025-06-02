package org.example;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
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
        if (!nativeLibraryPath.startsWith("/")) {
            nativeLibraryPath = System.getProperty("user.dir") + "/" + nativeLibraryPath;
        }

        System.load(nativeLibraryPath);
        log.info("OpenCV library loaded successfully");
    }

    public Mat loadImage(String filePath) {
        Mat image = Imgcodecs.imread(filePath);
        if (image.empty()) {
            throw new IllegalArgumentException("Image not found at path: " + filePath);
        }
        return image;
    }

    public void zeroChannel(Mat image, int channel) {
        if (channel < 0 || channel >= image.channels()) {
            throw new IllegalArgumentException("Invalid channel index: " + channel);
        }
        int totalBytes = (int) (image.total() * image.elemSize());
        byte[] buffer = new byte[totalBytes];
        image.get(0, 0, buffer);

        for (int i = channel; i < totalBytes; i += image.channels()) {
            buffer[i] = 0;
        }

        image.put(0, 0, buffer);
    }

    public void showImage(Mat image) {
        int type = image.channels() > 1 ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;
        int bufferSize = image.channels() * image.cols() * image.rows();
        byte[] buffer = new byte[bufferSize];
        image.get(0, 0, buffer);

        BufferedImage bufferedImage = new BufferedImage(image.cols(), image.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);

        ImageIcon icon = new ImageIcon(bufferedImage);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(bufferedImage.getWidth() + 50, bufferedImage.getHeight() + 50);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void saveImage(String filePath, Mat image) {
        if (!Imgcodecs.imwrite(filePath, image)) {
            throw new RuntimeException("Failed to save image to path: " + filePath);
        }
    }
}