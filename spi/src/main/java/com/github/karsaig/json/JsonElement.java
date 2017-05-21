package com.github.karsaig.json;

public interface JsonElement {
    boolean isJsonArray();

    JsonArray getAsJsonArray();

    boolean isJsonNull();

    JsonObject getAsJsonObject();

    boolean isJsonObject();

    boolean isJsonPrimitive();
}
