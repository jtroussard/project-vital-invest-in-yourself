package com.devlife4me.projectvital.util;

import org.springframework.web.util.HtmlUtils;

public class SanitizationUtils {

    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(input.trim());
    }
}
