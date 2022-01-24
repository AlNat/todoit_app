package dev.alnat.todoit.mapper;

import dev.alnat.todoit.model.Task;
import dev.alnat.todoit.types.TaskDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Created by @author AlNat on 23.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@Mapper(componentModel = "spring")
public interface TaskToTaskDTOMapper {

    TaskDTO toTaskDTO(Task task);

    List<TaskDTO> toTaskDTOList(List<Task> taskList);

}
