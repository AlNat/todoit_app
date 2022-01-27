package dev.alnat.todoit.config;

import dev.alnat.todoit.enums.TaskStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Делаем список скрытых статусов бином для удобства и оптимизации
 *
 * Created by @author AlNat on 27.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@Configuration
public class AppConfiguration {

    /**
     * Список скрытых статусов
     */
    @Bean
    public List<TaskStatus> hideStatusList() {
        return Arrays.stream(TaskStatus.values()).filter(t -> !t.isHide()).toList();
    }

}
