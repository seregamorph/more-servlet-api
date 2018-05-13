package org.moreservletapi.log;

import javax.annotation.Nullable;

public class LogEscapeUtils {

    private static final PercentEscaper ESCAPER_WITH_LF = new PercentEscaper("\n $#%!?={}[]()<>\"':;,._/\\*-+@&№" +
            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя" +
            "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ", false);

    public static String escapeWithLf(@Nullable String str) {
        if (str == null) {
            return "null";
        }
        str = str.replace("\r", "");
        str = str.replace("\t", "    ");
        return ESCAPER_WITH_LF.escape(str);
    }

    private LogEscapeUtils() {
    }
}
