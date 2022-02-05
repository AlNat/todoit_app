package dev.alnat.todoit.tools;

import dev.alnat.todoit.enums.TaskStatus;
import dev.alnat.todoit.search.TaskSearchRequest;
import dev.alnat.todoit.types.TaskDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Created by @author AlNat on 04.02.2022.
 * Licensed by Apache License, Version 2.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskUtils {

    //////////////////////
    // Генераторы задач //
    //////////////////////

    public static TaskDTO generateTask(Integer id) {
        // TODO Implement Me
        return new TaskDTO();
    }



    ///////////////////////////////////////
    // Генераторы для поисковых запросов //
    ///////////////////////////////////////

    public static TaskSearchRequest generateTaskSearch() {
        // TODO Implement Me
        return new TaskSearchRequest();
    }

    public static TaskSearchRequest generateTaskSearch(TaskStatus status) {
        // TODO Implement Me
        return new TaskSearchRequest();
    }


    public static MultiValueMap<String, String> toMap(TaskSearchRequest request) {
        // TODO Implement Me
        return new LinkedMultiValueMap<>();
    }


}
