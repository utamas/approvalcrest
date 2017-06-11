package com.github.karsaig.json;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hamcrest.Matcher;
import org.jetbrains.annotations.NotNull;

import com.github.karsaig.json.graph.GraphAdapterBuilder;
import com.github.karsaig.json.graph.GsonGraphAdapterBuilder;
import com.github.karsaig.json.gson.MapJsonSerializer;
import com.github.karsaig.json.gson.SetAndMapMarkingFieldNamingStrategy;
import com.github.karsaig.json.gson.SetJsonSerializer;

import com.google.common.base.Optional;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapterFactory;

public class GsonDelegateJsonBuilder implements JsonBuilder {
    private GsonBuilder delegate;

    private List<Class<?>> typesToIgnore;
    private List<Matcher<String>> fieldsToIgnore;
    private Set<Class<?>> circularReferenceTypes;
    private JsonConfiguration additionalConfig;

    @Override
    public @NotNull JsonBuilder initialize() {
        delegate = new GsonBuilder();
        delegate.registerTypeAdapter(Optional.class, new OptionalSerializer());
        delegate.registerTypeHierarchyAdapter(Set.class, new SetJsonSerializer(delegate));
        delegate.registerTypeHierarchyAdapter(Map.class, new MapJsonSerializer(delegate));
        delegate.setFieldNamingStrategy(new SetAndMapMarkingFieldNamingStrategy());
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
        delegate.setPrettyPrinting();
        return this;
    }

    @Override
    public @NotNull JsonBuilder addExtraConfiguration(@NotNull JsonConfiguration additionalConfig) {
        this.additionalConfig = additionalConfig;
        return this;
    }

    @Override
    public @NotNull Json build() {
        if (!circularReferenceTypes.isEmpty()) {
            registerCircularReferenceTypes(circularReferenceTypes, this);
        }

        if (!typesToIgnore.isEmpty() || !fieldsToIgnore.isEmpty()) {
            delegate.setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    for (Matcher<String> matcher : fieldsToIgnore) {
                        if (matcher.matches(fieldAttributes.getName())) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return (typesToIgnore.contains(clazz));
                }
            });
        }

        if (additionalConfig != null) {
            Map<Type, List<Object>> typeAdapterMap = additionalConfig.getTypeAdapters();
            for (Type type : typeAdapterMap.keySet()) {
                if (typeAdapterMap.get(type) != null) {
                    for (Object o : typeAdapterMap.get(type)) {
                        delegate.registerTypeAdapter(type, o);
                    }
                }
            }
        }

        return new GsonDelegateJson(delegate.create());
    }

    public void registerTypeAdapterFactory(TypeAdapterFactory factory) {
        delegate.registerTypeAdapterFactory(factory);
    }

    public void registerTypeAdapter(Type key, TypeAdapterFactory factory) {
        delegate.registerTypeAdapter(key, factory);
    }

    private static void registerCircularReferenceTypes(Set<Class<?>> circularReferenceTypes, JsonBuilder jsonBuilder) {
        GraphAdapterBuilder graphAdapterBuilder = new GsonGraphAdapterBuilder();
        for (Class<?> circularReferenceType : circularReferenceTypes) {
            graphAdapterBuilder.addType(circularReferenceType);
        }
        graphAdapterBuilder.registerOn(jsonBuilder);
    }

    private static class OptionalSerializer<T> implements JsonSerializer<Optional<T>> {
        @Override
        public com.google.gson.JsonElement serialize(Optional<T> src, Type typeOfSrc, JsonSerializationContext context) {
            com.google.gson.JsonArray result = new com.google.gson.JsonArray();
            result.add(context.serialize(src.orNull()));
            return result;
        }
    }
}
