package com.github.karsaig.json;

import org.jetbrains.annotations.NotNull;

import com.google.gson.Gson;

public class GsonDelegateJson implements Json {
    private final Gson gson;

    public GsonDelegateJson(Gson gson) {
        this.gson = gson;
    }

    @Override
    public @NotNull String toJson(@NotNull Object src) {
        return gson.toJson(src);
    }

    @Override
    public @NotNull JsonElement toJsonTree(@NotNull Object actual) {
        return new GsonDelegateJsonElement(gson.toJsonTree(actual));
    }
}
