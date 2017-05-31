package com.github.karsaig;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

public class MyBeanSerializerModifier extends BeanSerializerModifier {

    private final Set<Class<?>> typesToIgnore;

    public MyBeanSerializerModifier(Set<Class<?>> typesToIgnore) {
        this.typesToIgnore = typesToIgnore;
    }

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {

        for (int i = 0; i < beanProperties.size(); i++) {
            BeanPropertyWriter writer = beanProperties.get(i);

            Iterator<Class<?>> typeToIgnoreIterator = typesToIgnore.iterator();
            boolean found = false;
            while (typeToIgnoreIterator.hasNext() && !found) {
                Class<?> typeToIgnore = typeToIgnoreIterator.next();
                found = typeToIgnore.isAssignableFrom(writer.getPropertyType());
            }

            if (found) {
                beanProperties.set(i, new MyBeanPropertyWriter(writer));
            }
        }

        return super.changeProperties(config, beanDesc, beanProperties);
    }

}
