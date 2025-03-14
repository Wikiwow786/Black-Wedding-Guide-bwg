package com.bwg.util;

import java.util.Optional;

public class NameUtils {

    private NameUtils() {
    }

    public static String formatFullName(String firstName, String lastName) {
        return String.join(" ",
                Optional.ofNullable(firstName).orElse(""),
                Optional.ofNullable(lastName).orElse("")
        ).trim();
    }
}