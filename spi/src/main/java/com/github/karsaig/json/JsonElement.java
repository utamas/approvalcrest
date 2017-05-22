package com.github.karsaig.json;

import org.jetbrains.annotations.NotNull;

public interface JsonElement {
    boolean isJsonArray();

    @NotNull JsonArray getAsJsonArray();

    boolean isJsonNull();

    @NotNull JsonObject getAsJsonObject();

    boolean isJsonObject();

    boolean isJsonPrimitive();
}
