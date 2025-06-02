package org.example;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.opencv.imgproc.Imgproc;

public class ImageAPITest {

    @Test
    public void testOpenCVInitialization() {
        try {
            ImageAPI api = new ImageAPI();
            String os = api.getOperatingSystemType().name();
            String version = org.opencv.core.Core.getVersionString();

            System.out.println("OS version - " + os);
            System.out.println("Open CV version - " + version);

            assertNotNull(version, "OpenCV version should not be null");
            assertFalse(version.isEmpty(), "OpenCV version should not be empty");
        } catch (Exception e) {
            fail("OpenCV initialization failed: " + e.getMessage());
        }
    }

    @Test
    public void testApplyFilters() throws Exception {
        ImageAPI api = new ImageAPI();
        String imagePath = Config.getAbsolutePath("Images/1/1.jpeg"); // Укажите путь к тестовому изображению
        int[] kernelSizes = {3, 5, 7};

        for (int size : kernelSizes) {
            api.applyFilters(imagePath, size);
        }
    }

    @Test
    public void testPerformMorphology() throws Exception {
        ImageAPI api = new ImageAPI();
        String imagePath = Config.getAbsolutePath("Images/2/2.jpeg"); // Укажите путь к тестовому изображению с текстом
        int[] kernelSizes = {3, 5, 7, 9, 13, 15};
        int[] morphTypes = {
            Imgproc.MORPH_RECT,
            Imgproc.MORPH_ELLIPSE,
            Imgproc.MORPH_CROSS
        };

        api.performMorphology(imagePath, kernelSizes, morphTypes);
    }
}
