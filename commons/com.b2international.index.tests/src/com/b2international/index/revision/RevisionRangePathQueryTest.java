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

import org.junit.Test;

import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionFixtures.RevisionData;
import com.google.common.collect.ImmutableList;

/**
 * @since 6.4
 */
public class RevisionRangePathQueryTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(RevisionData.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void readRangeWithoutBase() throws Exception {
		final String branchA = createBranch(MAIN, "a");
		search(RevisionIndex.toRevisionRange("", branchA), Query.select(RevisionData.class).where(Expressions.matchAll()).build());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void readRangeWithoutCompare() throws Exception {
		search(RevisionIndex.toRevisionRange(MAIN, ""), Query.select(RevisionData.class).where(Expressions.matchAll()).build());
	}
	
	@Test
	public void readRange() throws Exception {
		final String branchA = createBranch(MAIN, "a");
		final RevisionData data1 = new RevisionData(STORAGE_KEY1, "field1", "field2");
		final RevisionData data2 = new RevisionData(STORAGE_KEY2, "field1", "field2Changed");
		indexRevision(MAIN, data1);
		indexRevision(branchA, data2);
		final Iterable<RevisionData> hits = search(RevisionIndex.toRevisionRange(MAIN, branchA), Query.select(RevisionData.class).where(Expressions.matchAll()).build());
		assertThat(hits).containsOnly(data2);
	}
	
}