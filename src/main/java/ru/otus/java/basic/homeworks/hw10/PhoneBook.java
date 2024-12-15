package ru.otus.java.basic.homeworks.hw10;

import java.util.*;

public class PhoneBook {
    private final Map<String, Set<String>> book = new HashMap<>();

    public void add(String name, String phoneNumber) {
        Set<String> phoneNumbers = book.get(name);
        if (phoneNumbers == null) {
            phoneNumbers = new HashSet<>();
            book.put(name, phoneNumbers);
        }
        phoneNumbers.add(phoneNumber);
    }

    public String findName(String name) {
        Map<String, Set<String>> result = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : book.entrySet()) {
            if (entry.getKey().contains(name)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        if (!result.isEmpty()) {
            List<String> results = new ArrayList<>();
            results.add(String.format("\nТелефоны: '%s', а именно:", name));
            for (Map.Entry<String, Set<String>> entry : result.entrySet()) {
                results.add(String.format("%s - %s", entry.getKey(), entry.getValue()));
            }
            return String.join("\n", results);
        }
        return String.format("\nТелефонов с '%s' - нет в справочнике!", name);
    }

    public String containsPhoneNumber(String phoneNumber) {
        for (Map.Entry<String, Set<String>> entry : book.entrySet()) {
            Set<String> numbers = entry.getValue();
            if (numbers.contains(phoneNumber)) {
                return String.format("\nНомер: %s - принадлежит: %s", phoneNumber, entry.getKey());
            }
        }
        return String.format("\nНомера: %s - нет в справочнике!", phoneNumber);
    }

    public static void main(String[] args) {
        PhoneBook phonebook = new PhoneBook();
        phonebook.add("Романов Иван", "10000000000");
        phonebook.add("Романов Иван", "20000000000");
        phonebook.add("Иванов Николай", "30000000000");
        phonebook.add("Иванов Пётр", "40000000000");
        phonebook.add("Иванов Александр", "50000000000");
        phonebook.add("Петров Николай", "60000000000");
        phonebook.add("Петров Роман", "70000000000");

        System.out.println(phonebook.findName("Иванов"));
        System.out.println(phonebook.findName("Пётр"));
        System.out.println(phonebook.findName("Петров Иван"));
        System.out.println(phonebook.findName("Иван"));
        System.out.println(phonebook.findName("Рома"));
        System.out.println(phonebook.containsPhoneNumber("20000000000"));
        System.out.println(phonebook.containsPhoneNumber("60000000000"));
        System.out.println(phonebook.containsPhoneNumber("00000000000"));
    }
}
