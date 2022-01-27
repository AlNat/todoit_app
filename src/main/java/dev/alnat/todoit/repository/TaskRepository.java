package dev.alnat.todoit.repository;

import dev.alnat.todoit.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для взаимодействия с БД по задачам.
 * Предоставляет поисковый метод реализованный вручную
 * @see dev.alnat.todoit.repository.impl.TaskSearchRepositoryImpl
 *
 * Created by @author AlNat on 23.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, TaskSearchRepository {

}
