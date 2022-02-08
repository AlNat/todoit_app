package dev.alnat.todoit;

import dev.alnat.todoit.common.Sorting;
import dev.alnat.todoit.configuration.PostgreSQLTestContainerConfiguration;
import dev.alnat.todoit.tools.TaskUtils;
import dev.alnat.todoit.tools.TestUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    /**
     * Кейс удаление задачи по id задачи которой нет в БД
     *
     * Должен отправить синхронный запрос на удаление
     * В ответе должен быть 404 код
     */
    @Test
    @DisplayName("Проверка удаления задачи по несуществующему id")
    void testDeleteByIdNotExistsTask() {
        String responseBody = null;
        try {
            responseBody = mvc.perform(delete("/api/v1/task/123"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn()
                    .getResponse().getContentAsString();
        } catch (Exception e) {
            Assertions.fail(e);
        }

        Assertions.assertNotNull(responseBody, "Текст ошибки не передан!");
    }

    /**
     * Кейс сохранения задачи с некорректным JSON
     *
     * Должен отправить синхронный запрос на сохранение
     * В ответе должен быть 400 код
     */
    @Test
    @DisplayName("Проверка ошибки сохранения задачи по невалидному JSON")
    void testMalformedTaskDTO() {
        String responseBody = null;
        try {
            responseBody = mvc.perform(
                        post("/api/v1/task/")
                            .content("{")
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse().getContentAsString();
        } catch (Exception e) {
            Assertions.fail(e);
        }

        Assertions.assertNotNull(responseBody, "Текст ошибки не передан!");
    }

    /**
     * Кейс обновления задачи по id задачи которой нет в БД
     *
     * Должен отправить синхронный запрос на сохранение
     * В ответе должен быть 400 код
     */
    @Test
    @DisplayName("Проверка ошибки обновления несуществующей задачи")
    void testUpdateNotExistsTask() {
        String responseBody = null;
        try {
            responseBody = mvc.perform(
                            put("/api/v1/task/123")
                                    .content(mapper.writeValueAsString(TaskUtils.generateTask(123L)))
                                    .contentType(MediaType.APPLICATION_JSON)
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

    // Некорректные параметры лимита и offset-а
    private static Stream<Arguments> dataIncorrectLimitAndOffset() {
        return Stream.of(
                Arguments.of(-1, -1),
                Arguments.of(-1, 0),
                Arguments.of(0, -1),
                Arguments.of(0, 0),
                Arguments.of(101, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("dataIncorrectLimitAndOffset")
    @DisplayName("Проверка некорректных limit и offset")
    void testIncorrectLimitAndOffset(Integer limit, Integer offset) throws Exception {
        var params = new LinkedMultiValueMap<String, String>();
        params.add("limit", limit.toString());
        params.add("offset", offset.toString());

        this.mvc.perform(get("/api/v1/task/").params(params))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }


    // Некорректные параметры сортировки
    private static Stream<Arguments> dataIncorrectSorting() {
        return Stream.of(
                Arguments.of(100, 0, new Sorting[]{Sorting.of("created"), Sorting.of("created"), Sorting.of("created"), Sorting.of("created"), Sorting.of("created"), Sorting.of("created")}),
                Arguments.of(100, 0, new Sorting[]{Sorting.builder().sortOrder(Sorting.SortOrder.DESC).build()})
        );
    }

    @ParameterizedTest
    @MethodSource("dataIncorrectSorting")
    @DisplayName("Проверка некорректной сортировки")
    void testIncorrectSorting(int limit, int offset, Sorting... sorting) throws Exception {
        var req = TaskUtils.generateTaskSearch(limit, offset, sorting);
        var params = TestUtils.objectToMap(req);

        this.mvc.perform(get("/api/v1/task/").params(params))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

}
