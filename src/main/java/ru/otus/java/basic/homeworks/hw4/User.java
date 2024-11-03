package ru.otus.java.basic.homeworks.hw4;

public class User {
    private String firstname, lastname, patronymic, email;
    private int yearofbirth;

    //Getters and setters
    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getYearofbirth() {
        return yearofbirth;
    }

    public void setYearofbirth(int yearofbirth) {
        this.yearofbirth = yearofbirth;
    }

    //Constructor
    public User(String lastname, String firstname, String patronymic, String email, int yearofbirth) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.patronymic = patronymic;
        this.email = email;
        this.yearofbirth = yearofbirth;
    }

    //Методы
    public void info() {
        System.out.println("ФИО: " + lastname + " " + firstname + " " + patronymic);
        System.out.println("Год рождения: " + yearofbirth);
        System.out.println("e-mail: " + email);
        System.out.println();
    }
}
