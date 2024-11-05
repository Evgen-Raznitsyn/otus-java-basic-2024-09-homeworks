package ru.otus.java.basic.homeworks.hw4;

public class MainApp {
    public static void main(String[] args) {
        int currentYear = 2024;
        User[] users = {
                new User("Иванов", "Николай", "Александрович", "ivanov@mail.ru", 1980),
                new User("Петров", "Василий", "Константинович", "petrov@gmail.com", 1981),
                new User("Смирнов", "Михаил", "Петрович", "smirnov@gmail.com", 1997),
                new User("Сидоров", "Артём", "Дмитриевич", "sidorov@mail.ru", 1989),
                new User("Печкин", "Роман", "Николаевич", "pechkin@gmail.com", 1991),
                new User("Кузнецова", "Ольга", "Владимировна", "kuznetsova@mail.ru", 1990),
                new User("Васильева", "Виктория", "Юрьевна", "vasileva@gmail.com", 1999),
                new User("Николаев", "Сергей", "Павлович", "nikolaev@mail.ru", 1998),
                new User("Морозов", "Николай", "Александрович", "morozov@gmail.com", 1993),
                new User("Фролов", "Алексей", "Викторович", "frolov@gmail.com", 1994)
        };

        //Вывести информацию только о пользователях старше 40 лет
        for (User user : users) {
            int ageUsers = currentYear - user.getYearofbirth();
            if (ageUsers > 40) {
                user.info();
            }
        }

        //Коробка
        Box myBox = new Box(10, 10, 10, "зеленый");
        myBox.printInfo();  // Печать информации о коробке
        myBox.open();       // Открыть коробку
        myBox.putItem("игрушка");  // Положить предмет
        myBox.putItem("мяч");  // Попытка положить еще предмет
        myBox.close();      // Закрыть коробку
        myBox.repaint("красный"); //Перекрасить коробку
        myBox.printInfo();  // Печать информации с предметом
        myBox.takeOutItem(); // Попытка вытянуть предмет из закрытой коробки
        myBox.open();       // Открыть коробку
        myBox.takeOutItem(); // Вытянуть предмет
        myBox.close();      // Закрыть коробку
    }
}
