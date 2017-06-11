package com.github.karsaig.json.module;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;

public class SetSerializingModuleIntegrationTest {

    @Test
    public void shouldSerializeItemsSortedLexicographicalOrderWhenObjectIsASet() throws JsonProcessingException {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new SetSerializingModule());

        // WHEN
        String json = objectMapper.writeValueAsString(ImmutableSet.of("bbbb", "aaaa", "foo", "bar"));

        // THEN
        assertThat(json, is("[\"aaaa\",\"bar\",\"bbbb\",\"foo\"]"));
    }

}