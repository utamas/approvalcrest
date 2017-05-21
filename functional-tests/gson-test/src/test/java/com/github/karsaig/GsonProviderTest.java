package com.github.karsaig;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hamcrest.Matcher;
import org.junit.Test;

import com.github.karsaig.approvalcrest.matcher.GsonProvider;

import com.github.karsaig.json.Gson;

public class GsonProviderTest {
    @Test
    public void shouldWhen() {
        // GIVEN
        List<Class<?>> typesToIgnore = emptyList();
        List<Matcher<String>> fieldsToIgnore = Collections.emptyList();
        Set<Class<?>> circularReferenceTypes = Collections.emptySet();

        // WHEN
        Gson actualGson = GsonProvider.gson(typesToIgnore, fieldsToIgnore, circularReferenceTypes);

        // THEN
        assertNotNull(actualGson);
    }
}
