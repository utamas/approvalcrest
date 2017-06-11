package com.github.karsaig.json.module;

import static org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.MapType;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Ordering;

public class MapSerializingModule extends Module {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperHolder.getInstance();

    @Override
    public String getModuleName() {
        return MapSerializingModule.class.getSimpleName();
    }

    @Override
    public Version version() {
        return VersionProvider.VERSION;
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new MapSerializers());
    }

    public static class MapSerializers extends Serializers.Base {
        @Override
        public JsonSerializer<?> findMapSerializer(SerializationConfig config, MapType type, BeanDescription beanDesc, JsonSerializer<Object> keySerializer,
                TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
            Class<?> rawType = type.getRawClass();

            if (Map.class.isAssignableFrom(rawType)) {
                return new MapSerializer();
            }

            return super.findMapSerializer(config, type, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
        }

        @Override
        public JsonSerializer<?> findMapLikeSerializer(SerializationConfig config, MapLikeType type, BeanDescription beanDesc, JsonSerializer<Object> keySerializer,
                TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer) {
            Class<?> rawType = type.getRawClass();

            if (Map.class.isAssignableFrom(rawType)) {
                return new MapSerializer();
            }

            return super.findMapLikeSerializer(config, type, beanDesc, keySerializer, elementTypeSerializer, elementValueSerializer);
        }
    }

    public static class MapSerializer extends JsonSerializer<Map> {
        @Override
        @SuppressWarnings("unchecked")
        public void serialize(Map map, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            ArrayListMultimap<String, Object> objects = ArrayListMultimap.create();
            for (Entry<Object, Object> mapEntry : (Set<Entry<Object, Object>>) map.entrySet()) {
                Object mapKey = mapEntry.getKey();
                String objectAsJson = toJson(mapKey).concat(toJson(mapEntry.getValue()));
                objects.put(objectAsJson, mapKey);
            }

            ImmutableList<String> sortedMapKeySet = Ordering.natural().immutableSortedCopy(objects.keySet());
            ArrayNode array = OBJECT_MAPPER.createArrayNode();

            if (allKeysArePrimitiveOrStringOrEnum(sortedMapKeySet, objects)) {
                for (String jsonRepresentation : sortedMapKeySet) {
                    List<Object> objectsInTheSet = objects.get(jsonRepresentation);
                    for (Object objectInTheSet : objectsInTheSet) {
                        ObjectNode jsonObject = OBJECT_MAPPER.createObjectNode();
                        jsonObject.set(String.valueOf(objectInTheSet), toJsonTree(map.get(objectInTheSet)));
                        array.add(jsonObject);
                    }
                }
            } else {
                for (String jsonRepresentation : sortedMapKeySet) {
                    ArrayNode keyValueArray = OBJECT_MAPPER.createArrayNode();
                    List<Object> objectsInTheSet = objects.get(jsonRepresentation);
                    for (Object objectInTheSet : objectsInTheSet) {
                        keyValueArray.add(toJsonTree(objectInTheSet));
                        keyValueArray.add(toJsonTree(map.get(objectInTheSet)));
                        array.add(keyValueArray);
                    }
                }
            }

            gen.writeTree(array);
        }

        private boolean allKeysArePrimitiveOrStringOrEnum(List<String> sortedMapKeySet, ListMultimap<String, Object> objects) {
            for (String jsonRepresentation : sortedMapKeySet) {
                List<Object> mapKeys = objects.get(jsonRepresentation);
                for (Object object : mapKeys) {
                    Class<?> type = object.getClass();

                    if (!(isPrimitiveOrWrapper(type) || type == String.class || type.isEnum())) {
                        return false;
                    }
                }
            }
            return true;
        }

        private String toJson(Object key) throws JsonProcessingException {
            return OBJECT_MAPPER.writeValueAsString(key);
        }

        private JsonNode toJsonTree(Object objectInTheSet) {
            return OBJECT_MAPPER.valueToTree(objectInTheSet);
        }
    }
}
