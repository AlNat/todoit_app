package dev.alnat.todoit.config;

import dev.alnat.todoit.util.Utils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * Парсер даты и времени из URL-а
 *
 * Created by @author AlNat on 27.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@Component
public class URLLocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(String stringDate) {
        try {
            return Utils.parseDateTime(stringDate);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

}
