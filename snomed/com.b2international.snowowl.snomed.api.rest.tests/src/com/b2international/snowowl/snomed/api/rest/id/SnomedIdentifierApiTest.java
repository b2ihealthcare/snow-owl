package com.b2international.snowowl.snomed.api.rest.id;

import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;
import static org.hamcrest.CoreMatchers.allOf;

import java.util.Arrays;
import java.util.Collection;

import org.hamcrest.CustomMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.http.ContentType;

@RunWith(Parameterized.class)
public class SnomedIdentifierApiTest extends AbstractSnomedApiTest {

	private static class CharAtMatcher extends CustomMatcher<String> {

		private final int indexFromEnd;
		private final char expectedChar;

		public CharAtMatcher(final int index, final char expectedChar) {
			super(String.format("character at index %d counted from the end is %c", index, expectedChar));
			
			this.indexFromEnd = index;
			this.expectedChar = expectedChar;
		}
		
		@Override
		public boolean matches(Object item) {
			if (!(item instanceof String)) {
				return false;
			}
			
			final String str = ((String) item);
			return str.charAt(str.length() - indexFromEnd - 1) == expectedChar;
		}
	}
	
	private static class SegmentMatches extends CustomMatcher<String> {
		
		private final int start;
		private final int end;
		private final String expectedSegment;
		
		public SegmentMatches(final int start, final int end, final String expectedSegment) {
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
	
	@Parameters(name = "{index}: {0} identifier with namespace {1}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
			//	type			namespaceId	partitionDigit	componentDigit	
			{ 	"Concept",		null, 		'0', 			'0' }, 
			{ 	"Description",	null, 		'0', 			'1' }, 
			{ 	"Relationship", null, 		'0', 			'2' }, 
			{ 	"Concept",		"1000154",	'1', 			'0' }, 
			{ 	"Description",	"1000154",	'1', 			'1' }, 
			{ 	"Relationship", "1000154",	'1', 			'2' }, 
		});
	}
	
	private final String type;
	private final String namespaceId;
	private final char partitionDigit;
	private final char componentDigit;

	public SnomedIdentifierApiTest(String type, String namespaceId, char partitionDigit, char componentDigit) {
		this.type = type;
		this.namespaceId = namespaceId;
		this.partitionDigit = partitionDigit;
		this.componentDigit = componentDigit;
	}

	private String assertGeneratedIdMatchesRequest() {
		
		final ImmutableMap.Builder<String, String> requestBuilder = ImmutableMap.builder();
		requestBuilder.put("type", type.toUpperCase());

		if (namespaceId != null) {
			requestBuilder.put("namespace", namespaceId);
		}
		
		return givenAuthenticatedRequest(SCT_API)
		.with()
			.contentType(ContentType.JSON)
		.and()
			.body(requestBuilder.build())
		.when()
			.post("/ids")
		.then()
		.assertThat()
			.statusCode(201)
		.and()
			.body("id", allOf(
				new CharAtMatcher(1, componentDigit),
				new CharAtMatcher(2, partitionDigit),
				new SegmentMatches(10, 3, namespaceId)))
		.and()
			.extract().response().path("id");
	}
	
	@Test
	public void generateComponentId() {
		String componentId = assertGeneratedIdMatchesRequest();
		assertComponentNotExists(type.toLowerCase() + "s", componentId, "MAIN");
	}
}
