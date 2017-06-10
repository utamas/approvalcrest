package com.github.karsaig.json.module;

import static com.github.karsaig.json.module.VersionProvider.VERSION;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class SetAndMapMarkingModule extends Module {
    @Override
    public String getModuleName() {
        return SetAndMapMarkingModule.class.getSimpleName();
    }

    @Override
    public Version version() {
        return VERSION;
    }

    @Override
    public void setupModule(SetupContext context) {
        context.setNamingStrategy(new SetAndMapMarkingPropertyNamingStrategy());
    }

}
