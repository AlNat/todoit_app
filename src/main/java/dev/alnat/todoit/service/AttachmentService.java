package dev.alnat.todoit.service;

import dev.alnat.todoit.model.dto.FileDTO;
import dev.alnat.todoit.types.AttachmentPreview;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Created by @author AlNat on 06.02.2022.
 * Licensed by Apache License, Version 2.0
 */
public interface AttachmentService {

    Optional<FileDTO> fetchAttachmentById(Long id);

    void saveAttachmentToTask(MultipartFile file, Long taskId);

    List<AttachmentPreview> fetchAttachmentPreviewByTask(Long taskId);

}
