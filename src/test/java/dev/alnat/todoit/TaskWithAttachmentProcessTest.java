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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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

    private final byte[] fileData = "Hello world".getBytes(StandardCharsets.UTF_8);
    private final String attachmentName = "test_file.txt";

    /**
     * Шаг 1
     * Получения списка задач
     */
    @Test
    @Order(1)
    @DisplayName("Шаг 1. Получение пустого списка задач")
    void testGetTaskListAtFirstTime() {
        var request = TaskUtils.generateTaskSearch();
        var response = searchTaskByParams(request);

        Assertions.assertTrue(response.getData().isEmpty(),
                "Список задач не пуст, проверьте очередность запуска тестов и окружение");
    }

    @Test
    @Order(2)
    @DisplayName("Шаг 2. Создание задачи")
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
    @DisplayName("Шаг 3. Сохранение вложения к задаче")
    void uploadFirstFile() {

        var attachment = uploadAttachment(attachmentName, fileData, 1L);

        Assertions.assertEquals(attachmentName, attachment.getName(),
                "Получение не то вложение! Проверьте корректность логики или окружение");
        Assertions.assertNotNull(attachment.getId(),
                "У задачи не заполнено авто-генерируемое поле, проверьте Entity!");
        Assertions.assertNotNull(attachment.getCreated(),
                "У задачи не заполнено авто-генерируемое поле, проверьте Entity!");
    }

    @Test
    @Order(4)
    @DisplayName("Шаг 4. Получение списка вложений к задаче")
    void gatTaskAttachment() {
        var attachmentList = getAttachmentsByTask(1L);

        Assertions.assertFalse(attachmentList.isEmpty(),
                "Список вложений пуст! Проверьте корректность логики или окружение");
        Assertions.assertEquals(1, attachmentList.size(),
                "Список вложений не корректного размера! Проверьте корректность логики или окружение");
        Assertions.assertEquals(attachmentName, attachmentList.get(0).getName(),
                "Получено не ожидаемое вложение! Проверьте корректность логики или окружение");
    }

    @Test
    @Order(5)
    @DisplayName("Шаг 5. Получение вложения к задаче")
    void getAttachment() {
        var attachment = getAttachment(1L);

        assertThat(attachment, equalTo(fileData));
    }

    @Test
    @Order(6)
    @DisplayName("Шаг 6. Удаление вложения к задаче")
    void deleteAttachment() {
        deleteAttachment(1L);
    }

    @Test
    @Order(7)
    @DisplayName("Шаг 7. Получение списка вложений к задаче после удаления")
    void gatTaskAttachmentAfterDelete() {
        var attachmentList = getAttachmentsByTask(1L);

        Assertions.assertTrue(attachmentList.isEmpty(),
                "Список вложений не пуст! Проверьте корректность логики или окружение");
    }

}
