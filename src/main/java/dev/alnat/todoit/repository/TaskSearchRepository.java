package dev.alnat.todoit.repository;

import dev.alnat.todoit.model.Task;
import dev.alnat.todoit.search.TaskSearchRequest;

import java.util.List;

/**
 * Created by @author AlNat on 23.01.2022.
 * Licensed by Apache License, Version 2.0
 */
public interface TaskSearchRepository {

    List<Task> findByParam(TaskSearchRequest searchDTO);

}
