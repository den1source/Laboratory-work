package org.example;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Random;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
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
            case WINDOWS -> nativeLibraryPath = Config.getProp("pathToNativeLibWin");
            case MACOS -> nativeLibraryPath = Config.getProp("pathToNativeLibMac");
            case LINUX -> nativeLibraryPath = Config.getProp("pathToNativeLibLinux");
            default -> throw new Exception("Unsupported operating system");
        }

        if (nativeLibraryPath == null || nativeLibraryPath.isEmpty()) {
            throw new Exception("Native library path not found in configuration for OS: " + osType);
        }

        // Преобразуем относительный путь в абсолютный
        File libFile = new File(System.getProperty("user.dir"), nativeLibraryPath);
        System.load(libFile.getAbsolutePath());
        log.info("OpenCV library loaded successfully");
    }

    public void floodFillImage(String imagePath, Point seedPoint, Scalar newVal, Scalar loDiff, Scalar upDiff) {
        Mat srcImage = Imgcodecs.imread(imagePath); // Убираем добавление корневого пути
        Mat mask = new Mat();
        if (loDiff == null || upDiff == null) {
            Random random = new Random();
            loDiff = new Scalar(random.nextInt(50), random.nextInt(50), random.nextInt(50));
            upDiff = new Scalar(random.nextInt(50), random.nextInt(50), random.nextInt(50));
        }
        Imgproc.floodFill(srcImage, mask, seedPoint, newVal, new Rect(), loDiff, upDiff, Imgproc.FLOODFILL_FIXED_RANGE);
        File outputFile = new File("Images/1/floodFillResult.jpg");
        Imgcodecs.imwrite(outputFile.getAbsolutePath(), srcImage);
    }

    public void pyramidOperations(String imagePath, int levels, boolean isDown) {
        Mat srcImage = Imgcodecs.imread(imagePath); // Убираем добавление корневого пути
        Mat result = srcImage.clone();
        for (int i = 0; i < levels; i++) {
            if (isDown) {
                Imgproc.pyrDown(result, result);
            } else {
                Imgproc.pyrUp(result, result);
            }
        }
        File outputFile = new File(isDown ? "Images/1/pyrDownResult.jpg" : "Images/1/pyrUpResult.jpg");
        Imgcodecs.imwrite(outputFile.getAbsolutePath(), result);
    }

    public void identifyRectangles(String imagePath, int width, int height) {
        Mat srcImage = Imgcodecs.imread(imagePath); // Убираем добавление корневого пути
        Mat grayImage = new Mat();
        Imgproc.cvtColor(srcImage, grayImage, Imgproc.COLOR_BGR2GRAY);
        Mat thresholdImage = new Mat();
        Imgproc.threshold(grayImage, thresholdImage, 50, 255, Imgproc.THRESH_BINARY);
        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(thresholdImage, contours, new Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        contours.sort(Collections.reverseOrder(Comparator.comparing(Imgproc::contourArea)));

        int count = 0;
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            System.out.println("Found rectangle: width=" + rect.width + ", height=" + rect.height);
            if (rect.width == width && rect.height == height) {
                count++;
            }
        }
        System.out.println("Found " + count + " rectangles of size " + width + "x" + height);
        File outputFile = new File("Images/1/rectanglesResult.jpg");
        Imgcodecs.imwrite(outputFile.getAbsolutePath(), srcImage);
    }
}