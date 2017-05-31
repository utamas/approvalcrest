package com.github.karsaig;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ObjectWriterTest {
    @Ignore
    @Test
    public void shouldWhen() throws JsonProcessingException {
        // GIVEN

        ObjectMapper mapper = new ObjectMapper().disable(FAIL_ON_UNKNOWN_PROPERTIES);
        ObjectWriter writer = mapper.writer().withoutAttribute("fooo");

        // WHEN
        String json = writer.writeValueAsString(new A(new B("41", "42"), "baaar"));

        // THEN
        assertThat(json, is(""));
    }

    private class A {
        private B foo;
        private String fooo;

        public A(B foo, String fooo) {
            this.foo = foo;
            this.fooo = fooo;
        }

        public B getFoo() {
            return foo;
        }

        public String getFooo() {
            return fooo;
        }
    }

    private class B {
        private String bar;
        private String baar;

        private B(String bar, String baar) {
            this.bar = bar;
            this.baar = baar;
        }

        public String getBar() {
            return bar;
        }

        public String getBaar() {
            return baar;
        }
    }
}
