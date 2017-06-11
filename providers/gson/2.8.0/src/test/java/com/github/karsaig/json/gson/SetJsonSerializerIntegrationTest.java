package com.github.karsaig.json.gson;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SetJsonSerializerIntegrationTest {

    @Test
    public void shouldSerializeItemsSortedLexicographicalOrderWhenObjectIsASet() {
        // GIVEN
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(Set.class, new SetJsonSerializer(gsonBuilder));
        Gson gson = gsonBuilder.create();

        // WHEN
        String json = gson.toJson(ImmutableSet.of("bbbb", "aaaa", "foo", "bar"));

        // THEN
        assertThat(json, is("[\"aaaa\",\"bar\",\"bbbb\",\"foo\"]"));
    }
}