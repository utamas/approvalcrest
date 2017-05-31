package com.github.karsaig.json.ignore;

import com.google.common.collect.ImmutableSet;

public class TypeBasedFieldIgnoringModule extends AbstractFieldIgnoringModule {
    private static final String MODULE_NAME = "TypeBasedFieldIgnoringModule";

    public TypeBasedFieldIgnoringModule(Class<?>... typesToIgnore) {
        super(new TypeBasedFieldIgnoringBeanSerializerModifier(ImmutableSet.copyOf(typesToIgnore)), MODULE_NAME);
    }
}
