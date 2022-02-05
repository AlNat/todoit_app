package dev.alnat.todoit;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.alnat.todoit.repository.AttachmentRepository;
import dev.alnat.todoit.repository.TaskRepository;
import dev.alnat.todoit.search.TaskSearchRequest;
import dev.alnat.todoit.search.TaskSearchResponse;
import dev.alnat.todoit.tools.TaskUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Набор общих методов
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

    // TODO Сохранение новой задачи
    // TODO Получение по ID
    // TODO Обновление задачи
    // TODO Смена статуса у задачи
    // TODO Удаление задачи

    @SneakyThrows
    protected TaskSearchResponse startTaskSearchAndWaitResponse(TaskSearchRequest request) {
        var requestParams = TaskUtils.toMap(request);

        String response = startGETRequestAndWaitResponse(requestParams, "/api/v1/task");

        try {
            return mapper.readValue(response, TaskSearchResponse.class);
        } catch (RuntimeException e) {
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

    protected void clearDB() {
        attachmentRepository.deleteAll();;
        taskRepository.deleteAll();
    }


    @SneakyThrows
    protected String getSyncResponseToGET(String request, String url) {
        return this.mvc.perform(
                        get(url)
                            .content(request)
                            .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse().getContentAsString();
    }
    @SneakyThrows
    protected String getSyncResponseToPOST(String request, String url) {
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
                .getResponse().getContentAsString();
    }

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

        return result.getResponse().getContentAsString();
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

        return result.getResponse().getContentAsString();
    }

}
