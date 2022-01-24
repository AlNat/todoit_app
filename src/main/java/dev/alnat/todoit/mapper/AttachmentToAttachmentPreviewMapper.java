package dev.alnat.todoit.mapper;

import dev.alnat.todoit.model.Attachment;
import dev.alnat.todoit.types.AttachmentPreview;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Created by @author AlNat on 23.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@Mapper(componentModel = "spring")
public interface AttachmentToAttachmentPreviewMapper {

    AttachmentPreview toTaskDTO(Attachment task);

    List<AttachmentPreview> toTaskDTOList(List<Attachment> taskList);

}
