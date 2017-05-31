package com.github.karsaig.json;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonDelegateJson implements Json {
    private final ObjectMapper delegate;

    public JacksonDelegateJson(ObjectMapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public @NotNull String toJson(@Nullable Object src) {
        try {
            return delegate.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public @NotNull JsonElement toJsonTree(@Nullable Object actual) {
        return null;
    }
}
