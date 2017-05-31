package com.github.karsaig;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;

public class ObjectWriterTest {
    @Test
    public void shouldWhen() throws JsonProcessingException {
        // GIVEN

        final Set<Class<?>> typesToIgnore = ImmutableSet.<Class<?>>of(B.class);

        ObjectMapper mapper = new ObjectMapper().registerModule(new TypeBasedFieldIgnoringModule(typesToIgnore));

        // WHEN
        String json = mapper.writeValueAsString(new A(new B("Tamas", "Utasi"), "baar"));

        // THEN
        assertThat(json, is("{\"fooo\":\"baar\"}"));
    }

    //    private static class GZSNYF extends Serializers.Base {
    //        private final Set<Class<?>> typesToIgnore;
    //
    //        public GZSNYF(Set<Class<?>> typesToIgnore) {
    //            this.typesToIgnore = typesToIgnore;
    //        }
    //
    //        @Override
    //        public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
    //
    //            Iterator<Class<?>> iterator = typesToIgnore.iterator();
    //            boolean found = false;
    //            while (iterator.hasNext() && !found) {
    //                Class<?> typeToIgnore = iterator.next();
    //                found = typeToIgnore.isAssignableFrom(type.getRawClass());
    //            }
    //
    //            if (found) {
    //                return new None() {
    //
    //                    @Override
    //                    public boolean isEmpty(Object value) {
    //                        return true;
    //                    }
    //
    //                    @Override public boolean isEmpty(SerializerProvider provider, Object value) {
    //                        return true;
    //                    }
    //
    //                    @Override
    //                    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
    //                        System.out.println("Ignores writing " + value);
    //                    }
    //                };
    //            }
    //
    //            //            if (type.getRawClass()) {
    //            //
    //            //            }
    //
    //            return super.findSerializer(config, type, beanDesc);
    //        }
    //    }

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

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("foo", foo).append("fooo", fooo).toString();
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

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("bar", bar).append("baar", baar).toString();
        }
    }
}
