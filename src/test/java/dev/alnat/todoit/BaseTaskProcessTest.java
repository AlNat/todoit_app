package dev.alnat.todoit;

import dev.alnat.todoit.configuration.PostgreSQLTestContainerConfiguration;
import dev.alnat.todoit.enums.TaskStatus;
import dev.alnat.todoit.tools.TaskUtils;
import dev.alnat.todoit.types.TaskDTO;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;

/**
 * Набор тест-кейсов для имитации определенного бизнес-процесса
 * Процесс -- пользователь начинает использовать приложение и создает задачу, изменяет ее,
 *  затем создает и удаляет другую задачу
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
    void getTaskListAtFirstTime() {
        var request = TaskUtils.generateTaskSearch();
        var response = searchTaskByParams(request);

        Assertions.assertTrue(response.getData().isEmpty(),
                "Список задач не пуст, проверьте очередность запуска тестов и окружение");
    }

    @Test
    @Order(2)
    @DisplayName("Шаг 2. Создание первой задачи")
    void saveFirstTask() {
        var newTask = TaskDTO.builder()
                .title("Тестовая задача")
                .description("Описание тестовой задачи")
                .status(TaskStatus.PLANNED)
                .planned(LocalDateTime.now().plusDays(7L))
                .build();

        var taskId = saveNewTask(newTask);
        Assertions.assertNotNull(taskId,
                "Новая задача не получили идентификатор! Проверьте корректность API");
    }

    @Test
    @Order(3)
    @DisplayName("Шаг 3. Получение первой задачи по id")
    void getFirstTaskById() {
        var task = getTaskById(1L);

        Assertions.assertEquals("Тестовая задача", task.getTitle(),
                "Получение не та задача! Проверьте корректность логики или окружение");
        Assertions.assertNotNull(task.getCreated(),
                "У задачи не заполнено авто-генерируемое поле, проверьте Entity!");
        Assertions.assertNotNull(task.getUpdated(),
                "У задачи не заполнено авто-генерируемое поле, проверьте Entity!");
    }

    @Test
    @Order(4)
    @DisplayName("Шаг 4. Изменение первой задачи")
    void changeFirstTask() {
        var task = getTaskById(1L);
        task.setDescription("Измененное описание задачи");

        updateTask(task);
    }

    @Test
    @Order(5)
    @DisplayName("Шаг 5. Получение первой задачи после изменения")
    void getFirstTaskByIdAfterChange() {
        var task = getTaskById(1L);

        Assertions.assertEquals("Измененное описание задачи", task.getDescription(),
                "Изменение задачи на предыдущем шаге не применилось! Проверьте корректность логики или окружение");
    }

    @Test
    @Order(6)
    @DisplayName("Шаг 6. Изменение статуса первой задачи")
    void changeFirstTaskStatus() {
        changeTaskStatus(1L, TaskStatus.ARCHIVED);

        var task = getTaskById(1L);
        Assertions.assertEquals(TaskStatus.ARCHIVED, task.getStatus(),
                "Изменение задачи не применилось! Проверьте корректность логики или окружение");
    }

    @Test
    @Order(7)
    @DisplayName("Шаг 7. Получение списка задач с первой задачей")
    void getTaskListAtSecondTime() {
        var request = TaskUtils.generateTaskSearch();
        var response = searchTaskByParams(request);

        Assertions.assertFalse(response.getData().isEmpty(),
                "Список задач пуст, проверьте очередность запуска тестов и окружение");
        Assertions.assertEquals(1, response.getData().size(),
                "Размер списка задач не корректен, проверьте окружение запуска тестов!");

        var task = response.getData().get(0);
        Assertions.assertEquals("Тестовая задача", task.getTitle(),
                "Задача не соответствует ожидаемой, проверьте окружение запуска тестов!");
        Assertions.assertEquals("Измененное описание задачи", task.getDescription(),
                "Задача не соответствует ожидаемой, проверьте окружение запуска тестов!");
        Assertions.assertEquals(TaskStatus.ARCHIVED, task.getStatus(),
                "Задача не соответствует ожидаемой, проверьте окружение запуска тестов!");
    }

    @Test
    @Order(8)
    @DisplayName("Шаг 8. Получение списка задач с фильтрацией по статусам")
    void getTaskListAtThirdTime() {
        var request = TaskUtils.generateTaskSearch(TaskStatus.PLANNED, TaskStatus.DONE);
        var response = searchTaskByParams(request);

        Assertions.assertTrue(response.getData().isEmpty(),
                "Список задач не пуст, проверьте очередность запуска тестов и окружение");
    }

    @Test
    @Order(9)
    @DisplayName("Шаг 9. Создание второй задачи")
    void saveSecondTask() {
        var newTask = TaskDTO.builder()
                .title("Тестовая задача 2")
                .description("Описание тестовой задачи 2")
                .status(TaskStatus.PLANNED)
                .planned(LocalDateTime.now().plusDays(14L))
                .build();

        var taskId = saveNewTask(newTask);
        Assertions.assertNotNull(taskId,
                "Новая задача не получили идентификатор! Проверьте корректность API");
    }

    @Test
    @Order(10)
    @DisplayName("Шаг 10. Получение списка задач c двумя задачами")
    void getTaskListAtFourthTime() {
        var request = TaskUtils.generateTaskSearch();
        var response = searchTaskByParams(request);

        Assertions.assertFalse(response.getData().isEmpty(),
                "Список задач пуст, проверьте очередность запуска тестов и окружение");
        Assertions.assertEquals(2, response.getData().size(),
                "Размер списка задач не корректен, проверьте окружение запуска тестов!");
    }

    @Test
    @Order(11)
    @DisplayName("Шаг 11. Удаление второй задачи")
    void deleteSecondTask() {
        deleteTask(2L);
    }

    @Test
    @Order(12)
    @DisplayName("Шаг 12. Получение списка задач с одной задачей, которая осталась")
    void getTaskListAtFifthTime() {
        var request = TaskUtils.generateTaskSearch();
        var response = searchTaskByParams(request);

        Assertions.assertFalse(response.getData().isEmpty(),
                "Список задач пуст, проверьте очередность запуска тестов и окружение");
        Assertions.assertEquals(1, response.getData().size(),
                "Размер списка задач не корректен, проверьте окружение запуска тестов!");
    }

}
