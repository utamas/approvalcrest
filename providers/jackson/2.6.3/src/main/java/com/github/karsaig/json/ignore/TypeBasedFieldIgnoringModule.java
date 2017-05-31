package com.github.karsaig.json.ignore;

import java.util.Set;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.Module;

public class TypeBasedFieldIgnoringModule extends Module {
    private final Set<Class<?>> typesToIgnore;

    public TypeBasedFieldIgnoringModule(Set<Class<?>> typesToIgnore) {
        this.typesToIgnore = typesToIgnore;
    }

    @Override
    public String getModuleName() {
        return "ApprovalcrestJson";
    }

    @Override
    public Version version() {
        return VersionUtil.parseVersion("0.1-SNAPSHOT", "com.github.karsaig.approvalcrest.providers.jackson", "2.6.3");
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addBeanSerializerModifier(new TypeBasedBeanSerializerModifier(typesToIgnore));
    }
}
