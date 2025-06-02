package org.example;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

public class ImageAPITest {

    @Test
    public void testFloodFill() throws Exception {
        ImageAPI api = new ImageAPI();
        String imagePath = new File("Images/1/1.jpeg").getAbsolutePath(); // Убираем двойное добавление корневого пути
        api.floodFillImage(imagePath, new Point(0, 0), new Scalar(0, 255, 0), null, null);
        // Проверить результат вручную
    }

    @Test
    public void testPyramidOperations() throws Exception {
        ImageAPI api = new ImageAPI();
        String imagePath = new File("Images/1/1.jpeg").getAbsolutePath(); // Убираем двойное добавление корневого пути
        api.pyramidOperations(imagePath, 2, true); // Понижение
        api.pyramidOperations(imagePath, 2, false); // Повышение
        // Проверить результат вручную
    }

    @Test
    public void testIdentifyRectangles() throws Exception {
        ImageAPI api = new ImageAPI();
        String imagePath = new File("Images/1/1.jpeg").getAbsolutePath(); // Убираем двойное добавление корневого пути
        api.identifyRectangles(imagePath, 17, 6);
        // Проверить результат вручную
    }
}
