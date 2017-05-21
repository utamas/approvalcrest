package com.github.karsaig.json;

/**
 * Very limited Json functionality that we need.
 */
public interface Gson {
    String toJson(Object src);

    JsonElement toJsonTree(Object actual);
}
