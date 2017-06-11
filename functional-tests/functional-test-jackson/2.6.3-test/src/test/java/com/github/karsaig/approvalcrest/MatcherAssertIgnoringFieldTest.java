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

import static com.github.karsaig.approvalcrest.MatcherAssert.assertThat;
import static com.github.karsaig.approvalcrest.matcher.Matchers.sameBeanAs;
import static com.github.karsaig.approvalcrest.model.Bean.Builder.bean;
import static com.github.karsaig.approvalcrest.model.BeanWithPrimitives.Builder.beanWithPrimitives;
import static com.github.karsaig.approvalcrest.model.ChildBean.Builder.child;
import static com.github.karsaig.approvalcrest.model.ParentBean.Builder.parent;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Map;
import java.util.Set;

import org.junit.ComparisonFailure;
import org.junit.Test;

import com.github.karsaig.approvalcrest.matcher.Matchers;
import com.github.karsaig.approvalcrest.model.Bean;
import com.github.karsaig.approvalcrest.model.BeanWithPrimitives;
import com.github.karsaig.approvalcrest.model.ChildBean;
import com.github.karsaig.approvalcrest.model.ParentBean;

/**
 * Tests for {@link MatcherAssert} which verify that fields can be ignored from the comparison.
 */
public class MatcherAssertIgnoringFieldTest {

	@Test
	public void ignoresField() {
		Bean.Builder expected = bean().string("banana");
		Bean.Builder actual = bean().string("apple");
		
		MatcherAssert.assertThat(actual, Matchers.sameBeanAs(expected).ignoring("string"));
	}
	
	@Test(expected = ComparisonFailure.class)
	public void failsWhenBeanDoesNotMatchedEvenAfterIgnoringField() {
		Bean.Builder expected = bean().string("banana").integer(2);
		Bean.Builder actual = bean().string("apple");
		
		MatcherAssert.assertThat(actual, Matchers.sameBeanAs(expected).ignoring("string"));
	}
	
	@Test
	public void ignoresPrimitiveFields() {
		BeanWithPrimitives.Builder expected = beanWithPrimitives().beanByte((byte)1).beanInt(1).beanChar('a').beanShort((short)2).beanLong(3).beanFloat(4).beanDouble(5).beanBoolean(true);
		BeanWithPrimitives.Builder actual = beanWithPrimitives().beanByte((byte)2).beanInt(2).beanChar('b').beanShort((short)3).beanLong(4).beanFloat(5).beanDouble(6).beanBoolean(false);
		
		assertThat(actual, sameBeanAs(expected).ignoring("beanInt").ignoring("beanByte").ignoring("beanChar").ignoring("beanShort").ignoring("beanLong").ignoring("beanFloat").ignoring("beanDouble").ignoring("beanBoolean"));
	}
	
	@Test
	public void ignoresFieldInNestedBean() {
		ParentBean.Builder expected = parent().childBean(child().childString("banana"));
		ParentBean.Builder actual = parent().childBean(child().childString("orange"));
		
		MatcherAssert.assertThat(actual, Matchers.sameBeanAs(expected).ignoring("childBean.childString"));
	}
	
	@Test(expected = ComparisonFailure.class)
	public void failsWhenBeanDoesNotMatchEvenAfterIgnoringFieldInNestedBean() {
		ParentBean.Builder expected = parent().parentString("expected").childBean(child().childString("banana"));
		ParentBean.Builder actual = parent().childBean(child().childString("orange"));
		
		MatcherAssert.assertThat(actual, Matchers.sameBeanAs(expected).ignoring("childBean.childString"));
	}
	
	@Test
	public void allowsToSpecifySubpathOnNullObjects() {
		ParentBean.Builder expected = parent().parentString("banana");
		ParentBean.Builder actual = parent().parentString("banana");
		
		MatcherAssert.assertThat(actual, Matchers.sameBeanAs(expected).ignoring("childBean.nonExistingField"));
	}
	
