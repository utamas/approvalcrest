package com.github.karsaig.json.graph;

import com.github.karsaig.json.JsonBuilder;

public interface GraphAdapterBuilder {

    GraphAdapterBuilder addType(Class<?> circularReferenceType);

    GraphAdapterBuilder registerOn(JsonBuilder jsonBuilder);
}
