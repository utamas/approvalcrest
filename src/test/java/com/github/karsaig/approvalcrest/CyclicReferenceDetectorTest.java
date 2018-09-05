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

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import com.github.karsaig.approvalcrest.model.cyclic.*;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.github.karsaig.approvalcrest.CyclicReferenceDetector.getClassesWithCircularReferences;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

/**
 * Tests which verify the {@link com.github.karsaig.approvalcrest.CyclicReferenceDetector} returns the classes which participate in a cyclic reference.
 */
public class CyclicReferenceDetectorTest {
	
	private static final Set<String> EMPTY_PATHS_TO_IGNORE = Collections.emptySet();
	private static final List<Class<?>> EMPTY_TYPES_TO_IGNORE = Collections.emptyList();
	private static final List<Matcher<String>> EMPTY_PATTERNS_TO_IGNORE = Collections.emptyList();
	
    @Test
    public void shouldReturnAnEmptySetWhenTheObjectIsNull() {
        Set<Class<?>> returnedObjects = getClassesWithCircularReferences(null,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedObjects.isEmpty(), is(true));
    }

    @Test
    public void shouldReturnAnEmptyListWhenThereIsNoCircularReference() {
        One one = new One();
        one.setGenericObject(new Two());
        Set<Class<?>> returnedObjects = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);
        
