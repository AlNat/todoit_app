package dev.alnat.todoit;

import dev.alnat.todoit.configuration.PostgreSQLTestContainerConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Набор тест-кейсов по проверке ошибок при обращении к API Задач
 */
@SpringBootTest
@ContextConfiguration(classes = PostgreSQLTestContainerConfiguration.class) // Поднимаем БД в контейнере
@AutoConfigureMockMvc
@DirtiesContext // Будем независимы от других тестов -- запустим в новом контексте
class TaskAPIFailuresTest extends BaseTest {

    @BeforeEach
    void clear() {
        clearDB();
    }


    /**
     * Кейс поиска по id задачи которой нет в БД
     *
     * Должен отправить синхронный запрос на поиск
     * В ответе должен быть 404 код
     */
    @Test
    @DisplayName("Проверка получения задачи по несуществующему id")
    void testGetByIdNotExistsTask() {
        String responseBody = null;
        try {
            responseBody = mvc.perform(
                            get("/api/v1/task/123").accept(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse().getContentAsString();
        } catch (Exception e) {
            Assertions.fail(e);
        }

        Assertions.assertNotNull(responseBody, "Текст ошибки не передан!");
    }

    // TODO Обновление несуществующей задачи
    // TODO Удаление несуществующей задачи
    // TODO Некорректно составленные DTO на создание задачи

}
