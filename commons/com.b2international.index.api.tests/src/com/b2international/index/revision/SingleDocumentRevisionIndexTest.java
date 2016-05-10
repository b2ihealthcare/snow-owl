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
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;

import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionFixtures.Data;

public abstract class SingleDocumentRevisionIndexTest extends BaseRevisionIndexTest {

	@Test
	public void searchEmptyIndexShouldReturnNullRevision() throws Exception {
		final Data revision = index().read(RevisionBranch.MAIN_PATH, new RevisionIndexRead<Data>() {
			@Override
			public Data execute(RevisionSearcher index) throws IOException {
				return index.get(Data.class, STORAGE_KEY1);
			}
		});
		
		assertNull(revision);
	}
	
	@Test
	public void indexRevision() throws Exception {
		indexRevision(STORAGE_KEY1, new Data("field1", "field2"));
	}

	@Test
	public void indexTwoRevisions() throws Exception {
		indexRevision();
		indexRevision(STORAGE_KEY1, new Data("field1Changed", "field2Changed"));
	}

	@Test
	public void deleteRevision() throws Exception {
		indexRevision();
		deleteRevision(STORAGE_KEY1);
	}
	
	@Test
	public void updateThenDeleteRevision() throws Exception {
		indexTwoRevisions();
		deleteRevision(STORAGE_KEY1);
	}
	
	@Test
	public void searchDifferentRevisions() throws Exception {
		final Data first = new Data("field1", "field2");
		final Data second = new Data("field1Changed", "field2");
		
		indexRevision(STORAGE_KEY1, first);
		indexRevision(STORAGE_KEY2, second);
		
		final Iterable<Data> matches = index().read(RevisionBranch.MAIN_PATH, new RevisionIndexRead<Iterable<Data>>() {
			@Override
			public Iterable<Data> execute(RevisionSearcher index) throws IOException {
				final Query<Data> query = Query.builder(Data.class).selectAll().where(Expressions.exactMatch("field1", "field1")).build();
				return index.search(query);
			}
		});
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(first);
	}

	@Test
	public void searchMultipleRevisions() throws Exception {
		final Data first = new Data("field1", "field2");
		final Data second = new Data("field1", "field2Changed");
		
		indexRevision(STORAGE_KEY1, first);
		indexRevision(STORAGE_KEY1, second);
		
		final Iterable<Data> matches = index().read(RevisionBranch.MAIN_PATH, new RevisionIndexRead<Iterable<Data>>() {
			@Override
			public Iterable<Data> execute(RevisionSearcher index) throws IOException {
				final Query<Data> query = Query.builder(Data.class).selectAll().where(Expressions.exactMatch("field1", "field1")).build();
				return index.search(query);
			}
		});
		// only second version should match, the first revision should be unaccessible without timestamp
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(second);
	}
	
}
