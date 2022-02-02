package dev.alnat.todoit.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static dev.alnat.todoit.constants.DateFormats.DATE_TIME_FORMAT;

/**
 * Created by @author AlNat on 27.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    public static<T> List<T> getSubList(List<T> list, int maxValues) {
        return list.subList(0, maxValues);
    }

    public static LocalDateTime parseDateTime(String stringDate) {
        if (!StringUtils.hasText(stringDate)) {
            return null;
        }

        try {
            final var decodedString = URLDecoder.decode(stringDate, StandardCharsets.UTF_8);
            return LocalDateTime.parse(decodedString, DATE_TIME_FORMAT);
        } catch (Exception e) {
            return LocalDateTime.parse(stringDate, DATE_TIME_FORMAT);
        }
    }

}
