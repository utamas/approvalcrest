package com.google.gson;

import java.util.List;
import java.util.Set;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

import com.github.karsaig.json.*;
import com.github.karsaig.json.GsonBuilder;
import com.github.karsaig.json.JsonSerializer;

// FIXME: rename me.
public class GsonGsonBuilder implements com.github.karsaig.json.GsonBuilder {
    @Override
    public @NotNull com.github.karsaig.json.GsonBuilder initialize() {
        return this;
    }

    @Override
    public @NotNull com.github.karsaig.json.GsonBuilder registerTypesToIgnore(List<Class<?>> typesToIgnore) {
        return this;
    }

    @Override
    public @NotNull com.github.karsaig.json.GsonBuilder registerFieldsToIgnore(List<Matcher<String>> fieldsToIgnore) {
        return this;
    }

    @Override
    public @NotNull com.github.karsaig.json.GsonBuilder registerCircularReferenceTypes(Set<Class<?>> circularReferenceTypes) {
        return this;
    }

    @Override
    public @NotNull com.github.karsaig.json.GsonBuilder setPrettyPrinting() {
        return this;
    }

    @Override
    public @NotNull com.github.karsaig.json.GsonBuilder addExtraConfiguration(GsonConfiguration additionalConfig) {
        return this;
    }

    @NotNull
    @Override public <T> GsonBuilder registerTypeHierarchyAdapter(Class<T> setClass, JsonSerializer<Set> jsonSerializer) {
        return this;
    }

    @Override
    public @NotNull com.github.karsaig.json.Gson build() {
        return new GsonDelegate();
    }
}
