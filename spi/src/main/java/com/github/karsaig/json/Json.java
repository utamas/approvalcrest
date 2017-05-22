package com.github.karsaig.json;

import org.jetbrains.annotations.NotNull;

/**
 * Very limited Json functionality that we need.
 */
public interface Json {
    @NotNull String toJson(@NotNull Object src);

    @NotNull JsonElement toJsonTree(@NotNull Object actual);
}
