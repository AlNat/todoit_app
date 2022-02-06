package dev.alnat.todoit.tools;

import dev.alnat.todoit.enums.TaskStatus;
import dev.alnat.todoit.search.TaskSearchRequest;
import dev.alnat.todoit.types.TaskDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by @author AlNat on 04.02.2022.
 * Licensed by Apache License, Version 2.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TaskUtils {

    //////////////////////
    // Генераторы задач //
    //////////////////////

    public static TaskDTO generateTask(Long id) {
        return TaskDTO.builder()
                .id(id)
                .status(TaskStatus.PLANNED)
                .title("Some Task Name")
                .planned(LocalDateTime.now().plusHours(1L))
                .build();
    }

    public static TaskDTO generateTask(String name) {
        return TaskDTO.builder()
                .id(1L)
                .status(TaskStatus.PLANNED)
                .title(name)
                .planned(LocalDateTime.now().plusHours(1L))
                .build();
    }

    public static TaskDTO generateTask(Long id, String name) {
        return TaskDTO.builder()
                .id(id)
                .status(TaskStatus.PLANNED)
                .title(name)
                .planned(LocalDateTime.now().plusHours(1L))
                .build();
    }


    ///////////////////////////////////////
    // Генераторы для поисковых запросов //
    ///////////////////////////////////////

    public static TaskSearchRequest generateTaskSearch() {
        return TaskSearchRequest.builder().build();
    }

    public static TaskSearchRequest generateTaskSearch(TaskStatus status) {
        return TaskSearchRequest.builder()
                .statusList(Collections.singletonList(status))
                .build();
    }

    public static TaskSearchRequest generateTaskSearch(Integer limit, Integer offset) {
        return TaskSearchRequest.builder()
                .limit(limit)
                .offset(offset)
                .build();
    }

    public static TaskSearchRequest generateTaskSearch(TaskStatus... statuses) {
        return TaskSearchRequest.builder()
                .statusList(Arrays.asList(statuses))
                .build();
    }

}
