package dev.alnat.todoit.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурируем чтобы корректно парсить дату из GET параметров
 *
 * Created by @author AlNat on 27.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

    private final URLLocalDateTimeConverter urlLocalDateTimeConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(urlLocalDateTimeConverter);
    }

}
