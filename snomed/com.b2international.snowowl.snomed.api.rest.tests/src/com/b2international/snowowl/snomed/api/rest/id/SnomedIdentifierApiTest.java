/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.api.rest.id;

import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.api.rest.SnomedIdentifierRestRequests.generateSctId;
import static org.hamcrest.CoreMatchers.allOf;

import java.util.Arrays;
import java.util.Collection;

import org.hamcrest.CustomMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;

/**
 * @since 2.0
 */
@RunWith(Parameterized.class)
public class SnomedIdentifierApiTest extends AbstractSnomedApiTest {

	private static class CharAtMatcher extends CustomMatcher<String> {

		private final int indexFromEnd;
		private final char expectedChar;

		public CharAtMatcher(int index, char expectedChar) {
			super(String.format("character at index %d counted from the end is %c", index, expectedChar));

			this.indexFromEnd = index;
			this.expectedChar = expectedChar;
		}

		@Override
		public boolean matches(Object item) {
			if (!(item instanceof String)) {
				return false;
			}

			String str = ((String) item);
			return str.charAt(str.length() - indexFromEnd - 1) == expectedChar;
		}
	}

	private static class SegmentMatches extends CustomMatcher<String> {

		private final int start;
		private final int end;
		private final String expectedSegment;

		public SegmentMatches(int start, int end, String expectedSegment) {
			super(String.format("string segment in range (%d,%d) counted from the end matches %s", start, end, expectedSegment));

			this.start = start;
			this.end = end;
			this.expectedSegment = expectedSegment;
		}

		@Override
		public boolean matches(Object item) {
			if (!(item instanceof String)) {
				return false;
			}

			if (expectedSegment == null) {
				return true;
			}

			String str = ((String) item);
			return str.substring(str.length() - start, str.length() - end).equals(expectedSegment);
		}
	}

	@Parameters(name = "{index}_{0}_{1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			//	type								namespaceId	partitionDigit	componentDigit	
			{ 	SnomedComponentType.CONCEPT,		null, 		'0', 			'0' }, 
			{ 	SnomedComponentType.DESCRIPTION,	null, 		'0', 			'1' }, 
			{ 	SnomedComponentType.RELATIONSHIP,	null, 		'0', 			'2' }, 
			{ 	SnomedComponentType.CONCEPT,		"1000154",	'1', 			'0' }, 
			{ 	SnomedComponentType.DESCRIPTION,	"1000154",	'1', 			'1' }, 
			{ 	SnomedComponentType.RELATIONSHIP,	"1000154",	'1', 			'2' }, 
		});
	}

	private final SnomedComponentType type;
	private final String namespaceId;
	private final char partitionDigit;
	private final char componentDigit;

	public SnomedIdentifierApiTest(SnomedComponentType type, String namespaceId, char partitionDigit, char componentDigit) {
		this.type = type;
		this.namespaceId = namespaceId;
		this.partitionDigit = partitionDigit;
		this.componentDigit = componentDigit;
	}

	@Test
	public void generateComponentId() {
		String componentId = generateSctId(type, namespaceId)
				.statusCode(201)
				.body("id", allOf(new CharAtMatcher(1, componentDigit), new CharAtMatcher(2, partitionDigit), new SegmentMatches(10, 3, namespaceId)))
				.extract().body().path("id");

		// A generated SCTID that was just returned should not be in use
		getComponent(branchPath, type, componentId).statusCode(404);
	}
}
