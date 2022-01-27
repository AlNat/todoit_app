package dev.alnat.todoit.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by @author AlNat on 27.01.2022.
 * Licensed by Apache License, Version 2.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {

    public static<T> List<T> getSubList(List<T> list, int maxValues) {
        return list.subList(0, maxValues);
    }

}
