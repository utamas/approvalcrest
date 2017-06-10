package com.github.karsaig.json.gson;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import com.github.karsaig.approvalcrest.FieldsIgnorer;

import com.google.gson.FieldNamingStrategy;

public class SetAndMapMarkingFieldNamingStrategy implements FieldNamingStrategy {
    @Override
    public String translateName(Field f) {
        if (Set.class.isAssignableFrom(f.getType()) || Map.class.isAssignableFrom(f.getType())) {
            return FieldsIgnorer.MARKER + f.getName();
        }
        return f.getName();
    }
}
