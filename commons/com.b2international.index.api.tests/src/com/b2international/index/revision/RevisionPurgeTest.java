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

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionFixtures.Data;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import junit.framework.AssertionFailedError;

/**
 * @since 5.0
 */
public class RevisionPurgeTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableSet.<Class<?>>of(Data.class);
	}
	
	@Test
	public void purgeEmptyBranch() throws Exception {
		index().purge(MAIN, Purge.ALL);
	}
	
	@Test
	public void purgeBranchWithSingleRevision() throws Exception {
		indexRevision(MAIN, STORAGE_KEY1, new Data("field1", "field2"));
		index().purge(MAIN, Purge.ALL);
		// the revision should be still there
		assertNotNull(getRevision(MAIN, Data.class, STORAGE_KEY1));
	}
	
	@Test
	public void purgeBranchWithTwoRevisionsOfDocument() throws Exception {
		indexRevision(MAIN, STORAGE_KEY1, new Data("field1", "field2"));
		indexRevision(MAIN, STORAGE_KEY1, new Data("field1Changed", "field2"));
		index().purge(MAIN, Purge.ALL);
		// only the most recent revision should be available
		final Iterable<Data> revisions = searchRaw(Query.select(Data.class)
				.where(Expressions.exactMatch(Revision.STORAGE_KEY, STORAGE_KEY1))
				.limit(Integer.MAX_VALUE)
				.build());
		assertEquals(1, Iterables.size(revisions));
		final Data actual = Iterables.getOnlyElement(revisions);
		assertDocEquals(new Data("field1Changed", "field2"), actual);
	}
	
	@Test
	public void purgeMainWithTwoRevisionsAndABranch() throws Exception {
		indexRevision(MAIN, STORAGE_KEY1, new Data("field1", "field2"));
		createBranch(MAIN, "a");
		indexRevision(MAIN, STORAGE_KEY1, new Data("field1Changed", "field2"));
		index().purge(MAIN, Purge.ALL);
		
		// both revisions should still remain, because the "MAIN/a" requires the first one
		final Iterable<Data> revisions = searchRaw(Query.select(Data.class)
				.where(Expressions.exactMatch(Revision.STORAGE_KEY, STORAGE_KEY1))
				.limit(Integer.MAX_VALUE)
				.build());
		assertEquals(2, Iterables.size(revisions));
		final Data actualOnMAIN = getRevision(MAIN, Data.class, STORAGE_KEY1);
		assertDocEquals(new Data("field1Changed", "field2"), actualOnMAIN);
		
		final Data actualOnChild = getRevision("MAIN/a", Data.class, STORAGE_KEY1);
		assertDocEquals(new Data("field1", "field2"), actualOnChild);
	}
	
	@Test
	public void purgeChildBranchDoesNotPurgeParentRevisions() throws Exception {
		indexRevision(MAIN, STORAGE_KEY1, new Data("field1", "field2"));
		indexRevision(MAIN, STORAGE_KEY1, new Data("field1Changed", "field2"));
		createBranch(MAIN, "a");
		
		index().purge("MAIN/a", Purge.ALL);
		
		final Iterable<Data> revisions = searchRaw(Query.select(Data.class)
				.where(Expressions.exactMatch(Revision.STORAGE_KEY, STORAGE_KEY1))
				.limit(Integer.MAX_VALUE)
				.build());
		assertEquals(2, Iterables.size(revisions));
	}
	
	@Test
	public void purgeLatestPurgesOnlyMostRecentSegment() throws Exception {
		indexRevision(MAIN, STORAGE_KEY1, new Data("field1", "field2"));
		indexRevision(MAIN, STORAGE_KEY1, new Data("field1Changed", "field2"));
		createBranch(MAIN, "a");
		indexRevision(MAIN, STORAGE_KEY1, new Data("field1Changed", "field2Changed"));
		
		index().purge(MAIN, Purge.LATEST);
		
		final Iterable<Data> revisions = searchRaw(Query.select(Data.class)
				.where(Expressions.exactMatch(Revision.STORAGE_KEY, STORAGE_KEY1))
				.limit(Integer.MAX_VALUE)
				.build());
		assertEquals(3, Iterables.size(revisions));
	}
	
	@Test
	public void purgeHistoryPurgesHistoricalSegments() throws Exception {
		indexRevision(MAIN, STORAGE_KEY1, new Data("field1", "field2"));
		indexRevision(MAIN, STORAGE_KEY1, new Data("field1Changed", "field2"));
		createBranch(MAIN, "a");
		indexRevision(MAIN, STORAGE_KEY1, new Data("field1Changed", "field2Changed"));
		indexRevision(MAIN, STORAGE_KEY1, new Data("field1Latest", "field2Latest"));
		
		index().purge(MAIN, Purge.HISTORY);
		
		final Iterable<Data> revisions = searchRaw(Query.select(Data.class)
				.where(Expressions.exactMatch(Revision.STORAGE_KEY, STORAGE_KEY1))
				.limit(Integer.MAX_VALUE)
				.build());
		assertEquals(3, Iterables.size(revisions));
		int revisionsInSegment0 = 0;
		int revisionsInSegment2 = 0;
		for (Data rev : revisions) {
			if (rev.getSegmentId() == 0) {
				revisionsInSegment0++;
			} else if (rev.getSegmentId() == 2) {
				revisionsInSegment2++;
			} else {
				throw new AssertionFailedError("Unexpected segment ID: " + rev.getSegmentId());
			}
		}
		assertEquals(1, revisionsInSegment0);
		assertEquals(2, revisionsInSegment2);
	}
	
}