        assertThat(returnedObjects.isEmpty(), is(true));
    }

    @Test
    public void shouldReturnTheClassWithCyclicReferenceWhenTheObjectHasAFieldWithCyclicReference() {
        One one = new One();
        Two two = new Two();
        two.setGenericObject(one);
        one.setGenericObject(two);

        Set<Class<?>> returnedObjects = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedObjects, hasItem(One.class));
    }

    @Test
    public void shouldReturnEmptyListWhenTheObjectHasAFieldWithCyclicReferenceButTheTypeIsIgnored() {
        One one = new One();
        Two two = new Two();
        two.setGenericObject(one);
        one.setGenericObject(two);
        List<Class<?>> typesToIgnore = new ArrayList<Class<?>>();
        typesToIgnore.add(Two.class);

        Set<Class<?>> returnedObjects = getClassesWithCircularReferences(one,typesToIgnore,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedObjects.isEmpty(), is(true));
    }
    
    @Test
    public void shouldReturnEmptyListWhenTheObjectHasAFieldWithCyclicReferenceButTheFieldNameIsIgnored() {
        One one = new One();
        Two two = new Two();
        two.setGenericObject(one);
        one.setGenericObject(two);
        List<Matcher<String>> fieldNamesToIgnore = new ArrayList<Matcher<String>>();
        fieldNamesToIgnore.add(Matchers.is("twoObject"));

        Set<Class<?>> returnedObjects = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,fieldNamesToIgnore,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedObjects.isEmpty(), is(true));
    }
    
    @Test
    public void shouldReturnTheClassWithCyclicReferenceFieldWhenTheCyclicReferenceIsMoreThanTwoNodesAway() {
        One one = new One();
        Two two = new Two();
        Three three = new Three();

        one.setGenericObject(two);
        two.setGenericObject(three);
        three.setGenericObject(one);

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses, hasItem(One.class));
    }

    @Test
    public void shouldReturnEmptyListWhenTheCyclicReferenceIsMoreThanTwoNodesAwayButTheTypeIsIgnored() {
        One one = new One();
        Two two = new Two();
        Three three = new Three();

        one.setGenericObject(two);
        two.setGenericObject(three);
        three.setGenericObject(one);
        List<Class<?>> typesToIgnore = new ArrayList<Class<?>>();
        typesToIgnore.add(Three.class);

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,typesToIgnore,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses.isEmpty(), is(true));
    }
    
    @Test
    public void shouldReturnEmptyListWhenTheCyclicReferenceIsMoreThanTwoNodesAwayButTheFieldNameIsIgnored() {
        One one = new One();
        Two two = new Two();
        Three three = new Three();

        one.setGenericObject(two);
        two.setGenericObject(three);
        three.setGenericObject(one);
        List<Matcher<String>> fieldNamesToIgnore = new ArrayList<Matcher<String>>();
        fieldNamesToIgnore.add(Matchers.is("threeObject"));

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,fieldNamesToIgnore,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses.isEmpty(), is(true));
    }
    
    @Test
    public void shouldReturnAnEmptySetWhenTheObjectHasANullField() {
        One one = new One();
        one.setGenericObject(null);

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses.isEmpty(), is(true));
    }

    @Test
    public void shouldReturnAnEmptySetWhenTheObjectHasANullFieldAndTheTypeIsIgnored() {
        One one = new One();
        one.setGenericObject(null);
        List<Class<?>> typesToIgnore = new ArrayList<Class<?>>();
        typesToIgnore.add(Object.class);
        
        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,typesToIgnore,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses.isEmpty(), is(true));
    }
    
    @Test
    public void shouldReturnAnEmptySetWhenTheObjectHasANullFieldAndTheFieldNameIsIgnored() {
        One one = new One();
        one.setGenericObject(null);
        List<Matcher<String>> fieldNamesToIgnore = new ArrayList<Matcher<String>>();
        fieldNamesToIgnore.add(Matchers.is("oneObject"));
        
        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,fieldNamesToIgnore,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses.isEmpty(), is(true));
    }
    
    @Test
    public void shouldReturnAnEmptySetWhenTheObjectHasAPrimitiveOrWrapperField() {
        One one = new One();
        one.setGenericObject(5);

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses.isEmpty(), is(true));
    }

    @Test
    public void shouldReturnAnEmptySetWhenTheObjectHasAStringField() {
        One one = new One();
        one.setGenericObject("irrelevant string");

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses.isEmpty(), is(true));
    }

    @Test
    public void shouldReturnAnEmptySetWhenTheObjectHasAnEnumField() {
        One one = new One();
        one.setGenericObject(Five.FIVE);

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses.isEmpty(), is(true));
    }

    @Test
    public void shouldReturnTheClassWithCircularReferenceFieldWhenTheFieldIsAListAndContainsAnObjectThatCausesCircularReference() {
        One one = new One();
        Two two = new Two();
        Three three = new Three();

        two.setGenericObject(three);
        three.setGenericObject(one);
        one.setGenericObject(Arrays.asList(two));

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses, hasItem(One.class));
    }

    @Test
    public void shouldReturnEmptySetWhenTheFieldIsAListAndContainsAnObjectThatCausesCircularReferenceButTheFieldNameIsIgnored() {
        One one = new One();
        Two two = new Two();
        Three three = new Three();

        two.setGenericObject(three);
        three.setGenericObject(one);
        one.setGenericObject(Arrays.asList(two));
        List<Matcher<String>> fieldNamesToIgnore = new ArrayList<Matcher<String>>();
        fieldNamesToIgnore.add(Matchers.is("twoObject"));
        
        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,fieldNamesToIgnore,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses.isEmpty(), is(true));
    }
    
    @SuppressWarnings("unchecked")
	@Test
    public void shouldReturnClassesWithCircularReferenceWhenAnObjectHasAListFieldWithMoreThanOneObjectsThatHasCircularReference() {
        One one = new One();
        Two two = new Two();
        Two two2 = new Two();
        Three three = new Three();
        three.setGenericObject(two2);
        two2.setGenericObject(three);

        two.setGenericObject(one);
        one.setGenericObject(Arrays.asList(two, three));

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses, hasItems(One.class, Three.class));
    }

    @Test
    public void shouldReturnTheClassWithCircularReferenceWhenTheObjectHasAFieldThatReferencesItself() {
        One one = new One();
        one.setGenericObject(one);

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses, hasItem(One.class));
    }

    @Test
    public void shouldReturnClassWithCircularReferenceWhenTheObjectHasAMapFieldWithAnObjectThatCausesCircularaReference() {
        One one = new One();
        Two two = new Two();

        two.setGenericObject(one);
        Map<Object, Two> twoMap = new HashMap<Object, Two>(1);

        twoMap.put(1, two);
        one.setGenericObject(twoMap);

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses, hasItem(One.class));
    }

    @Test
    public void shouldReturnAClassWithCircularReferenceWhenTheObjectHasAFieldFromTheSuperClassThatHasCircularReference() {
        Four four = new Four();
        One one = new One();
        one.setGenericObject(four);
        four.setGenericObject(one);

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses, hasItem(One.class));
    }

    @Test
    public void shouldReturnEmptySetWhenTheObjectHasAFieldFromTheSuperClassThatHasCircularReferenceButTheFieldNameIsIgnored() {
        Four four = new Four();
        One one = new One();
        one.setGenericObject(four);
        four.setGenericObject(one);
        List<Matcher<String>> fieldNamesToIgnore = new ArrayList<Matcher<String>>();
        fieldNamesToIgnore.add(Matchers.is("threeObject"));

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,fieldNamesToIgnore,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses.isEmpty(), is(true));
    }
    
    @Test
    public void shouldReturnAClassWithCircularReferenceWhenTheObjectHasAMapFieldThatHasCircularReferenceOnTheKeySey() {
        One one = new One();
        Two two = new Two();

        two.setGenericObject(one);
        Map<Two, Object> twoMap = new HashMap<Two, Object>(1);

        twoMap.put(two, 1);
        one.setGenericObject(twoMap);

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(one,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses, hasItem(One.class));
    }

    @Test
    public void shouldNotReturnAClassWhenAnObjectIsInSeparatePathAndCausesNoCircularReference() {
        One one = new One();
        Two two = new Two();

        Four four = new Four();
        four.setGenericObject(one);
        four.setSubClassField(two);
        one.setGenericObject(two);

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(four,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses.isEmpty(), is(true));
    }

	@Test
	@SuppressWarnings("unchecked")
    public void shouldReturnClassesWithCircularReferenceEvenWhenTheTwoClassAreInSeparatePath() {
        Four four = new Four();
        Four four1 = new Four();

        Two two = new Two();
        four.setGenericObject(two);
        two.setGenericObject(four);

        four.setSubClassField(four1);

        Two two1 = new Two();
        Three three = new Three();

        four1.setSubClassField(two1);
        two1.setGenericObject(three);
        three.setGenericObject(two1);

        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(four,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses, hasItems(Four.class, Two.class));
    }

    @Test
    public void shouldNotAddStaticFieldsToTheSetOfCircularReferences() {
        Set<Class<?>> returnedClasses = getClassesWithCircularReferences(Element.ONE,EMPTY_TYPES_TO_IGNORE,EMPTY_PATTERNS_TO_IGNORE,EMPTY_PATHS_TO_IGNORE);

        assertThat(returnedClasses, is(empty()));
    }
}