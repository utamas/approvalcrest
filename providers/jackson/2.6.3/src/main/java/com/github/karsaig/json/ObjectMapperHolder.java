package com.github.karsaig.json;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ObjectMapperHolder {
    private static ObjectMapper createInitializedInstance() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(ALLOW_COMMENTS);
        mapper.enable(ALLOW_UNQUOTED_FIELD_NAMES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return mapper;
    }

    private static final ObjectMapper INSTANCE = createInitializedInstance();

    public static ObjectMapper getInstance() {
        return INSTANCE;
    }
}
