package com.github.karsaig.json.gson;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MapJsonSerializerIntegrationTest {
    @Test
    public void shouldCreateAndArrayOfEntriesWhenMapIsSerialized() {
        // GIVEN
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(Map.class, new MapJsonSerializer(gsonBuilder));
        Gson gson = gsonBuilder.create();

        // WHEN
        String json = gson.toJson(ImmutableMap.of("key", "value", "foo", "bar"));

        // THEN
        assertThat(json, is("[{\"foo\":\"bar\"},{\"key\":\"value\"}]"));
    }
}