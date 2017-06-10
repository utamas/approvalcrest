package com.github.karsaig.json.module;

import static com.fasterxml.jackson.core.util.VersionUtil.parseVersion;

import com.fasterxml.jackson.core.Version;

public class VersionProvider {
    private static final String APPROVAL_CREST_VERSION = "0.1";
    private static final String GROUP_ID = "com.github.karsaig.approvalcrest.providers.jackson";
    private static final String JACKSON_VERSION_AS_ARTIFACT_ID = "2.6.3";

    public static final Version VERSION = parseVersion(APPROVAL_CREST_VERSION, GROUP_ID, JACKSON_VERSION_AS_ARTIFACT_ID);
}
