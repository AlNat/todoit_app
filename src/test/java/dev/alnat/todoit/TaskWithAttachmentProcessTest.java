package dev.alnat.todoit;

import dev.alnat.todoit.configuration.PostgreSQLTestContainerConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Набор тест-кейсов для имитации определенного бизнес-процесса
 * Вариант процесса -- пользователь работает с задачей с вложением
 */
@SpringBootTest
@ContextConfiguration(classes = PostgreSQLTestContainerConfiguration.class) // Поднимаем БД в контейнере
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Тесты должны идти в точном порядке
@AutoConfigureMockMvc
@DirtiesContext // Будем независимы от других тестов -- запустим в новом контексте
class TaskWithAttachmentProcessTest extends BaseTest {

    /**
     * Шаг 1
     * Получения списка задач
     */
    @Test
    @Order(1)
    @DisplayName("Шаг 1. Получение пустого списка задач")
    void testGetTaskListAtFirstTime() {

    }

    // TODO Шаг 2 Создание задачи с цветом
    // TODO Шаг 3 Добавления аттача к задаче
    // TODO Шаг 4 получение списка задач
    // TODO Шаг 5 получение списка вложений задачи
    // TODO Шаг 6 скачивание вложения по второй задачи
    // TODO Шаг 7 удаление вложения ко второй задаче
    // TODO Шаг 8 перевод второй задачи в выполненные
    // TODO Шаг 9 Получение списка активных задач

}
