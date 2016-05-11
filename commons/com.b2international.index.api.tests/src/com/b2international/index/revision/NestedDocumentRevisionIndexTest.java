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
package com.b2international.index.revision;

import static com.b2international.index.revision.RevisionFixtures.*;
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
public abstract class NestedDocumentRevisionIndexTest extends BaseRevisionIndexTest {

	private final String branchPath = RevisionBranch.MAIN_PATH;
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(NestedData.class);
	}
	
	@Test
	public void indexNestedDocument() throws Exception {
		final NestedData data = new NestedData("field1", new Data("field1", "field2"));
		indexRevision(branchPath, STORAGE_KEY1, data);
		assertEquals(data, getDocument(branchPath, NestedData.class, STORAGE_KEY1));
	}

	@Test
	public void nestedDocumentOfDeletedRevisionShouldNotBeAccessible() throws Exception {
		indexNestedDocument();
		deleteRevision(branchPath, NestedData.class, STORAGE_KEY1);
		
		// query to get parent document, should be none
		final Query<NestedData> parentDocQuery = Query.builder(NestedData.class).selectAll().where(Expressions.matchAll()).build();
		final Iterable<NestedData> parentDocs = search(branchPath, parentDocQuery);
		assertThat(parentDocs).isEmpty();
		
		// query to get nested child document, should be none
		final Query<Data> nestedDataQuery = Query.builder(Data.class, NestedData.class).selectAll().where(Expressions.matchAll()).build();
		final Iterable<Data> nestedDocs = search(branchPath, nestedDataQuery);
		assertThat(nestedDocs).hasSize(0);
	}
	
	@Test
	public void searchNestedDocument() throws Exception {
		final NestedData data = new NestedData("field1", new Data("field1", "field2"));
		final NestedData data2 = new NestedData("field1", new Data("field1Changed", "field2"));
		indexRevision(branchPath, STORAGE_KEY1, data);
		indexRevision(branchPath, STORAGE_KEY2, data2);
		
		final Query<NestedData> query = Query.builder(NestedData.class).selectAll().where(Expressions.nestedMatch("data", Expressions.exactMatch("field1", "field1"))).build();
		final Iterable<NestedData> matches = search(branchPath, query);
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(data);
	}
	
}
