package ru.otus.java.basic.homeworks.hw24;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application {

    // Домашнее задание:
    // 1. Добавить логирование вместо sout
    //+ 2. Доделать DELETE для продуктов. DELETE без ид удаляет все продукты, DELETE c ид удаляет конкретный продукт
    // 3. * Доделать PUT для продуктов. По id из тела запроса находим соответствующий продукт и обновляем его поля

    private static final Logger LOGGER = LogManager.getLogger(Application.class);
    public static void main(String[] args) {
        LOGGER.debug("Запускаем сервер...");
        new HttpServer(8189).start();
    }
}
