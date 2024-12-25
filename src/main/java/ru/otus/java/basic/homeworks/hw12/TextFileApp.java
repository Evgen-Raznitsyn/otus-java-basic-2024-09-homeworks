package ru.otus.java.basic.homeworks.hw12;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TextFileApp {
    private static final String FILE_DIRECTORY = "src/main/java/ru/otus/java/basic/homeworks/hw12/files/";

    public static void main(String[] args) {
        List<String> textFiles = listTextFiles();

        if (textFiles.isEmpty()) {
            System.out.println("Нет текстовых файлов в каталоге " + FILE_DIRECTORY + ".");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            displayTextFiles(textFiles);

            System.out.print("Введите номер файла, с которым хотите работать (или 0 для выхода): ");
            int fileIndex = scanner.nextInt() - 1;
            scanner.nextLine();

            if (fileIndex == -1) {
                break;
            }

            if (fileIndex < 0 || fileIndex >= textFiles.size()) {
                System.out.println("Неверный номер файла, попробуйте снова.");
                continue;
            }

            String selectedFile = FILE_DIRECTORY + textFiles.get(fileIndex);
            System.out.println("Содержимое файла '" + textFiles.get(fileIndex) + "':");
            readFile(selectedFile);
            writeToFile(selectedFile);
        }
        System.out.println("Выход из программы.");
    }

    private static void displayTextFiles(List<String> textFiles) {
        System.out.println("Список текстовых файлов в каталоге " + FILE_DIRECTORY + ":");
        for (int i = 0; i < textFiles.size(); i++) {
            System.out.println((i + 1) + ". " + textFiles.get(i));
        }
    }

    private static List<String> listTextFiles() {
        List<String> files = new ArrayList<>();
        File dir = new File(FILE_DIRECTORY);
        File[] fileList = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (fileList != null) {
            for (File file : fileList) {
                files.add(file.getName());
            }
        }
        return files;
    }

    private static void readFile(String fileName) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName));
             InputStreamReader isr = new InputStreamReader(bis)) {
            int charRead = isr.read();
            while (charRead != -1) {
                System.out.print((char) charRead);
                charRead = isr.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeToFile(String fileName) {
        System.out.print("Введите строку для записи в файл: ");
        Scanner scanner = new Scanner(System.in);
        String text = scanner.nextLine();
        try (BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(fileName, true))) {
            byte[] buffer = text.getBytes(StandardCharsets.UTF_8);
            bw.write(buffer);
            bw.write(System.lineSeparator().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Строка успешно записана в файл.");
    }
}