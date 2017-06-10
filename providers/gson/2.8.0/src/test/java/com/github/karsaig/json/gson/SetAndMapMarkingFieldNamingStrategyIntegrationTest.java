package com.github.karsaig.json.gson;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SetAndMapMarkingFieldNamingStrategyIntegrationTest {
    @Test
    public void shouldPrefixPropertyNameWhenItsTypeIsMap() {
        // GIVEN
        Gson gson = new GsonBuilder().setFieldNamingStrategy(new SetAndMapMarkingFieldNamingStrategy()).create();

        // WHEN
        String json = gson.toJson(new MapContainer(ImmutableMap.of("key", "value")));

        // THEN
        assertThat(json, is("{\"!_TO_BE_SORTED_!map\":{\"key\":\"value\"}}"));
    }

    private class MapContainer {
        private Map<String, String> map;

        public MapContainer(Map<String, String> map) {
            this.map = map;
        }

        public Map<String, String> getMap() {
            return map;
        }
    }
}