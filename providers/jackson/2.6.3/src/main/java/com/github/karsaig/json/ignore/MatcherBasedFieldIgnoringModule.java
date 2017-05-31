package com.github.karsaig.json.ignore;

import java.util.Arrays;

import org.hamcrest.Matcher;

import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

public class MatcherBasedFieldIgnoringModule extends AbstractFieldIgnoringModule {

    private static final String MODULE_NAME = "MatcherBasedFieldIgnoringModule";

    private static BeanSerializerModifier createModifier(Matcher<String>[] matchers) {
        return new MatcherBasedFieldIgnoringBeanSerializerModifier(Arrays.asList(matchers));
    }

    public MatcherBasedFieldIgnoringModule(Matcher<String>... matchers) {
        super(createModifier(matchers), MODULE_NAME);
    }
}
