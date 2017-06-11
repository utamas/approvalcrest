package com.github.karsaig.json.gson;

import static org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MapJsonSerializer implements JsonSerializer<Map> {
    private final GsonBuilder gsonBuilder;

    public MapJsonSerializer(GsonBuilder gsonBuilder) {
        this.gsonBuilder = gsonBuilder;
    }

    @Override
    public JsonElement serialize(Map map, Type type, JsonSerializationContext context) {
        Gson gson = gsonBuilder.create();
        ArrayListMultimap<String, Object> objects = mapObjectsByTheirJsonRepresentation(map, gson);
        return arrayOfObjectsOrderedByTheirJsonRepresentation(gson, objects, map);
    }

    @SuppressWarnings("unchecked")
    private ArrayListMultimap<String, Object> mapObjectsByTheirJsonRepresentation(Map map, Gson gson) {
        ArrayListMultimap<String, Object> objects = ArrayListMultimap.create();
        for (Entry<Object, Object> mapEntry : (Set<Entry<Object, Object>>) map.entrySet()) {
            objects.put(gson.toJson(mapEntry.getKey()).concat(gson.toJson(mapEntry.getValue())), mapEntry.getKey());
        }
        return objects;
    }

    private JsonElement arrayOfObjectsOrderedByTheirJsonRepresentation(Gson gson, ListMultimap<String, Object> objects, Map map) {
        ImmutableList<String> sortedMapKeySet = Ordering.natural().immutableSortedCopy(objects.keySet());

        JsonArray array = new JsonArray();
        if (allKeysArePrimitiveOrStringOrEnum(sortedMapKeySet, objects)) {
            for (String jsonRepresentation : sortedMapKeySet) {
                List<Object> objectsInTheSet = objects.get(jsonRepresentation);
                for (Object objectInTheSet : objectsInTheSet) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add(String.valueOf(objectInTheSet), gson.toJsonTree(map.get(objectInTheSet)));
                    array.add(jsonObject);
                }
            }
        } else {
            for (String jsonRepresentation : sortedMapKeySet) {
                JsonArray keyValueArray = new JsonArray();
                List<Object> objectsInTheSet = objects.get(jsonRepresentation);
                for (Object objectInTheSet : objectsInTheSet) {
                    keyValueArray.add(gson.toJsonTree(objectInTheSet));
                    keyValueArray.add(gson.toJsonTree(map.get(objectInTheSet)));
                    array.add(keyValueArray);
                }
            }
        }

        return array;
    }

    private boolean allKeysArePrimitiveOrStringOrEnum(ImmutableList<String> sortedMapKeySet, ListMultimap<String, Object> objects) {
        for (String jsonRepresentation : sortedMapKeySet) {
            List<Object> mapKeys = objects.get(jsonRepresentation);
            for (Object object : mapKeys) {
                if (!(isPrimitiveOrWrapper(object.getClass()) || object.getClass() == String.class || object.getClass().isEnum())) {
                    return false;
                }
            }
        }
        return true;
    }
}