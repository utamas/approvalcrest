package com.github.karsaig.approvalcrest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.github.karsaig.approvalcrest.matcher.Matchers;
import com.github.karsaig.approvalcrest.model.BeanWithPrimitives;

public class JsonMatcherOverwriteTest extends AbstractJsonMatcherTest {

	private static final String OVERWRITE_FLAG_NAME = "jsonMatcherUpdateInPlace";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@BeforeClass
	public static void setup() {
		System.setProperty(OVERWRITE_FLAG_NAME, "true");
	}

	@AfterClass
	public static void tearDown() {
		System.clearProperty(OVERWRITE_FLAG_NAME);
	}

	@Test
	public void shouldThrowExceptionWhenSystemPropertyIsSetAndApprovedFileDoesNotExist() {
		// GIVEN
		BeanWithPrimitives input = getBeanWithPrimitives();
		expectedException.expect(AssertionError.class);
		expectedException.expectMessage(
				"Not approved file created: '57d703/287f64-not-approved.json'; please verify its contents and rename it to '287f64-approved.json'.");
		// WHEN
		MatcherAssert.assertThat(input, Matchers.sameJsonAsApproved());
		// THEN
		// Exception thrown
	}

	@Test
	public void shouldOverwriteApprovedFileWhenSystemPropertyIsSetAndApprovedFileExists() {
		// GIVEN
		BeanWithPrimitives input = getBeanWithPrimitives();
		// WHEN
		MatcherAssert.assertThat(input, Matchers.sameJsonAsApproved());
		// THEN
		
	}
}
