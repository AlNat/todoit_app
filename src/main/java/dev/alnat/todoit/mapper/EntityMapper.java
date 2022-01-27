package dev.alnat.todoit.mapper;

import java.util.List;

/**
 * Created by @author AlNat on 28.01.2022.
 * Licensed by Apache License, Version 2.0
 */
public interface EntityMapper<Entity, DTO> {

    DTO entityToDTO(Entity domain);
    List<DTO> entityToDTO(List<Entity> collection);

    Entity dtoToEntity(DTO dto);
    List<Entity> dtoToEntity(List<DTO> collection);

}
