package dev.alnat.todoit.repository;

import dev.alnat.todoit.model.Attachment;
import dev.alnat.todoit.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by @author AlNat on 23.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    /**
     * Превью вложений (без БД)
     * @param task задача
     * @return список ее вложений в виде прьвью
     */
    @Query("SELECT new Attachment(a.id, a.created, a.size, a.name) FROM Attachment a where a.task = :task")
    List<Attachment> getPreviewByTask(Task task);

}
