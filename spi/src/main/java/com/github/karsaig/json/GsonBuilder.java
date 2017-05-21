package com.github.karsaig.json;

import java.util.List;
import java.util.Set;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

public interface GsonBuilder {
    @NotNull GsonBuilder initialize();

    @NotNull GsonBuilder registerTypesToIgnore(List<Class<?>> typesToIgnore);

    @NotNull GsonBuilder registerFieldsToIgnore(List<Matcher<String>> fieldsToIgnore);

    @NotNull GsonBuilder registerCircularReferenceTypes(Set<Class<?>> circularReferenceTypes);

    @NotNull GsonBuilder setPrettyPrinting();

    @NotNull GsonBuilder addExtraConfiguration(GsonConfiguration additionalConfig);

    @NotNull <T> GsonBuilder registerTypeHierarchyAdapter(Class<T> setClass, JsonSerializer<Set> jsonSerializer);

    @NotNull Gson build();
}
