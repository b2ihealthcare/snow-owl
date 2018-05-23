/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.junit.Before;
import org.junit.Test;

import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionFixtures.Data;
import com.google.common.collect.ImmutableList;

/**
 * @since 6.4
 */
public class RevisionRangePathQueryTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(Data.class);
	}

	private String branchA;
	
	@Before
	@Override
	public void setup() {
		super.setup();
		branchA = createBranch(MAIN, "a");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void readRangeWithoutBase() throws Exception {
		search(RevisionIndex.toRevisionRange("", branchA), Query.select(Data.class).where(Expressions.matchAll()).build());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void readRangeWithoutCompare() throws Exception {
		search(RevisionIndex.toRevisionRange(MAIN, ""), Query.select(Data.class).where(Expressions.matchAll()).build());
	}
	
	@Test
	public void readRange() throws Exception {
		final Data data1 = new Data(STORAGE_KEY1, "field1", "field2");
		final Data data2 = new Data(STORAGE_KEY2, "field1", "field2Changed");
		indexRevision(MAIN, data1);
		indexRevision(branchA, data2);
		final Iterable<Data> hits = search(RevisionIndex.toRevisionRange(MAIN, branchA), Query.select(Data.class).where(Expressions.matchAll()).build());
		assertThat(hits).containsOnly(data2);
	}
	
}
