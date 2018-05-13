package org.moreservletapi.log;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ReplaceTemplateList {

    private final List<ReplaceTemplate> replaceTemplates = new CopyOnWriteArrayList<>();

    public ReplaceTemplateList add(String regex, String replacement) {
        ReplaceTemplate replaceTemplate = new ReplaceTemplate(regex, replacement);
        return add(replaceTemplate);
    }

    public ReplaceTemplateList add(ReplaceTemplate replaceTemplate) {
        replaceTemplates.add(replaceTemplate);
        return this;
    }

    public ReplaceTemplateList addAll(ReplaceTemplate... replaceTemplates) {
        this.replaceTemplates.addAll(Arrays.asList(replaceTemplates));
        return this;
    }

    public String apply(@Nonnull String msg) {
        for (ReplaceTemplate replaceTemplate : replaceTemplates) {
            msg = replaceTemplate.apply(msg);
        }
        return msg;
    }

    public static String applyAll(@Nonnull String msg, ReplaceTemplateList... lists) {
        for (ReplaceTemplateList list : lists) {
            msg = list.apply(msg);
        }
        return msg;
    }
}
