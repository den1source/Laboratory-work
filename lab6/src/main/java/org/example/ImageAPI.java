package org.example;

import java.util.List;
import java.util.Locale;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
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
            case WINDOWS -> nativeLibraryPath = Config.getAbsolutePath(Config.getProp("pathToNativeLibWin"));
            case MACOS -> nativeLibraryPath = Config.getAbsolutePath(Config.getProp("pathToNativeLibMac"));
            case LINUX -> nativeLibraryPath = Config.getAbsolutePath(Config.getProp("pathToNativeLibLinux"));
            default -> throw new Exception("Unsupported operating system");
        }

        if (nativeLibraryPath == null || nativeLibraryPath.isEmpty()) {
            throw new Exception("Native library path not found in configuration for OS: " + osType);
        }

        System.load(nativeLibraryPath);
        log.info("OpenCV library loaded successfully");
    }

    public Mat detectEdges(String imagePath, double threshold1, double threshold2) {
        Mat srcImage = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_COLOR);
        Mat grayImage = new Mat();
        Imgproc.cvtColor(srcImage, grayImage, Imgproc.COLOR_BGR2GRAY);
        Mat detectedEdges = new Mat();
        Imgproc.blur(grayImage, detectedEdges, new Size(3, 3));
        Imgproc.Canny(detectedEdges, detectedEdges, threshold1, threshold2);
        return detectedEdges;
    }

    public Mat pyramidDown(Mat image, int levels) {
        Mat result = image.clone();
        for (int i = 0; i < levels; i++) {
            Imgproc.pyrDown(result, result);
        }
        return result;
    }

    public Mat pyramidUp(Mat image, int levels) {
        Mat result = image.clone();
        for (int i = 0; i < levels; i++) {
            Imgproc.pyrUp(result, result);
        }
        return result;
    }

    public Mat getImageFragmentAfterPyramidOperations(Mat image, int levels, Rect region) {
        Mat downImage = pyramidDown(image, levels);
        Mat upImage = pyramidUp(downImage, levels);
        return new Mat(upImage, region);
    }

    public Mat subtractImages(Mat image1, Mat image2) {
        Mat result = new Mat();
        Core.subtract(image1, image2, result);
        return result;
    }

    public int countRectangles(String imagePath, int targetWidth, int targetHeight) {
        Mat srcImage = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_COLOR);
        Mat grayImage = new Mat();
        Imgproc.cvtColor(srcImage, grayImage, Imgproc.COLOR_BGR2GRAY);
        Mat edges = new Mat();
        Imgproc.Canny(grayImage, edges, 50, 150);
        List<MatOfPoint> contours = new java.util.ArrayList<>();
        Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        int count = 0;
        double tolerance = 0.2; // Допустимая погрешность (20%)
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            if (Math.abs(rect.width - targetWidth) <= targetWidth * tolerance &&
                Math.abs(rect.height - targetHeight) <= targetHeight * tolerance) {
                count++;
            }
        }
        return count;
    }
}