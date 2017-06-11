package com.github.karsaig.json.gson;

import static com.google.common.collect.Sets.newTreeSet;

import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class SetJsonSerializer implements JsonSerializer<Set> {
    private final GsonBuilder gsonBuilder;

    public SetJsonSerializer(GsonBuilder gsonBuilder) {
        this.gsonBuilder = gsonBuilder;
    }

    @Override
    public com.google.gson.JsonElement serialize(Set set, Type type, JsonSerializationContext context) {
        Gson gson = gsonBuilder.create();
        Set<Object> orderedSet = orderSetByElementsJsonRepresentation(set, gson);
        return arrayOfObjectsOrderedByTheirJsonRepresentation(gson, orderedSet);
    }

    @SuppressWarnings("unchecked")
    private Set<Object> orderSetByElementsJsonRepresentation(Set set, final Gson gson) {
        Set<Object> objects = newTreeSet(new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return gson.toJson(o1).compareTo(gson.toJson(o2));
            }
        });
        objects.addAll(set);
        return objects;
    }

    private JsonArray arrayOfObjectsOrderedByTheirJsonRepresentation(Gson gson, Set<Object> objects) {
        JsonArray array = new JsonArray();
        for (Object object : objects) {
            array.add(gson.toJsonTree(object));
        }
        return array;
    }
}
