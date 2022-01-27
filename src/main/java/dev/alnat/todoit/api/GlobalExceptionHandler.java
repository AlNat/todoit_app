package dev.alnat.todoit.api;

import brave.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Created by @author AlNat on 27.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Tracer tracer;

    /**
     * Ошибки с размером файла
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<String> handleMultipartException(final MultipartException ex) {
        log.warn("Multipart exception", ex);
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .contentType(MediaType.TEXT_PLAIN)
                .build();
    }

    /**
     * Ошибки с кодом статуса
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(final ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getMessage());
    }

    /**
     * Глобальный перехват NPE
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(final NullPointerException ex) {
        log.error("Handle NPE", ex);
        final String traceId = tracer.currentSpan().context().traceIdString();
        return ResponseEntity
                .internalServerError()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Internal Server Error, traceId=[" + traceId + "]");
    }

    /**
     * Глобальный перехват любой ошибки
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(final Exception ex) {
        log.error("Handle unexpected exception: {}", ex.getMessage(), ex);
        final String traceId = tracer.currentSpan().context().traceIdString();
        return ResponseEntity
                .internalServerError()
                .contentType(MediaType.TEXT_PLAIN)
                .body("Internal Server Error, traceId=[" + traceId + "]");
    }

}