	@Test
	public void allowsToIgnoreNullObjects() {
		ParentBean.Builder expected = parent().parentString("banana");
		ParentBean.Builder actual = parent().parentString("banana");
		
		MatcherAssert.assertThat(actual, Matchers.sameBeanAs(expected).ignoring("childBean"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void throwsIllegalArgumentExceptionWhenSubpathIsSpecifiedOnPrimitiveField() {
		ParentBean.Builder expected = parent().childBean(child().childString("banana"));
		ParentBean.Builder actual = parent().childBean(child().childString("orange"));
		
		MatcherAssert.assertThat(actual, Matchers.sameBeanAs(expected).ignoring("childBean.childString.subpath"));
	}
	
	@Test
	public void ignoresFieldsInBeansWhitinList() {
		ParentBean expected = parent()
				.addToChildBeanList(child().childString("kiwi"))
				.addToChildBeanList(child().childString("plum")).build();
		ParentBean actual = parent()
				.addToChildBeanList(child().childString("banana"))
				.addToChildBeanList(child().childString("grape")).build();
		
		MatcherAssert.assertThat(actual, Matchers.sameBeanAs(expected).ignoring("childBeanList.childString"));
	}
	
	@Test(expected = ComparisonFailure.class)
	public void failsWhenBeanDoesNotMatchAfterIgnoringFieldsInBeansWhitinList() {
		ParentBean expected = parent()
				.addToChildBeanList(child().childString("kiwi").childInteger(2))
				.addToChildBeanList(child().childString("plum")).build();
		ParentBean actual = parent()
				.addToChildBeanList(child().childString("banana"))
				.addToChildBeanList(child().childString("grape")).build();
		
		MatcherAssert.assertThat(actual, Matchers.sameBeanAs(expected).ignoring("childBeanList.childString"));
	}
	
	@Test
	public void ignoresFieldsInBeansWhitinListThatContainsNullBeans() {
		ParentBean.Builder expected = parent()
				.addToChildBeanList(child().childString("kiwi"))
				.addToChildBeanList((ChildBean)null)
				.addToChildBeanList(child().childString("plum"));
		ParentBean.Builder actual = parent()
				.addToChildBeanList(child().childString("banana"))
				.addToChildBeanList((ChildBean)null)
				.addToChildBeanList(child().childString("grape"));
		
		MatcherAssert.assertThat(actual, Matchers.sameBeanAs(expected).ignoring("childBeanList.childString"));
	}
	
	@Test
	public void ignoresFieldsInSet() {
		Bean expected1 = bean().integer(1).string("value").build();
		Bean expected2 = bean().string("value").hashSet(newHashSet(bean().build())).build();
		Bean expected = bean().set(newHashSet(expected1, expected2)).build();
		
		Bean actual1 = bean().string("value").build();
		Bean actual2 = bean().string("value").hashSet(newHashSet(bean().build())).build();
		Bean actual = bean().set(newHashSet(actual1, actual2)).build();
		
		assertThat(actual, sameBeanAs(expected).ignoring("set.integer"));
	}
	
	@Test
	public void ignoresSet() {
		Bean expected1 = bean().integer(1).string("value").build();
		Bean expected2 = bean().string("value").hashSet(newHashSet(bean().build())).build();
		Bean expected = bean().set(newHashSet(expected1, expected2)).integer(1).build();
		
		Bean actual1 = bean().string("value").build();
		Bean actual2 = bean().string("value").hashSet(newHashSet(bean().build())).build();
		Bean actual = bean().set(newHashSet(actual1, actual2)).integer(1).build();
		
		assertThat(actual, sameBeanAs(expected).ignoring("set"));
	}
	
	@Test
	public void ignoresMap() {
		Map<Object, Object> expectedMap = newHashMap();
		expectedMap.put("key", newHashSet(bean().string("value").integer(1)));
		expectedMap.put("key2", newHashSet(bean().string("value")));
		MapContainer expected = new MapContainer(expectedMap);

		Map<Object, Object> actualMap = newHashMap();
		actualMap.put("key", newHashSet(bean().string("value").integer(2)));
		actualMap.put("key2", newHashSet(bean().string("value")));
		MapContainer actual = new MapContainer(actualMap);
		
		assertThat(actual, sameBeanAs(expected).ignoring("map"));
	}
	
	@Test
	public void ignoresFieldsInSetWhenSetIsAtTopLevel() {
		Bean expected1 = bean().integer(1).string("value").build();
		Bean expected2 = bean().string("value").hashSet(newHashSet(bean().build())).build();
		Set<Bean> expected = newHashSet(expected1, expected2);
		
		Bean actual1 = bean().string("value").build();
		Bean actual2 = bean().string("value").hashSet(newHashSet(bean().build())).build();
		Set<Bean> actual = newHashSet(actual1, actual2);
		
		assertThat(actual, sameBeanAs(expected).ignoring("integer"));
	}
	
	@Test
	public void ignoresFieldsInArray() {
		Bean expected1 = bean().integer(1).string("value").build();
		Bean expected2 = bean().string("value").hashSet(newHashSet(bean().build())).build();
		Bean expected = bean().array(expected1, expected2).build();
		
		Bean actual1 = bean().string("value").build();
		Bean actual2 = bean().string("value").hashSet(newHashSet(bean().build())).build();
		Bean actual = bean().array(actual1, actual2).build();
		
		assertThat(actual, sameBeanAs(expected).ignoring("array.integer"));
	}
	
	@Test
	public void ignoresFieldsInSetInsideMap() {
		Map<Object, Object> expectedMap = newHashMap();
		expectedMap.put("key", newHashSet(bean().string("value").integer(1)));
		expectedMap.put("key2", newHashSet(bean().string("value")));
		MapContainer expected = new MapContainer(expectedMap);

		Map<Object, Object> actualMap = newHashMap();
		actualMap.put("key", newHashSet(bean().string("value").integer(2)));
		actualMap.put("key2", newHashSet(bean().string("value")));
		MapContainer actual = new MapContainer(actualMap);
		
		assertThat(actual, sameBeanAs(expected).ignoring("map.key.integer"));
	}
	
	@Test
	public void ignoresFieldsInArrayWhenArrayIsAtTopLevel() {
		Bean expected1 = bean().integer(1).string("value").build();
		Bean expected2 = bean().string("value").hashSet(newHashSet(bean().build())).build();
		Bean[] expected = {expected1, expected2};
		
		Bean actual1 = bean().string("value").build();
		Bean actual2 = bean().string("value").hashSet(newHashSet(bean().build())).build();
		Bean[] actual = {actual1, actual2};
		
		assertThat(actual, sameBeanAs(expected).ignoring("integer"));
	}
	
	@Test
	public void ignoresFieldsInMapWhereKeyIsString() {
		Map<Object, Object> expectedMap = newHashMap();
		expectedMap.put("key", bean().integer(1).string("value").build());
		expectedMap.put("key2", bean().integer(1).string("value").build());
		MapContainer expected = new MapContainer(expectedMap);

		Map<Object, Object> actualMap = newHashMap();
		actualMap.put("key", bean().integer(1).string("unexpected value").build());
		actualMap.put("key2", bean().integer(1).string("unexpected value").build());
		MapContainer actual = new MapContainer(actualMap);
		
		assertThat(actual, sameBeanAs(expected).ignoring("map.key.string").ignoring("map.key2.string"));
	}
	
	@Test
	public void ignoresElementsInMap() {
		Map<Object, Object> expectedMap = newHashMap();
		expectedMap.put("key", bean().integer(1).string("value").build());
		MapContainer expected = new MapContainer(expectedMap);

		Map<Object, Object> actualMap = newHashMap();
		actualMap.put("key", bean().integer(1).string("unexpected value").build());
		MapContainer actual = new MapContainer(actualMap);
		
		assertThat(actual, sameBeanAs(expected).ignoring("map.key"));
	}
	
	@Test
	public void ignoresElementsInMapWhenMapIsAtTopLevel() {
		Map<Object, Object> expected = newHashMap();
		expected.put("key", bean().integer(1).string("value").build());
		expected.put("key2", bean().integer(1).string("value").build());
		
		Map<Object, Object> actual = newHashMap();
		actual.put("key", bean().integer(1).string("unexpected value").build());
		actual.put("key2", bean().integer(1).string("value").build());
		
		assertThat(actual, sameBeanAs(expected).ignoring("key"));
	}
	
	@Test
	public void ignoresFieldsInMapWhereKeyIsPrimitive() {
		Map<Object, Object> expectedMap = newHashMap();
		expectedMap.put(1, bean().integer(1).string("value").build());
		MapContainer expected = new MapContainer(expectedMap);
		
		Map<Object, Object> actualMap = newHashMap();
		actualMap.put(1, bean().integer(1).string("unexpected value").build());
		MapContainer actual = new MapContainer(actualMap);
		
		assertThat(actual, sameBeanAs(expected).ignoring("map.1.string"));
	}
	
	@Test
	public void ignoresFieldsInMapWhereKeyIsEnum() {
		Map<Object, Object> expectedMap = newHashMap();
		expectedMap.put(TestEnum.ONE, bean().integer(1).string("value").build());
		MapContainer expected = new MapContainer(expectedMap);
		
		Map<Object, Object> actualMap = newHashMap();
		actualMap.put(TestEnum.ONE, bean().integer(1).string("unexpected value").build());
		MapContainer actual = new MapContainer(actualMap);
		
		assertThat(actual, sameBeanAs(expected).ignoring("map.ONE.string"));
	}
	
	@Test
	public void ignoresFieldsInMapWhereKeyIsObject() {
		Map<Object, Object> expectedMap = newHashMap();
		expectedMap.put(bean().integer(1).string("1").build(), beanWithPrimitives().beanDouble(2.0).build());
		expectedMap.put(bean().integer(2).string("2").build(), beanWithPrimitives().beanDouble(3.0).build());
		MapContainer expected = new MapContainer(expectedMap);
		
		Map<Object, Object> actualMap = newHashMap();
		actualMap.put(bean().integer(1).string("1").build(), beanWithPrimitives().beanDouble(2.0).build());
		actualMap.put(bean().integer(2).build(), beanWithPrimitives().beanDouble(3.0).build());
		MapContainer actual = new MapContainer(actualMap);
		
		assertThat(actual, sameBeanAs(expected).ignoring("map.string"));
	}
	
	@Test
	public void ignoresFieldsInMapWhenMapIsAtTopLevel() {
		Map<Object, Object> expected = newHashMap();
		expected.put(bean().integer(1).string("1").build(), beanWithPrimitives().beanDouble(2.0).build());
		expected.put(bean().integer(2).string("2").build(), beanWithPrimitives().beanDouble(3.0).build());
		
		Map<Object, Object> actual = newHashMap();
		actual.put(bean().integer(1).string("1").build(), beanWithPrimitives().beanDouble(2.0).build());
		actual.put(bean().integer(2).build(), beanWithPrimitives().beanDouble(3.0).build());
		
		assertThat(actual, sameBeanAs(expected).ignoring("string"));
	}
	
	private enum TestEnum {
		ONE
	}
	
	private static class MapContainer {
		@SuppressWarnings("unused")
		private Map<Object, Object> map;
		
		public MapContainer(Map<Object, Object> map) {
			this.map = map;
		}

		public Map<Object, Object> getMap() {
			return map;
		}
	}
}
