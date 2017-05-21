package com.github.karsaig.json.graph;

import com.github.karsaig.json.GsonBuilder;

public interface GraphAdapterBuilder {

    void addType(Class<?> circularReferenceType);

    void registerOn(GsonBuilder gsonBuilder);
}
