package dev.alnat.todoit.service.impl;

import dev.alnat.todoit.mapper.AttachmentPreviewMapper;
import dev.alnat.todoit.model.Attachment;
import dev.alnat.todoit.model.Task;
import dev.alnat.todoit.model.dto.FileDTO;
import dev.alnat.todoit.repository.AttachmentRepository;
import dev.alnat.todoit.repository.TaskRepository;
import dev.alnat.todoit.service.AttachmentService;
import dev.alnat.todoit.types.AttachmentPreview;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Created by @author AlNat on 23.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeneralAttachmentService implements AttachmentService {

    private final TaskRepository taskRepository;
    private final AttachmentRepository attachmentRepository;
    private final AttachmentPreviewMapper previewMapper;

    @Override
    public Optional<FileDTO> fetchAttachmentById(Long id) {
        var attachOpt = attachmentRepository.findById(id);

        log.debug("By id {} {} attachment", id, attachOpt.isEmpty() ? "not found" : "found");

        if (attachOpt.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(FileDTO.from(attachOpt.get()));
    }

    @Override
    @Transactional
    public void saveAttachmentToTask(MultipartFile file, Long taskId) {
        var task = fetchTaskOrThrow(taskId);

        byte[] data;
        try {
            data = file.getBytes();
        } catch (IOException e) {
            log.error("Exception at reading attachment file", e);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error at receiving file");
        }

        var attach = new Attachment();
        attach.setTask(task);
        attach.setData(data);
        attach.setName(file.getOriginalFilename());
        attach.setSize(file.getSize());

        attachmentRepository.save(attach);
        log.debug("Save attachment {}", attach);
    }

    @Override
    public List<AttachmentPreview> fetchAttachmentPreviewByTask(Long taskId) {
        var task = fetchTaskOrThrow(taskId);
        return previewMapper.entityToDTO(attachmentRepository.getPreviewByTask(task));
    }


    /**
     * Проверка наличия задачи или то что она в скрытом статусе
     */
    private Task fetchTaskOrThrow(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty() || task.get().getStatus().isHide()) {
            log.warn("Not found task {} on saving attachment", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found task " + id);
        }

        return task.get();
    }

}
