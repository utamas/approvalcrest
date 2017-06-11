package com.github.karsaig.json.module;

import static com.google.common.collect.Sets.newTreeSet;

import java.io.IOException;
import java.util.Comparator;
import java.util.Set;

import com.github.karsaig.json.ObjectMapperHolder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.CollectionType;

public class SetSerializingModule extends Module {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperHolder.getInstance();

    @Override
    public String getModuleName() {
        return SetSerializingModule.class.getSimpleName();
    }

    @Override
    public Version version() {
        return VersionProvider.VERSION;
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new SetSerializers());
    }

    public static class SetSerializers extends Serializers.Base {
        @Override
        public JsonSerializer<?> findCollectionSerializer(
                SerializationConfig config,
                CollectionType type,
                BeanDescription beanDesc,
                TypeSerializer elementTypeSerializer,
                JsonSerializer<Object> elementValueSerializer) {
            Class<?> rawType = type.getRawClass();

            if (Set.class.isAssignableFrom(rawType)) {
                return new SetSerializer();
            }

            return super.findCollectionSerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
        }

        @Override
        public JsonSerializer<?> findCollectionLikeSerializer(
                SerializationConfig config,
                CollectionLikeType type,
                BeanDescription beanDesc,
                TypeSerializer elementTypeSerializer,
                JsonSerializer<Object> elementValueSerializer) {
            return super.findCollectionLikeSerializer(config, type, beanDesc, elementTypeSerializer, elementValueSerializer);
        }
    }

    public static class SetSerializer extends JsonSerializer<Set> {
        @Override
        public void serialize(Set set, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            Set<Object> objects = newTreeSet(new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    return toJson(o1).compareTo(toJson(o2));
                }

                private String toJson(Object o1) {
                    try {
                        return OBJECT_MAPPER.writeValueAsString(o1);
                    } catch (JsonProcessingException e) {
                        throw new IllegalStateException(e);
                    }
                }
            });
            objects.addAll(set);

            ArrayNode array = OBJECT_MAPPER.createArrayNode();
            for (Object object : objects) {
                array.add(toJsonTree(object));
            }

            gen.writeTree(array);
        }

        private JsonNode toJsonTree(Object object) {
            return OBJECT_MAPPER.valueToTree(object);
        }
    }
}
