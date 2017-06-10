package com.github.karsaig.json.module;

import java.util.Map;
import java.util.Set;

import com.github.karsaig.approvalcrest.FieldsIgnorer;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

public class SetAndMapMarkingPropertyNamingStrategy extends PropertyNamingStrategy {
//    @Override
//    public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
//        return super.nameForField(config, field, defaultName);
//    }
//    public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
//        return super.nameForField(config, field, defaultName);
//    }

    @Override
    public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
        Class<?> propertyType = method.getRawReturnType();
        if (Set.class.isAssignableFrom(propertyType) || Map.class.isAssignableFrom(propertyType)) {
            return super.nameForGetterMethod(config, method, FieldsIgnorer.MARKER + defaultName);
        }
        return super.nameForGetterMethod(config, method, defaultName);
    }
}
