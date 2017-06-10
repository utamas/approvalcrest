package com.github.karsaig.json.module;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

public class SetAndMapMarkingModuleIntegrationTest {

    @Test
    public void shouldPrefixPropertyNameWhenItsTypeIsMap() throws JsonProcessingException {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new SetAndMapMarkingModule());

        // WHEN
        String json = objectMapper.writeValueAsString(new MapContainer(ImmutableMap.of("key", "value")));

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