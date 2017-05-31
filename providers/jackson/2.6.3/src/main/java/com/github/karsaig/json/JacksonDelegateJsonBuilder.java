package com.github.karsaig.json;

import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

import java.util.List;
import java.util.Set;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

public class JacksonDelegateJsonBuilder implements JsonBuilder {
    private ObjectMapper delegate;

    private List<Class<?>> typesToIgnore;
    private List<Matcher<String>> fieldsToIgnore;
    private Set<Class<?>> circularReferenceTypes;
    private JsonConfiguration additionalConfig;

    @Override
    public @NotNull JsonBuilder initialize() {
        delegate = new ObjectMapper();
        delegate.registerModule(new GuavaModule());
        return this;
    }

    @Override
    public @NotNull JsonBuilder registerTypesToIgnore(@NotNull List<Class<?>> typesToIgnore) {
        this.typesToIgnore = typesToIgnore;
        return this;
    }

    @Override
    public @NotNull JsonBuilder registerFieldsToIgnore(@NotNull List<Matcher<String>> fieldsToIgnore) {
        this.fieldsToIgnore = fieldsToIgnore;
        return this;
    }

    @Override
    public @NotNull JsonBuilder registerCircularReferenceTypes(@NotNull Set<Class<?>> circularReferenceTypes) {
        this.circularReferenceTypes = circularReferenceTypes;
        return this;
    }

    @Override
    public @NotNull JsonBuilder setPrettyPrinting() {
        delegate.enable(INDENT_OUTPUT);
        return this;
    }

    @Override
    public @NotNull JsonBuilder addExtraConfiguration(@NotNull JsonConfiguration additionalConfig) {
        this.additionalConfig = additionalConfig;
        return this;
    }

    @Override
    public @NotNull Json build() {
        // FIXME

        return new JacksonDelegateJson(delegate);
    }
}
