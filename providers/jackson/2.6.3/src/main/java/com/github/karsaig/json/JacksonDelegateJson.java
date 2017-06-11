package com.github.karsaig.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;

public class JacksonDelegateJson implements Json {
    private final ObjectMapper delegate;

    public JacksonDelegateJson(ObjectMapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public @NotNull String toJson(@Nullable Object src) {
        try {
            return delegate.writeValueAsString(unwrapIfPossible(src));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public @NotNull JsonElement toJsonTree(@Nullable Object src) {
        Object value = unwrapIfPossible(src);
        return new JacksonDelegateJsonElement(value == null ? NullNode.getInstance() : delegate.valueToTree(value), delegate);
    }
    // ========== Helper methods below. ==========

    private @Nullable Object unwrapIfPossible(@Nullable Object src) {
        return JacksonDelegateJsonElement.class.isInstance(src) ? JacksonDelegateJsonElement.class.cast(src).delegate : src;
    }
}
