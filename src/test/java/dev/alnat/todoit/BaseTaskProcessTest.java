package dev.alnat.todoit;

import dev.alnat.todoit.configuration.PostgreSQLTestContainerConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Набор тест-кейсов для имитации определенного бизнес-процесса
 * Вариант процесса -- пользователь начинает использовать приложение
 */
@SpringBootTest
@ContextConfiguration(classes = PostgreSQLTestContainerConfiguration.class) // Поднимаем БД в контейнере
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Тесты должны идти в точно порядке
@AutoConfigureMockMvc
@DirtiesContext // Будем независимы от других тестов -- запустим в новом контексте
class BaseTaskProcessTest extends BaseTest {

    /**
     * Шаг 1. Получения списка задач
     *
     * Выполняется поиск списка задач без фильтров
     * Ожидается успешное выполнение задачи и пустой список в ответе
     */
    @Test
    @Order(1)
    @DisplayName("Шаг 1. Получение пустого списка задач")
    void testGetTaskListAtFirstTime() {

    }

    // TODO Шаг 2 Создание задачи
    // TODO Шаг 3 Получение задачи
    // TODO Шаг 4 Редактирование задачи
    // TODO Шаг 5 Получение задачи
    // TODO Шаг 6 Смена статуса у задачи
    // TODO Шаг 7 Получение списка задач
    // TODO Шаг 8 Получение списка без этого статуса
    // TODO Шаг 9 Создание новой задачи
    // TODO Шаг 10 Получение списка задач
    // TODO Шаг 11 Удаление новой задачи
    // TODO Шаг 12 Получение списка задач

}
