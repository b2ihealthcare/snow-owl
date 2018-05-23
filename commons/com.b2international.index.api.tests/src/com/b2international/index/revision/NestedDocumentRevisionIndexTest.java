/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionFixtures.NestedData;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.7
 */
public class NestedDocumentRevisionIndexTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(NestedData.class);
	}
	
	@Test
	public void indexNestedDocument() throws Exception {
		final Data child = new Data();
		child.setField1("field1");
		child.setField2("field2");
		
		final NestedData parent = new NestedData(STORAGE_KEY1, "parent1", child);
		indexRevision(MAIN, parent);
		assertEquals(parent, getRevision(MAIN, NestedData.class, STORAGE_KEY1));
	}

	@Test
	public void searchParentDocumentWithNestedQuery() throws Exception {
		final Data child1 = new Data();
		child1.setField1("field1_1");
		child1.setField2("field2_1");
		final NestedData parent1 = new NestedData(STORAGE_KEY1, "parent1", child1);
		
		final Data child2 = new Data();
		child2.setField1("field1_2");
		child2.setField2("field2_2");
		final NestedData parent2 = new NestedData(STORAGE_KEY2, "parent2", child2);
		
		indexRevision(MAIN, parent1);
		indexRevision(MAIN, parent2);
		
		final Query<NestedData> query = Query.select(NestedData.class)
				.where(Expressions.nestedMatch("data", Expressions.exactMatch("field1", "field1_1")))
				.build();
		
		final Iterable<NestedData> matches = search(MAIN, query);
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(parent1);
	}
	
}
