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
            System.out.println("Список текстовых файлов в каталоге " + FILE_DIRECTORY + ":");
            for (int i = 0; i < textFiles.size(); i++) {
                System.out.println((i + 1) + ". " + textFiles.get(i));
            }

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
            System.out.println(readFile(selectedFile));

            System.out.print("Введите строку для записи в файл: ");
            String userInput = scanner.nextLine();
            writeToFile(selectedFile, userInput);
            System.out.println("Строка успешно записана в файл.");
        }

        System.out.println("Выход из программы.");
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

    private static String readFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName));
             InputStreamReader isr = new InputStreamReader(bis)) {
            int charRead;
            while ((charRead = isr.read()) != -1) {
                content.append((char) charRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private static void writeToFile(String fileName, String text) {
        try (BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(fileName, true))) {
            byte [] buffer = text.getBytes(StandardCharsets.UTF_8);
            bw.write(buffer);
            bw.write(System.lineSeparator().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}