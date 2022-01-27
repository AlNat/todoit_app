package dev.alnat.todoit.api;

import dev.alnat.todoit.service.AttachmentService;
import dev.alnat.todoit.types.AttachmentPreview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Created by @author AlNat on 23.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@RestController
@RequestMapping("/api/v1/attachment")
@RequiredArgsConstructor
@Tag(name = "Attachment REST API", description = "API для взаимодействия с вложения к задачам")
public class AttachmentController {

    private final AttachmentService service;

    @Operation(summary = "Получения списка превью вложений по задаче")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получен список вложений к задаче"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Не найдено вложение", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(type = "string")))
    })
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AttachmentPreview> fetchAttachmentPreviewByTask(@Parameter(description = "Id задачи", example = "1")
                                                                @RequestParam Long taskId) {
        return service.fetchAttachmentPreviewByTask(taskId);
    }

    @Operation(summary = "Получения тела вложений по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Сохранение выполнено успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Не найдено вложение", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(type = "string")))
    })
    @GetMapping("/{attachmentId}")
    public ResponseEntity<byte[]> fetchAttachmentById(@Parameter(description = "Id вложения", example = "123", in = ParameterIn.PATH)
                                                      @PathVariable Long attachmentId) {
        var fileDTOOpt = service.fetchAttachmentById(attachmentId);

        if (fileDTOOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found attachment with id " + attachmentId);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDTOOpt.get().getFilename() + "\"")
                .body(fileDTOOpt.get().getFile());
    }

    @Operation(summary = "Сохранение нового вложения к задаче")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Сохранение выполнено успешно"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Не найдена задача", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "422", description = "Вложение не может быть получено", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content(schema = @Schema(type = "string")))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value="/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveAttachmentToTask(@Parameter(description = "Файл") // , schema = @Schema(type = "string", format = "binary")
                                     @RequestParam(value = "file") MultipartFile file,
                                     @Parameter(description = "Id задачи", example = "1", in = ParameterIn.QUERY)
                                     @RequestParam("taskId") Long taskId) {
        service.saveAttachmentToTask(file, taskId);
    }

}
