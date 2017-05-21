package com.google.gson;

import java.util.Iterator;

public interface JsonArray extends JsonElement {
    Iterator<JsonElement> iterator();

    void add(JsonElement element);
}
