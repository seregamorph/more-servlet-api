package org.moreservletapi.log;

public class ReplaceTemplates {

    public static final ReplaceTemplate AUTHORIZATION_BASIC = new ReplaceTemplate(
            "Authorization: Basic [\\w=+/]+", "Authorization: Basic ***");

    public static ReplaceTemplate maskXmlExtra(String extraName) {
        return new ReplaceTemplate(
                "<extra\\s+name=\"" + extraName + "\">[^<]+</extra>",
                "<extra name=\"" + extraName + "\">***</extra>"
        );
    }

    public static ReplaceTemplate maskXmlTag(String tagName) {
        return new ReplaceTemplate(
                "<" + tagName + ">[^<]+</" + tagName + ">",
                "<" + tagName + ">***</" + tagName + ">"
        );
    }

    public static ReplaceTemplate maskJsonField(String fieldName) {
        return maskJsonField(fieldName, "[^\"]+");
    }

    public static ReplaceTemplate maskJsonField(String fieldName, /*@Language("RegExp")*/ String valueRegex) {
        return new ReplaceTemplate(
                "\"" + fieldName + "\"(\\s*):(\\s*)\"" + valueRegex + "\"",
                "\"" + fieldName + "\"$1:$2\"***\""
        );
    }

    private ReplaceTemplates() {
    }
}
