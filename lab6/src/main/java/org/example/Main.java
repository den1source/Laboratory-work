package org.example;

public class Main {
    public static void main(String[] args) {
        try {
            // Создаем экземпляр вашего API
            ImageAPI api = new ImageAPI();
            System.out.println("OpenCV successfully initialized!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




