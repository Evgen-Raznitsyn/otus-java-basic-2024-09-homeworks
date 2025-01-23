package ru.otus.java.basic.homeworks.hw21;

import java.util.concurrent.*;

public class ThreadPoolExample {
    private static final Object lock = new Object();
    private static int count = 0;

    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newFixedThreadPool(3)) {

            Runnable taskA = () -> printLetter('A', 5);
            Runnable taskB = () -> printLetter('B', 5);
            Runnable taskC = () -> printLetter('C', 5);

            executor.submit(taskA);
            executor.submit(taskB);
            executor.submit(taskC);

            executor.shutdown();
        }
    }

    private static void printLetter(char letter, int times) {
        for (int i = 0; i < times; i++) {
            synchronized (lock) {
                while (count % 3 != getLetterIndex(letter)) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
                System.out.print(letter);
                count++;
                lock.notifyAll();
            }
        }
    }

    private static int getLetterIndex(char letter) {
        switch (letter) {
            case 'A':
                return 0;
            case 'B':
                return 1;
            case 'C':
                return 2;
            default:
                throw new IllegalArgumentException("Invalid letter: " + letter);
        }
    }
}
