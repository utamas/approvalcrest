package com.github.karsaig.json.module;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;

public class MapSerializingModuleIntegrationTest {
    @Test
    public void shouldCreateAndArrayOfEntriesWhenMapIsSerialized() throws JsonProcessingException {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new MapSerializingModule());
        Map<String, String> map = ImmutableMap.of("key", "value", "foo", "bar");

        // WHEN
        String json = objectMapper.writeValueAsString(map);

        // THEN
        assertThat(json, is("[{\"foo\":\"bar\"},{\"key\":\"value\"}]"));
    }
}