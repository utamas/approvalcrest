package com.google.gson;

import java.lang.reflect.Type;

public interface JsonSerializer<T> {
    JsonElement serialize(T set, Type type, JsonSerializationContext context);
}
