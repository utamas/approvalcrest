package com.github.karsaig.json.module;

import java.util.List;

import org.hamcrest.Matcher;

public class MatcherBasedFieldIgnoringModule extends AbstractFieldIgnoringModule {
    private static final String MODULE_NAME = "MatcherBasedFieldIgnoringModule";

    public MatcherBasedFieldIgnoringModule(List<Matcher<String>> matchers) {
        super(new MatcherBasedFieldIgnoringBeanSerializerModifier(matchers), MODULE_NAME);
    }
}
