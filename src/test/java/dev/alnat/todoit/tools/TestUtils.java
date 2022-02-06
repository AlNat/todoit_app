package dev.alnat.todoit.tools;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by @author AlNat on 06.02.2022.
 * Licensed by Apache License, Version 2.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestUtils {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> reference = new TypeReference<>() {};


    public static LinkedMultiValueMap<String, String> objectToMap(Object object) {
        LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<>();
        objectToMap(object, null, linkedMultiValueMap);
        return linkedMultiValueMap;
    }

    // Рекурсивно проходим весь объект и созданием из него набор параметров в формате GET строки
    public static void objectToMap(Object object, String sourceField, LinkedMultiValueMap<String, String> linkedMultiValueMap) {
        if (object == null || isItEmptyCollection(object)) {
            return;
        }

        Map<String, Object> map = mapper.convertValue(object, reference);

        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (e.getValue() == null || isItEmptyCollection(e.getValue())) {
                continue;
            }

            // Если это вложенная коллекция -- проходим ее всю
            if (e.getValue() instanceof Collection<?> nestedCollection) {
                var firstObj = nestedCollection.stream().findFirst().get();

                // Если это уже готовые строки (как статусы -- можно сразу собрать параметры, без индексов)
                if (firstObj instanceof String) {
                    for (Object str : nestedCollection) {
                        linkedMultiValueMap.add(e.getKey(), String.valueOf(str));
                    }
                } else { // Иначе это вложенный объект и нужно передать в параметрах его индекс через obj[i].filed
                    for (int i = 0; i < nestedCollection.size(); i++) {
                        objectToMap(nestedCollection.iterator().next(), e.getKey() + "%5B" + i + "%5D", linkedMultiValueMap);
                    }
                }
                continue;
            }

            // Если это вложенный куда-то объект, то именуем соответствующим образом
            if (StringUtils.hasLength(sourceField)) {
                var key = String.format("%s.%s", sourceField, e.getKey());
                linkedMultiValueMap.add(key, String.valueOf(e.getValue()));
            } else { // Ну и иначе это просто набор параметров
                linkedMultiValueMap.add(e.getKey(), String.valueOf(e.getValue()));
            }
        }
    }

    private static boolean isItEmptyCollection(Object obj) {
        return obj instanceof Collection && ((Collection<?>) obj).isEmpty();
    }

}
