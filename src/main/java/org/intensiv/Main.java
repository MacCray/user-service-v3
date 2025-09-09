package org.intensiv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Main {
    public static void main(String[] args) {

        try {
            log.info("Запуск user-service");
            SpringApplication.run(Main.class, args);
        } catch (Exception e) {
            log.error("Критическая ошибка при запуске приложения", e);
        } finally {
            log.info("Завершение работы user-service");
        }
    }
}
