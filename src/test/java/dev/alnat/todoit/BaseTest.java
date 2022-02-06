package dev.alnat.todoit;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.alnat.todoit.enums.TaskStatus;
import dev.alnat.todoit.repository.AttachmentRepository;
import dev.alnat.todoit.repository.TaskRepository;
import dev.alnat.todoit.search.TaskSearchRequest;
import dev.alnat.todoit.search.TaskSearchResponse;
import dev.alnat.todoit.tools.TestUtils;
import dev.alnat.todoit.types.TaskDTO;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Набор общих методов
 * С ооочень большой натяжкой можно назвать тестовым фреймворком
 *
 * Created by @author AlNat on 16.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@SuppressWarnings({"SameParameterValue", "unused"})
public abstract class BaseTest {

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("isLoggingToConsoleEnabled", "true");
    }

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected AttachmentRepository attachmentRepository;

    @Autowired
    protected TaskRepository taskRepository;

    protected final ObjectMapper mapper = new ObjectMapper();


    ///////////////////
    // API для задач //
    ///////////////////

    @SneakyThrows
    protected Long saveNewTask(TaskDTO dto) {
        String request;
        try {
            request = mapper.writeValueAsString(dto);
        } catch (RuntimeException e) {
            Assertions.fail(e);
            return null;
        }

        String response = getSyncResponseToPOST("/api/v1/task/", request);

        TaskDTO savedDTO;
        try {
            savedDTO = mapper.readValue(response, TaskDTO.class);
        } catch (RuntimeException e) {
            Assertions.fail(e);
            return null;
        }

        return savedDTO.getId();
    }

    @SneakyThrows
    protected void updateTask(TaskDTO dto) {
        String request;
        try {
            request = mapper.writeValueAsString(dto);
        } catch (Exception e) {
            Assertions.fail(e);
            return;
        }

        getSyncResponseToPUT("/api/v1/task/", request);
    }

    protected void changeTaskStatus(Long id, TaskStatus status) {
        var map = new LinkedMultiValueMap<String, String>();
        map.add("status", status.name());

        getSyncResponseToPATCH("/api/v1/task/" + id, map);
    }

    protected void deleteTask(Long id) {
        getSyncResponseToDELETE("/api/v1/task/" + id);
    }

    protected TaskDTO getTaskById(Long id) {
        String response = getSyncResponseToGET("/api/v1/task/" + id);

        try {
            return mapper.readValue(response, TaskDTO.class);
        } catch (Exception e) {
            Assertions.fail(e);
            return null;
        }
    }

    @SneakyThrows
    protected TaskSearchResponse searchTaskByParams(TaskSearchRequest request) {
        var requestParams = TestUtils.objectToMap(request);

        String response = startGETRequestAndWaitResponse(requestParams, "/api/v1/task/");

        try {
            return mapper.readValue(response, TaskSearchResponse.class);
        } catch (Exception e) {
            Assertions.fail(e);
            return null;
        }
    }

    //////////////////////
    // API для вложений //
    //////////////////////

    // TODO Получить список вложений
    // TODO Сохранить вложение
    // TODO Скачать вложение

    /////////////////////////
    // Набор общих методов //
    /////////////////////////

    @Transactional
    protected void clearDB() {
        attachmentRepository.deleteAll();;
        taskRepository.deleteAll();
    }


    // Синхронный вызов

    @SneakyThrows
    protected String getSyncResponseToGET(String url) {
        return this.mvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @SneakyThrows
    protected String getSyncResponseToPOST(String url, String request) {
        return this.mvc.perform(
                        post(url)
                                .content(request)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @SneakyThrows
    protected String getSyncResponseToPUT(String url, String request) {
        return this.mvc.perform(
                        post(url)
                            .content(request)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @SneakyThrows
    protected String getSyncResponseToPATCH(String url, MultiValueMap<String, String> request) {
        return this.mvc.perform(
                        patch(url)
                            .params(request)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @SneakyThrows
    protected String getSyncResponseToDELETE(String url) {
        return this.mvc.perform(delete(url))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse().getContentAsString(StandardCharsets.UTF_8);
    }


    // Вызов с запуском DeferredResult

    @SneakyThrows
    protected String startGETRequestAndWaitResponse(MultiValueMap<String, String> request, String endpoint) {
        MvcResult result = this.mvc.perform(
                get(endpoint)
                    .params(request)
                    .accept(MediaType.APPLICATION_JSON)
        ).andExpect(request().asyncStarted()).andReturn();

        // Дожидаемся пока DeferredResult начет обрабатываться
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assertions.fail(e);
        }

        // И только после этого дожидаемся исполнения DeferredResult
        result = this.mvc
                .perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        return result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    @SneakyThrows
    protected String startPOSTRequestAndWaitResponse(String request, String endpoint) {
        MvcResult result = this.mvc.perform(
                post(endpoint)
                    .content(request)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(request().asyncStarted()).andReturn();

        // Дожидаемся пока DeferredResult начет обрабатываться
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Assertions.fail(e);
        }

        // И только после этого дожидаемся исполнения DeferredResult
        result = this.mvc
                .perform(asyncDispatch(result))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        return result.getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

}
