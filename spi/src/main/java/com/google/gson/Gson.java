package com.google.gson;

/**
 * Very limited Json functionality that we need.
 */
public interface Gson {
    String toJson(Object src);

    JsonElement toJsonTree(Object actual);
}
