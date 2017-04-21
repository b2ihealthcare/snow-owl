/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.Ignore;
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
		
		final NestedData parent = new NestedData("parent1", child);
		indexRevision(MAIN, STORAGE_KEY1, parent);
		assertEquals(parent, getRevision(MAIN, NestedData.class, STORAGE_KEY1));
	}

	@Ignore("We don't support this anymore")
	@Test
	public void nestedDocumentOfDeletedRevisionShouldNotBeAccessible() throws Exception {
		indexNestedDocument();
		deleteRevision(MAIN, NestedData.class, STORAGE_KEY1);
		
		// query to get parent document, should be none
		final Query<NestedData> parentDocQuery = Query.select(NestedData.class)
				.where(Expressions.matchAll())
				.build();
		
		final Iterable<NestedData> parentDocs = search(MAIN, parentDocQuery);
		assertThat(parentDocs).isEmpty();
		
		// query to get nested child document, should be none
		final Query<Data> nestedDataQuery = Query.select(Data.class, NestedData.class)
				.where(Expressions.matchAll())
				.build();
		
		final Iterable<Data> nestedDocs = search(MAIN, nestedDataQuery);
		assertThat(nestedDocs).hasSize(0);
	}
	
	@Test
	public void searchParentDocumentWithNestedQuery() throws Exception {
		final Data child1 = new Data();
		child1.setField1("field1_1");
		child1.setField2("field2_1");
		final NestedData parent1 = new NestedData("parent1", child1);
		
		final Data child2 = new Data();
		child2.setField1("field1_2");
		child2.setField2("field2_2");
		final NestedData parent2 = new NestedData("parent2", child2);
		
		indexRevision(MAIN, STORAGE_KEY1, parent1);
		indexRevision(MAIN, STORAGE_KEY2, parent2);
		
		final Query<NestedData> query = Query.select(NestedData.class)
				.where(Expressions.nestedMatch("data", Expressions.exactMatch("field1", "field1_1")))
				.build();
		
		final Iterable<NestedData> matches = search(MAIN, query);
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(parent1);
	}
	
	@Ignore("We don't support this anymore")
	@Test
	public void searchNestedDocumentWithParentQuery() throws Exception {
		indexNestedDocument();
		deleteRevision(MAIN, NestedData.class, STORAGE_KEY1);
		
		// properties in storage key 2's nested child are same as storage key 1's nested child 
		final Data child = new Data();
		child.setField1("field1");
		child.setField2("field2");
		
		final NestedData data2 = new NestedData("parent2", child);
		indexRevision(MAIN, STORAGE_KEY2, data2);
		
		final Query<Data> nestedQuery = Query.select(Data.class, NestedData.class)
				.where(Expressions.exactMatch("field1", "field1"))
				.build();
		
		final Iterable<Data> nestedMatches = search(MAIN, nestedQuery);
		assertThat(nestedMatches).hasSize(1);
		assertThat(nestedMatches).containsOnly(child);
	}
	
}
