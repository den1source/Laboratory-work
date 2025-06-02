package org.example;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

public class ImageAPITest {

    private static final String BASE_PATH = Config.getAbsolutePath("Images/");

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
    public void testDetectEdges() throws Exception {
        ImageAPI api = new ImageAPI();
        Mat edges = api.detectEdges(BASE_PATH + "1.jpeg", 50, 150);
        assertNotNull(edges, "Edges should not be null");
        Imgcodecs.imwrite(BASE_PATH + "output_edges.jpg", edges);
    }

    @Test
    public void testPyramidOperations() throws Exception {
        ImageAPI api = new ImageAPI();
        Mat srcImage = Imgcodecs.imread(BASE_PATH + "1.jpeg");
        Mat downImage = api.pyramidDown(srcImage, 2);
        Mat upImage = api.pyramidUp(downImage, 2);

        assertNotNull(downImage, "Downscaled image should not be null");
        assertNotNull(upImage, "Upscaled image should not be null");

        Imgcodecs.imwrite(BASE_PATH + "output_down.jpg", downImage);
        Imgcodecs.imwrite(BASE_PATH + "output_up.jpg", upImage);
    }

    @Test
    public void testCountRectangles() throws Exception {
        ImageAPI api = new ImageAPI();
        int count = api.countRectangles(BASE_PATH + "1.jpeg", 50, 50);
        System.out.println("Found rectangles: " + count);
        assertTrue(count >= 0, "Rectangle count should be non-negative");
    }
}
