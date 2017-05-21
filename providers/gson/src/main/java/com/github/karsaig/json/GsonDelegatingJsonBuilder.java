package com.github.karsaig.json;

import java.util.List;
import java.util.Set;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

// FIXME: rename me.
public class GsonDelegatingJsonBuilder implements com.github.karsaig.json.JsonBuilder {
    @Override
    public @NotNull JsonBuilder initialize() {
        return this;
    }

    @Override
    public @NotNull JsonBuilder registerTypesToIgnore(List<Class<?>> typesToIgnore) {
        return this;
    }

    @Override
    public @NotNull JsonBuilder registerFieldsToIgnore(List<Matcher<String>> fieldsToIgnore) {
        return this;
    }

    @Override
    public @NotNull JsonBuilder registerCircularReferenceTypes(Set<Class<?>> circularReferenceTypes) {
        return this;
    }

    @Override
    public @NotNull JsonBuilder setPrettyPrinting() {
        return this;
    }

    @Override
    public @NotNull JsonBuilder addExtraConfiguration(JsonConfiguration additionalConfig) {
        return this;
    }

    @NotNull
    @Override public <T> JsonBuilder registerTypeHierarchyAdapter(Class<T> setClass, JsonSerializer<Set> jsonSerializer) {
        return this;
    }

    @Override
    public @NotNull Json build() {
        return new JsonDelegate();
    }
}
