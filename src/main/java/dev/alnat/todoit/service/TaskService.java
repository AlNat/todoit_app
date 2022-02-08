package dev.alnat.todoit.service;

import dev.alnat.todoit.enums.TaskStatus;
import dev.alnat.todoit.search.TaskSearchRequest;
import dev.alnat.todoit.search.TaskSearchResponse;
import dev.alnat.todoit.types.TaskDTO;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Optional;

/**
 * Created by @author AlNat on 06.02.2022.
 * Licensed by Apache License, Version 2.0
 */
public interface TaskService {

    void getPagingDeferred(TaskSearchRequest request, DeferredResult<TaskSearchResponse> deferredResult);

    TaskSearchResponse getPaging(TaskSearchRequest request);

    TaskDTO get(Long id);

    Optional<TaskDTO> find(Long id);

    TaskDTO save(TaskDTO dto);

    void delete(Long id);

    TaskDTO update(Long id, TaskDTO dto);

    void updateStatus(Long id, TaskStatus status);

}
