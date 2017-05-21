package com.github.karsaig.approvalcrest;

import org.junit.Ignore;
import org.junit.Test;

import com.github.karsaig.approvalcrest.matcher.Matchers;

@Ignore
public class ContentMatcherSampleTests {

	@Test
	public void testWithsSameContentAsApprovedMatcher(){
		String actualContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit.\nAenean commodo ligula eget dolor.";

		MatcherAssert.assertThat(actualContent, Matchers.sameContentAsApproved());
	}
}
