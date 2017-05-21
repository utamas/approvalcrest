package com.github.karsaig.json;

import java.util.Iterator;

public interface JsonArray extends JsonElement {
    Iterator<JsonElement> iterator();

    void add(JsonElement element);
}
