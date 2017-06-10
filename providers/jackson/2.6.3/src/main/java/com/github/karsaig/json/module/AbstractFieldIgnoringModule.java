package com.github.karsaig.json.module;

import static com.github.karsaig.json.module.VersionProvider.VERSION;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

public abstract class AbstractFieldIgnoringModule extends Module {
    private final BeanSerializerModifier beanSerializerModifier;
    private final String moduleName;

    public AbstractFieldIgnoringModule(BeanSerializerModifier beanSerializerModifier, String moduleName) {
        this.beanSerializerModifier = beanSerializerModifier;
        this.moduleName = moduleName;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public Version version() {
        return VERSION;
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addBeanSerializerModifier(beanSerializerModifier);
    }
}
