package ru.otus.java.basic.homeworks.hw20;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class CountOccurrences {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Введите имя файла: ");
            String fileName = scanner.nextLine();

            System.out.print("Введите искомую последовательность символов: ");
            String searchString = scanner.nextLine();

            int count = countOccurrences(fileName, searchString);

            System.out.println("Количество вхождений: " + count);
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    public static int countOccurrences(String fileName, String searchString) throws IOException {
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int index = 0;
                while ((index = line.indexOf(searchString, index)) != -1) {
                    count++;
                    index += searchString.length();
                }
            }
        }
        return count;
    }
}