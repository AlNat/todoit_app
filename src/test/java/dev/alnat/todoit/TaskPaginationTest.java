package dev.alnat.todoit;

import dev.alnat.todoit.configuration.PostgreSQLTestContainerConfiguration;
import dev.alnat.todoit.enums.TaskStatus;
import dev.alnat.todoit.model.Task;
import dev.alnat.todoit.tools.TaskUtils;
import dev.alnat.todoit.types.TaskDTO;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Набор тестов по постраничному получению задач
 *
 * Created by @author AlNat on 06.02.2022.
 * Licensed by Apache License, Version 2.0
 */
@SpringBootTest
@ContextConfiguration(classes = PostgreSQLTestContainerConfiguration.class) // Поднимаем БД в контейнере
@AutoConfigureMockMvc
@DirtiesContext // Будем независимы от других тестов -- запустим в новом контексте
class TaskPaginationTest extends BaseTest {

    private static final String[] TASK_NAME = {"Task1", "Task2", "Task3", "Task4", "Task5"};
    private static final List<String> TASK_NAME_LIST = Arrays.asList(TASK_NAME);

    @BeforeEach
    void init() {
        clearDB();
        
        // Создаем задачи для последующих тестов
        for (int i = 0; i < TASK_NAME.length; i++) {
            Task task = new Task();
            task.setId((long) (i + 1));
            task.setTitle(TASK_NAME[i]);
            task.setStatus(TaskStatus.PLANNED);
            task.setPlanned(LocalDateTime.of(2100, 1, 1, 0, 0, i)); // Секунда соответствует номеру по порядку

            taskRepository.saveAndFlush(task);
        }
    }


    /**
     * В базе 5 задач
     * Ищем без фильтрации с limit = 5 и offset = 0
     * Ожидается что в ответе 5 задач и нет hasMore
     */
    @Test
    @DisplayName("Проверка получения всех задач на одной странице")
    void testGetSinglePageOnTaskSearch() {
        var request = TaskUtils.generateTaskSearch(100, 0);
        var response = searchTaskByParams(request);

        Assertions.assertFalse(response.getData().isEmpty(),
                "Список задач пуст, проверьте инициализацию данных для тестов");
        Assertions.assertEquals(5, response.getData().size(),
                "Список задач не равен ожидаемому, проверьте инициализацию данных для тестов");
        Assertions.assertFalse(response.getHasMore(),
                "Присутствует флаг о следующей странице, проверьте логику работы флага следующей страницы или данные для тестов");

        var foundTaskName = response.getData().stream().map(TaskDTO::getTitle).toArray();
        assertThat(TASK_NAME_LIST, Matchers.containsInAnyOrder(foundTaskName));
    }

    /**
     * В базе 5 задач
     * Ищем без фильтрации с limit = 3 и offset = 0
     * Ожидается что в ответе 3 задачи и есть hasMore
     */
    @Test
    @DisplayName("Проверка получения начислений на первой странице")
    void testGetFirstPageOnTaskSearch() {
        var request = TaskUtils.generateTaskSearch(3, 0);
        var response = searchTaskByParams(request);

        Assertions.assertFalse(response.getData().isEmpty(),
                "Список задач пуст, проверьте инициализацию данных для тестов");
        Assertions.assertEquals(3, response.getData().size(),
                "Список задач не равен ожидаемому, проверьте инициализацию данных для тестов");
        Assertions.assertTrue(response.getHasMore(),
                "Отсутствует флаг о следующей странице, проверьте логику работы флага следующей страницы или данные для тестов");

        var foundTaskName = response.getData().stream().map(TaskDTO::getTitle).toArray();
        assertThat(Arrays.asList(TASK_NAME[0], TASK_NAME[1], TASK_NAME[2]), Matchers.containsInAnyOrder(foundTaskName));
    }

