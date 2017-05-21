package com.github.karsaig.json;

public interface JsonObject extends JsonElement {
    JsonElement get(String field);

    void add(String s, JsonElement child);

    void remove(String lastSegmentOf);
}
