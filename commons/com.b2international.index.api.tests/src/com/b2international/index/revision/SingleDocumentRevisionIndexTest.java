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

import static com.b2international.index.revision.RevisionFixtures.STORAGE_KEY1;
import static com.b2international.index.revision.RevisionFixtures.STORAGE_KEY2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collection;

import org.junit.Test;

import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionFixtures.Data;
import com.google.common.collect.ImmutableList;

public class SingleDocumentRevisionIndexTest extends BaseRevisionIndexTest {

	private final String branchPath = RevisionBranch.MAIN_PATH;
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(Data.class);
	}
	
	@Test
	public void searchEmptyIndexShouldReturnNullRevision() throws Exception {
		final Data revision = getDocument(branchPath, Data.class, STORAGE_KEY1);
		assertNull(revision);
	}
	
	@Test
	public void indexRevision() throws Exception {
		final Data data = new Data("field1", "field2");
		indexRevision(branchPath, STORAGE_KEY1, data);
		assertEquals(data, getDocument(branchPath, Data.class, STORAGE_KEY1));
	}

	@Test
	public void updateRevisions() throws Exception {
		indexRevision();
		final Data data = new Data("field1Changed", "field2Changed");
		indexRevision(branchPath, STORAGE_KEY1, data);
		assertEquals(data, getDocument(branchPath, Data.class, STORAGE_KEY1));
	}

	@Test
	public void deleteRevision() throws Exception {
		indexRevision();
		deleteRevision(branchPath, Data.class, STORAGE_KEY1);
		assertNull(getDocument(branchPath, Data.class, STORAGE_KEY1));
	}
	
	@Test
	public void updateThenDeleteRevision() throws Exception {
		updateRevisions();
		deleteRevision(branchPath, Data.class, STORAGE_KEY1);
		assertNull(getDocument(branchPath, Data.class, STORAGE_KEY1));
	}
	
	@Test
	public void searchDifferentRevisions() throws Exception {
		final Data first = new Data("field1", "field2");
		final Data second = new Data("field1Changed", "field2");
		
		indexRevision(branchPath, STORAGE_KEY1, first);
		indexRevision(branchPath, STORAGE_KEY2, second);
		
		final Query<Data> query = Query.builder(Data.class).selectAll().where(Expressions.exactMatch("field1", "field1")).build();
		final Iterable<Data> matches = search(branchPath, query);
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(first);
	}

	@Test
	public void searchMultipleRevisions() throws Exception {
		final Data first = new Data("field1", "field2");
		final Data second = new Data("field1", "field2Changed");
		
		indexRevision(branchPath, STORAGE_KEY1, first);
		indexRevision(branchPath, STORAGE_KEY1, second);
		
		final Query<Data> query = Query.builder(Data.class).selectAll().where(Expressions.exactMatch("field1", "field1")).build();
		final Iterable<Data> matches = search(branchPath, query);
		// only second version should match, the first revision should be unaccessible without timestamp
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(second);
	}
	
}