    /**
     * В базе 5 задач
     * Ищем без фильтрации с limit = 3 и offset = 3
     * Ожидается что в ответе 2 задачи и нет hasMore
     */
    @Test
    @DisplayName("Проверка получения начислений на второй странице")
    void testGetLastPageOnTaskSearch() {
        var request = TaskUtils.generateTaskSearch(3, 3);
        var response = searchTaskByParams(request);

        Assertions.assertFalse(response.getData().isEmpty(),
                "Список задач пуст, проверьте инициализацию данных для тестов");
        Assertions.assertEquals(2, response.getData().size(),
                "Список задач не равен ожидаемому, проверьте инициализацию данных для тестов");
        Assertions.assertFalse(response.getHasMore(),
                "Присутствует флаг о следующей странице, проверьте логику работы флага следующей страницы или данные для тестов");

        var foundTaskName = response.getData().stream().map(TaskDTO::getTitle).toArray();
        assertThat(Arrays.asList(TASK_NAME[3], TASK_NAME[4]), Matchers.containsInAnyOrder(foundTaskName));
    }

    /**
     * В БД 5 задач
     * Ищем без фильтрации с limit = 3 и offset = 0
     * А затем без фильтрации с limit = 3 и offset = 3
     * Ожидается что все задачи будут отданы
     */
    @Test
    @DisplayName("Проверка последовательного получения всех задачи на 2 страницах")
    void testGetPageOnTaskSearch() {
        // Создаем запрос и получаем ответ
        var firstRequest = TaskUtils.generateTaskSearch(3, 0);
        var firstResponse = searchTaskByParams(firstRequest);

        Assertions.assertFalse(firstResponse.getData().isEmpty(),
                "Список задач пуст, проверьте инициализацию данных для тестов");
        Assertions.assertEquals(3, firstResponse.getData().size(),
                "Список задач не равен ожидаемому, проверьте инициализацию данных для тестов");
        Assertions.assertTrue(firstResponse.getHasMore(),
                "Отсутствует флаг о следующей странице, проверьте логику работы флага следующей страницы или данные для тестов");


        // Создаем второй запрос и получаем ответ
        var secondRequest = TaskUtils.generateTaskSearch(3, 3);
        var secondResponse = searchTaskByParams(secondRequest);

        Assertions.assertFalse(secondResponse.getData().isEmpty(),
                "Список задач пуст, проверьте инициализацию данных для тестов");
        Assertions.assertEquals(2, secondResponse.getData().size(),
                "Список задач не равен ожидаемому, проверьте инициализацию данных для тестов");
        Assertions.assertFalse(secondResponse.getHasMore(),
                "Присутствует флаг о следующей странице, проверьте логику работы флага следующей страницы или данные для тестов");


        // Проверяем что получили все задачи
        var resultList = new LinkedList<String>();
        resultList.addAll(firstResponse.getData().stream().map(TaskDTO::getTitle).toList());
        resultList.addAll(secondResponse.getData().stream().map(TaskDTO::getTitle).toList());
        
        assertThat(TASK_NAME_LIST, Matchers.containsInAnyOrder(resultList.toArray()));
    }

    
    /**
     * В БД 5 задач
     * Ищем без фильтрации с limit = 3 и offset = 0;
     * А затем без фильтрации с limit = 3 и offset = 4
     * Ожидается что всего в ответах будет 4 задачи из-за смещения offset-а
     */
    @Test
    @DisplayName("Проверка получения задач при смещенном offset")
    void testMovePageOffsetOnTaskSearch() {
        // Создаем запрос и получаем ответ
        var firstRequest = TaskUtils.generateTaskSearch(3, 0);
        var firstResponse = searchTaskByParams(firstRequest);

        Assertions.assertFalse(firstResponse.getData().isEmpty(),
                "Список задач пуст, проверьте инициализацию данных для тестов");
        Assertions.assertEquals(3, firstResponse.getData().size(),
                "Список задач не равен ожидаемому, проверьте инициализацию данных для тестов");
        Assertions.assertTrue(firstResponse.getHasMore(),
                "Отсутствует флаг о следующей странице, проверьте логику работы флага следующей страницы или данные для тестов");
        

        // Создаем второй запрос и получаем ответ
        var secondRequest = TaskUtils.generateTaskSearch(3, 4);
        var secondResponse = searchTaskByParams(secondRequest);

        Assertions.assertFalse(secondResponse.getData().isEmpty(),
                "Список задач пуст, проверьте инициализацию данных для тестов");
        Assertions.assertEquals(1, secondResponse.getData().size(),
                "Список задач не равен ожидаемому, проверьте инициализацию данных для тестов");
        Assertions.assertFalse(secondResponse.getHasMore(),
                "Присутствует флаг о следующей странице, проверьте логику работы флага следующей страницы или данные для тестов");
    }

}
