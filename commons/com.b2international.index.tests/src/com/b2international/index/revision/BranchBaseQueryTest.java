/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;

import org.junit.Test;

import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionFixtures.Data;
import com.google.common.collect.ImmutableList;

/**
 * @since 5.9
 */
public class BranchBaseQueryTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(Data.class);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void readBaseOfMainBranch() throws Exception {
		final Data data = new Data("field1", "field2");
		indexRevision(MAIN, STORAGE_KEY1, data);
		final Iterable<Data> hits = search(MAIN + RevisionIndex.BASE_REF_CHAR, Query.select(Data.class).where(Expressions.matchAll()).build());
		assertThat(hits).isEmpty();
	}
	
	@Test
	public void readBaseOfBranch() throws Exception {
		final Data data = new Data("field1", "field2");
		indexRevision(MAIN, STORAGE_KEY1, data);
		final String branch = createBranch(MAIN, "a");
		final Iterable<Data> hits = search(branch + RevisionIndex.BASE_REF_CHAR, Query.select(Data.class).where(Expressions.matchAll()).build());
		assertThat(hits).containsOnly(data);
	}

	@Test
	public void readBaseOfBranchWithNewComponents() throws Exception {
		final Data data = new Data("field1", "field2");
		indexRevision(MAIN, STORAGE_KEY1, data);
		final String branch = createBranch(MAIN, "a");
		
		indexRevision(branch, STORAGE_KEY2, new Data("field1Other", "field2Other"));
		
		final Iterable<Data> hits = search(branch + RevisionIndex.BASE_REF_CHAR, Query.select(Data.class).where(Expressions.matchAll()).build());
		assertThat(hits).containsOnly(data);
	}
	
	@Test
	public void readBaseOfBranchWithChangedComponents() throws Exception {
		final Data data = new Data("field1", "field2");
		indexRevision(MAIN, STORAGE_KEY1, data);
		final String branch = createBranch(MAIN, "a");
		
		indexRevision(branch, STORAGE_KEY1, new Data("field1Changed", "field2Changed"));
		
		final Iterable<Data> hits = search(branch + RevisionIndex.BASE_REF_CHAR, Query.select(Data.class).where(Expressions.matchAll()).build());
		assertThat(hits).containsOnly(data);
	}
	
	@Test
	public void readBaseOfBranchWithDeletedComponents() throws Exception {
		final Data data = new Data("field1", "field2");
		indexRevision(MAIN, STORAGE_KEY1, data);
		final String branch = createBranch(MAIN, "a");
		
		deleteRevision(MAIN, Data.class, STORAGE_KEY1);
		
		final Iterable<Data> hits = search(branch + RevisionIndex.BASE_REF_CHAR, Query.select(Data.class).where(Expressions.matchAll()).build());
		assertThat(hits).containsOnly(data);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void writeBaseOfBranch() throws Exception {
		indexRevision(createBranch(MAIN, "a") + RevisionIndex.BASE_REF_CHAR, STORAGE_KEY1, new Data("field1", "field2"));
	}

}
