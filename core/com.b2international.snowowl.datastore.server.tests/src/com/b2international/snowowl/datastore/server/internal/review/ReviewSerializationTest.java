/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.internal.review;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.datastore.review.ConceptChanges;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.review.ReviewStatus;
import com.b2international.snowowl.datastore.server.internal.JsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.2
 */
public class ReviewSerializationTest {

	private Review review;
	private ConceptChanges conceptChanges;
	private ObjectMapper mapper = JsonSupport.getDefaultObjectMapper();

	@Before
	public void setUp() {
		final Branch source = mock(Branch.class);
		when(source.path()).thenReturn("MAIN/a");
		when(source.baseTimestamp()).thenReturn(1L);
		when(source.headTimestamp()).thenReturn(10L);
		
		final Branch target = mock(Branch.class);
		when(target.path()).thenReturn("MAIN/a/b");
		when(target.baseTimestamp()).thenReturn(5L);
		when(target.headTimestamp()).thenReturn(8L);
		
		this.review = Review.builder("id", source, target).status(ReviewStatus.CURRENT).lastUpdated("2015-07-14T00:00:00Z").build();
		this.conceptChanges = new ConceptChanges("id", ImmutableSet.of("new1", "new2"), ImmutableSet.of("changed1"), ImmutableSet.of("deleted1", "deleted2", "deleted3"));
	}
	
	@Test
	public void serializeReview() throws Exception {
		final String json = mapper.writeValueAsString(review);

		assertEquals("{\"id\":\"id\","
				+ "\"source\":{\"path\":\"MAIN/a\",\"baseTimestamp\":1,\"headTimestamp\":10},"
				+ "\"target\":{\"path\":\"MAIN/a/b\",\"baseTimestamp\":5,\"headTimestamp\":8},"
				+ "\"status\":\"CURRENT\","
				+ "\"lastUpdated\":\"2015-07-14T00:00:00Z\"}", json);
	}
	
	@Test
	public void deserializeReview() throws Exception {
		final String json = mapper.writeValueAsString(review);
		final Review value = mapper.readValue(json, Review.class);
		
		assertEquals("id", value.id());
		assertEquals("MAIN/a", value.source().path());
		assertEquals(1L, value.source().baseTimestamp());
		assertEquals(10L, value.source().headTimestamp());
		assertEquals("MAIN/a/b", value.target().path());
		assertEquals(5L, value.target().baseTimestamp());
		assertEquals(8L, value.target().headTimestamp());
		assertEquals(ReviewStatus.CURRENT, value.status());
	}
	
	@Test
	public void serializeConceptChanges() throws Exception {
		final String json = mapper.writeValueAsString(conceptChanges);

		assertEquals("{\"id\":\"id\","
				+ "\"newConcepts\":[\"new1\",\"new2\"],"
				+ "\"changedConcepts\":[\"changed1\"],"
				+ "\"deletedConcepts\":[\"deleted1\",\"deleted2\",\"deleted3\"]}", json);
	}
	
	@Test
	public void deserializeCDOBranchImpl() throws Exception {
		final String json = mapper.writeValueAsString(conceptChanges);
		final ConceptChanges value = mapper.readValue(json, ConceptChanges.class);
		
		assertEquals("id", value.id());
		assertEquals(ImmutableSet.of("new1", "new2"), value.newConcepts());
		assertEquals(ImmutableSet.of("changed1"), value.changedConcepts());
		assertEquals(ImmutableSet.of("deleted1", "deleted2", "deleted3"), value.deletedConcepts());
	}
}
