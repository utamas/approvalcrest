package com.google.gson;

import java.util.List;
import java.util.Set;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

// FIXME: rename me.
public class GsonGsonBuilder implements GsonBuilder {
    @Override
    public @NotNull GsonBuilder initialize() {
        return this;
    }

    @Override
    public @NotNull GsonBuilder registerTypesToIgnore(List<Class<?>> typesToIgnore) {
        return this;
    }

    @Override
    public @NotNull GsonBuilder registerFieldsToIgnore(List<Matcher<String>> fieldsToIgnore) {
        return this;
    }

    @Override
    public @NotNull GsonBuilder registerCircularReferenceTypes(Set<Class<?>> circularReferenceTypes) {
        return this;
    }

    @Override
    public @NotNull GsonBuilder setPrettyPrinting() {
        return this;
    }

    @Override
    public @NotNull GsonBuilder addExtraConfiguration(GsonConfiguration additionalConfig) {
        return this;
    }

    @NotNull
    @Override public <T> GsonBuilder registerTypeHierarchyAdapter(Class<T> setClass, JsonSerializer<Set> jsonSerializer) {
        return this;
    }

    @Override
    public @NotNull Gson build() {
        return new GsonDelegate();
    }
}
