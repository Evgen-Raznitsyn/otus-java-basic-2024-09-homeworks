package ru.otus.java.basic.homeworks.hw14;

public class MultiThreadedArrayComputation {

    private static final int SIZE = 100_000_000;
    private static final int NUM_THREADS = 4;

    public static void main(String[] args) {
        double[] array = new double[SIZE];

        long startTime = System.nanoTime();

        Thread[] threads = new Thread[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                int start = threadIndex * (SIZE / NUM_THREADS);
                int end = start + (SIZE / NUM_THREADS);
                for (int j = start; j < end; j++) {
                    array[j] = 1.14 * Math.cos(j) * Math.sin(j * 0.2) * Math.cos(j / 1.2);
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < NUM_THREADS; i++) {
            try {
                for (Thread thread : threads) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Ожидание потока было прервано: " + e.getMessage());
            }
        }

        long endTime = System.nanoTime();

        long duration = endTime - startTime;
        System.out.println("Время выполнения с 4 потоками: " + duration + " миллисекунд");
    }
}