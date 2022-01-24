package dev.alnat.todoit.model.converter;

import dev.alnat.todoit.enums.TaskStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Конвертер для статусов в БД и обратно
 *
 * Created by @author AlNat on 23.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@Converter
public class TaskStatusConverter implements AttributeConverter<TaskStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TaskStatus taskStatus) {
        return taskStatus == null ? null : taskStatus.getValue();
    }

    @Override
    public TaskStatus convertToEntityAttribute(Integer status) {
        return status == null ? null : TaskStatus.ofValue(status);
    }
}
