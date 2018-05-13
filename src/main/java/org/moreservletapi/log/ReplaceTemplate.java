package org.moreservletapi.log;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplaceTemplate {

    private final Pattern pattern;
    private final String replaceString;

    public ReplaceTemplate(String regex, String replaceString) {
        this(Pattern.compile(regex), replaceString);
    }

    public ReplaceTemplate(Pattern pattern, String replaceString) {
        this.pattern = Objects.requireNonNull(pattern, "pattern");
        this.replaceString = Objects.requireNonNull(replaceString, "replaceString");
    }

    String apply(String source) {
        Matcher matcher = pattern.matcher(source);
        return matcher.replaceAll(replaceString);
    }
}
