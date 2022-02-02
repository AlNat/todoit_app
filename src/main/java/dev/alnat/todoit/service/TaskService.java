package dev.alnat.todoit.service;

import dev.alnat.todoit.enums.TaskStatus;
import dev.alnat.todoit.mapper.TaskMapper;
import dev.alnat.todoit.model.Task;
import dev.alnat.todoit.repository.TaskRepository;
import dev.alnat.todoit.search.TaskSearchRequest;
import dev.alnat.todoit.search.TaskSearchResponse;
import dev.alnat.todoit.types.TaskDTO;
import dev.alnat.todoit.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Created by @author AlNat on 23.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;


    @Async
    public void getPagingDeferred(TaskSearchRequest request, DeferredResult<TaskSearchResponse> deferredResult) {
        var result = getPaging(request);
        deferredResult.setResult(result);
    }

    public TaskSearchResponse getPaging(TaskSearchRequest request) {
        List<Task> entityList;
        try {
            entityList = taskRepository.findByParam(request);
        } catch (InvalidDataAccessApiUsageException e) {
            log.error("Paging sorting invalid", e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid search request " + request);
        }

        var list = taskMapper.entityToDTO(entityList);

        TaskSearchResponse result = new TaskSearchResponse();
        result.setRequest(request);

        if (list.size() > request.getLimit()) {
            result.setHasMore(true);
            result.setData(Utils.getSubList(list, request.getLimit()));
        } else {
            result.setHasMore(false);
            result.setData(list);
        }

        return result;
    }

    public TaskDTO get(Long id) {
        Task task = fetchOrThrow(id);

        return taskMapper.entityToDTO(task);
    }

    public Optional<TaskDTO> find(Long id) {
        var taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty() || taskOpt.get().getStatus().isHide()) {
            return Optional.empty();
        }

        return Optional.of(taskMapper.entityToDTO(taskOpt.get()));
    }

    public void save(TaskDTO dto) {
        Task newTask = taskMapper.dtoToEntity(dto);
        taskRepository.save(newTask);
    }

    @Transactional
    public void delete(Long id) {
        Task task = fetchOrThrow(id);

        task.setStatus(TaskStatus.DELETED);
        taskRepository.save(task);

        log.info("Task {} was deleted", id);
    }

    public void update(Long id, TaskDTO dto) {
        fetchOrThrow(id); // Проверяем на обновление удаленной задачи

        Task newTask = taskMapper.dtoToEntity(dto);
        newTask.setId(id);
        taskRepository.save(newTask);
    }

    public void updateStatus(Long id, TaskStatus status) {
        Task task = fetchOrThrow(id);
        task.setStatus(status);
        taskRepository.save(task);
    }

    private Task fetchOrThrow(Long id) throws ResponseStatusException {
        var taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty() || taskOpt.get().getStatus().isHide()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task with id " + id + " not found");
        }

        return taskOpt.get();
    }

}
