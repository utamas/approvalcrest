package com.github.karsaig.json;

import java.util.List;
import java.util.Set;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

public interface JsonBuilder {
    @NotNull JsonBuilder initialize();

    @NotNull JsonBuilder registerTypesToIgnore(List<Class<?>> typesToIgnore);

    @NotNull JsonBuilder registerFieldsToIgnore(List<Matcher<String>> fieldsToIgnore);

    @NotNull JsonBuilder registerCircularReferenceTypes(Set<Class<?>> circularReferenceTypes);

    @NotNull JsonBuilder setPrettyPrinting();

    @NotNull JsonBuilder addExtraConfiguration(JsonConfiguration additionalConfig);

    @NotNull <T> JsonBuilder registerTypeHierarchyAdapter(Class<T> setClass, JsonSerializer<Set> jsonSerializer);

    @NotNull Json build();
}
