/*
 * Copyright 2013 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.github.karsaig.approvalcrest;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.nio.file.Watchable;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hamcrest.Matcher;

import static java.lang.reflect.Modifier.isStatic;
import static java.util.Collections.newSetFromMap;
import static org.apache.commons.lang3.ClassUtils.isPrimitiveOrWrapper;

/**
 * Detects classes with fields that have circular reference and returns a set of those classes.
 */
public class CyclicReferenceDetector {

    private Set<Object> nodesInPaths = newSetFromMap(new IdentityHashMap<Object, Boolean>());
    private Set<Object> objectsWithCircularReferences = newSetFromMap(new IdentityHashMap<Object, Boolean>());
    
    /**
     * Returns a set of classes that have circular reference.
     * 
     * @param object the object to check if it has circular reference fields
     * @return a {@link Set} of {@link Class}es.
     */
    public static Set<Class<?>> getClassesWithCircularReferences(Object object,List<Class<?>> typesToIgnore, List<Matcher<String>> patternsToIgnore,Set<String> pathsToIgnore) {
        CyclicReferenceDetector cyclicReferenceDetector = new CyclicReferenceDetector();

        if (object != null) {
            cyclicReferenceDetector.detectCircularReferenceOnObject(object,typesToIgnore,patternsToIgnore,pathsToIgnore);
        }

        return getClasses(cyclicReferenceDetector.objectsWithCircularReferences);
    }

    /**
     * Returns a set of classes from a given set of objects.
     * @param objects a set of objects get classes to return from
     * @return a set of classes inside a given set.
     */
    private static Set<Class<?>> getClasses(Set<Object> objects) {
        Set<Class<?>> circularReferenceTypes = new HashSet<Class<?>>();
        for (Object objectInPath : objects) {
            circularReferenceTypes.add(objectInPath.getClass());
        }
        return circularReferenceTypes;
    }

    /**
     * Detects classes that have circular reference.
     * 
     * @param object the object to check if it has circular reference fields
     * @param clazz the class being used (necessary if we also checking super class as getDeclaredFields only returns
     *              fields of a given class, but not its super class)
     */
    private void detectCircularReferenceOnFields(Object object, Class<?> clazz, List<Class<?>> typesToIgnore, List<Matcher<String>> patternsToIgnore,Set<String> pathsToIgnore) {
        if (objectsWithCircularReferences.contains(object)) {
            return;
        }

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            if (!isStatic(field.getModifiers())) {
                try {
                	if(!isFieldnameIgnored(field, patternsToIgnore)){
                		Object fieldValue = field.get(object);
                		if (fieldValue != null) {
                			detectCircularReferenceOnObject(fieldValue,typesToIgnore,patternsToIgnore,pathsToIgnore);
                		}
                	}
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        detectCircularReferencesFromTheSuperClass(object, clazz,typesToIgnore,patternsToIgnore,pathsToIgnore);
    }

    private boolean isFieldnameIgnored(Field field, List<Matcher<String>> patternsToIgnore){
    	for(Matcher<String> matcher : patternsToIgnore){
    		if(matcher.matches(field.getName())){
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Detects circular reference on a given field.
     * If a field is a {@link Iterable} or a {@link Map}, loops through the values and
     * detects cycles.
     *
     * @param object the object to detect circular reference on
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	private void detectCircularReferenceOnObject(Object object,List<Class<?>> typesToIgnore, List<Matcher<String>> patternsToIgnore,Set<String> pathsToIgnore) {
    	if(typesToIgnore.contains(object.getClass())){
    		return;
    	}
    	
        boolean isValid = validateAnObject(object);
        boolean isInPath = nodesInPaths.contains(object);

        if (!isValid && isInPath) {
            return;
        }

        if (isInPath) {
            objectsWithCircularReferences.add(object);
            return;
        }

        if (object instanceof Iterable && !treatAsNotIterable(object)) {
            nodesInPaths.add(object);
            detectCircularReferenceFromObjectsContainedInAnIterable((Iterable) object,typesToIgnore,patternsToIgnore,pathsToIgnore);
        } else if (object instanceof Map) {
            nodesInPaths.add(object);
            detectCircularReferencesFromObjectsInAMap((Map) object,typesToIgnore,patternsToIgnore,pathsToIgnore);
        }

        if (isValid) {
            nodesInPaths.add(object);
            detectCircularReferenceOnFields(object, object.getClass(), typesToIgnore,patternsToIgnore,pathsToIgnore);
            nodesInPaths.remove(object);
        }
    }

    private boolean treatAsNotIterable(Object object){
    	return Closeable.class.isInstance(object);
    }
    
    /**
     * For objects with super classes, check for objects subject to circular reference in super class.
     * 
     * @param object the object to check if it has circular reference
     * @param clazz Used to prevent stackOverFlow exception
     */
    private void detectCircularReferencesFromTheSuperClass(Object object, Class<?> clazz, List<Class<?>> typesToIgnore, List<Matcher<String>> patternsToIgnore,Set<String> pathsToIgnore) {
        Class<?> superclass = clazz.getSuperclass();

        if (superclass != null && validateAnObject(object)) {
            detectCircularReferenceOnFields(object, superclass, typesToIgnore,patternsToIgnore,pathsToIgnore);
        }
    }

    /**
     * Detects circular references on {@link Map}s, i.e HashMap, TreeMap, etc.
     * 
     * @param map the {@link Map} with objects to checks for cyclic references on
     */
    private void detectCircularReferencesFromObjectsInAMap(Map<Object, Object> map,List<Class<?>> typesToIgnore, List<Matcher<String>> patternsToIgnore,Set<String> pathsToIgnore) {
        detectCircularReferenceFromObjectsContainedInAnIterable(map.values(), typesToIgnore,patternsToIgnore,pathsToIgnore);
        detectCircularReferenceFromObjectsContainedInAnIterable(map.keySet(), typesToIgnore,patternsToIgnore,pathsToIgnore);
    }

    /**
     * Detects circular references on {@link Iterable}s, i.e {@link Collection}s.
     * 
     * @param iterable the object to iterate through.
     */
    private void detectCircularReferenceFromObjectsContainedInAnIterable(Iterable<Object> iterable,List<Class<?>> typesToIgnore, List<Matcher<String>> patternsToIgnore,Set<String> pathsToIgnore) {
        for (Object elementInCollection : iterable) {
            if (elementInCollection != null) {
                detectCircularReferenceOnObject(elementInCollection,typesToIgnore,patternsToIgnore,pathsToIgnore);
            }
        }
    }

    /**
     * Checks to see if the given object is primitive or wrapper class, {@link String}, {@link Class}, instance of {@link Iterable},
     * instance of {@link Map} or instance of {@link Enum}.
     *
     * @param object The object to validate
     * @return true if the object is not primitive/wrapper class and not an instance of
     * 			{@link String}, {@link Iterable}, {@link Map} or {@link Enum})
     */
    private boolean validateAnObject(Object object) {
        return !isPrimitiveOrWrapper(object.getClass())
                && object.getClass() != String.class
                && object.getClass() != Class.class
                && !(object instanceof Iterable)
                && !(object instanceof Map)
                && !(object instanceof Enum);
    }
}