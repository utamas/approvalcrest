package com.google.gson.graph;

import com.google.gson.GsonBuilder;

public interface GraphAdapterBuilder {

    void addType(Class<?> circularReferenceType);

    void registerOn(GsonBuilder gsonBuilder);
}
