package dev.alnat.todoit.model.dto;

import dev.alnat.todoit.model.Attachment;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by @author AlNat on 27.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@Data
@AllArgsConstructor
public class FileDTO {

    private String filename;
    private byte[] file;

    public static FileDTO from(Attachment attachment) {
        return new FileDTO(attachment.getName(), attachment.getData());
    }

}
