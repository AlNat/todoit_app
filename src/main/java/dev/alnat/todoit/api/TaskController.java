package dev.alnat.todoit.api;

import dev.alnat.todoit.enums.TaskStatus;
import dev.alnat.todoit.search.TaskSearchRequest;
import dev.alnat.todoit.search.TaskSearchResponse;
import dev.alnat.todoit.service.TaskService;
import dev.alnat.todoit.types.TaskDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.Duration;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.REQUEST_TIMEOUT;

/**
 * Created by @author AlNat on 23.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/task", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Task REST API", description = "API для взаимодействия с задачами")
public class TaskController {

    private final TaskService service;

    @Value("${custom.task.paging.default-timeout}")
    private Duration pagingTimeout;

    @Operation(summary = "Постраничный поиск задач",
            description = "Через Swagger нельзя отобразить сортировку, их нужно передавать через массив," +
                    "формата sorting[0].sortBy=field1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Поиск произведен"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "408", description = "Поиск превысил время ожидания", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(type = "string")))
    })
    @GetMapping("/")
    public DeferredResult<TaskSearchResponse> getPaging(@ParameterObject @Parameter(in = ParameterIn.QUERY, required = true,
                                                            description = "Объект поиска для передачи в параметрах")
                                                        @Valid TaskSearchRequest request) {
        var deferredResult = new DeferredResult<TaskSearchResponse>(pagingTimeout.toMillis());

        deferredResult.onTimeout(() -> {
            throw new ResponseStatusException(REQUEST_TIMEOUT);
        });

        deferredResult.onError((throwable) -> {
            log.error("Exception at differed result", throwable);
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR);
        });

        service.getPagingDeferred(request, deferredResult);

        // Тут поток запаркуется
        return deferredResult;
    }

    @Operation(summary = "Поиск задачи по идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Поиск произведен"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(type = "string")))
    })
    @GetMapping("/{id}")
    public TaskDTO get(@Parameter(description = "Идентификатор задачи", required = true, in = ParameterIn.PATH, example = "1")
                       @PathVariable Long id) {
        return service.get(id);
    }

    @Operation(summary = "Сохранение новой задачи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Поиск произведен"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(type = "string")))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/")
    public TaskDTO save(@io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "DTO задачи")
                     @RequestBody TaskDTO dto) {
        return service.save(dto);
    }

    @Operation(summary = "Удаление задачи по идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Удаление произведено"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(type = "string")))
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/{id}")
    public void delete(@Parameter(description = "Идентификатор задачи", required = true, in = ParameterIn.PATH, example = "1")
                       @PathVariable Long id) {
        service.delete(id);
    }

    @Operation(summary = "Обновление задачи по идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Обновление произведено"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(type = "string")))
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@Parameter(description = "Идентификатор задачи", required = true, in = ParameterIn.PATH, example = "1")
                       @PathVariable Long id,
                       @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "DTO задачи")
                       @RequestBody TaskDTO dto) {
        service.update(id, dto);
    }

    @Operation(summary = "Обновление статуса задачи по идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Обновление произведено"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Задача не найдена", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(type = "string")))
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping(value = "/{id}")
    public void updateStatus(@Parameter(description = "Идентификатор задачи", required = true, in = ParameterIn.PATH, example = "1")
                             @PathVariable Long id,
                             @Parameter(description = "Новый статус", required = true, in = ParameterIn.QUERY, example = "DONE")
                             @RequestParam TaskStatus status) {
        service.updateStatus(id, status);
    }

}
