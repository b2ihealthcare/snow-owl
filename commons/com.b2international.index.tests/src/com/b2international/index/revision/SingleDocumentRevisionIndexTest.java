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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;

import org.junit.Test;

import com.b2international.index.revision.RevisionFixtures.RevisionData;
import com.b2international.index.revision.RevisionFixtures.ScoredData;
import com.google.common.collect.ImmutableList;

public class SingleDocumentRevisionIndexTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(RevisionData.class, ScoredData.class);
	}
	
	@Test
	public void searchEmptyIndexShouldReturnNullRevision() throws Exception {
		final RevisionData revision = getRevision(MAIN, RevisionData.class, STORAGE_KEY1);
		assertNull(revision);
	}
	
	@Test
	public void indexRevision() throws Exception {
		final RevisionData data = new RevisionData(STORAGE_KEY1, "field1", "field2");
		indexRevision(MAIN, data);
		assertEquals(data, getRevision(MAIN, RevisionData.class, STORAGE_KEY1));
	}

	@Test
	public void updateRevision() throws Exception {
		indexRevision();
		final RevisionData data = new RevisionData(STORAGE_KEY1, "field1Changed", "field2Changed");
		indexRevision(MAIN, data);
		assertEquals(data, getRevision(MAIN, RevisionData.class, STORAGE_KEY1));
	}

	@Test
	public void deleteRevision() throws Exception {
		indexRevision();
		deleteRevision(MAIN, RevisionData.class, STORAGE_KEY1);
		assertNull(getRevision(MAIN, RevisionData.class, STORAGE_KEY1));
	}
	
	@Test
	public void updateThenDeleteRevision() throws Exception {
		updateRevision();
		deleteRevision(MAIN, RevisionData.class, STORAGE_KEY1);
		assertNull(getRevision(MAIN, RevisionData.class, STORAGE_KEY1));
	}
	
	@Test
	public void parentRevisionsStillVisibleAfterNewChildBranch() throws Exception {
		indexRevision();
		final String childBranch = createBranch(MAIN, "a");
		assertNotNull(getRevision(childBranch, RevisionData.class, STORAGE_KEY1));
		assertNotNull(getRevision(MAIN, RevisionData.class, STORAGE_KEY1));
	}
	
	@Test
	public void updateRevisionShadowsRevisionOnParent() throws Exception {
		indexRevision();
		final String childBranch = createBranch(MAIN, "a");
		
		// put updated revision to child branch
		final RevisionData data = new RevisionData(STORAGE_KEY1, "field1Changed", "field2Changed");
		indexRevision(childBranch, data);
		// lookup by storageKey on child should return new revision
		assertEquals(data, getRevision(childBranch, RevisionData.class, STORAGE_KEY1));

		final RevisionData expectedRevisionOnMain = new RevisionData(STORAGE_KEY1, "field1", "field2");
		final RevisionData actualRevisionOnMain = getRevision(MAIN, RevisionData.class, STORAGE_KEY1);
		assertEquals(expectedRevisionOnMain, actualRevisionOnMain);
	}
	
	@Test
	public void divergedBranchContentShouldBeAccessibleBasedOnSegments() throws Exception {
		updateRevisionShadowsRevisionOnParent();
		
		// put new revision to MAIN
		final RevisionData data = new RevisionData(STORAGE_KEY1, "field1ChangedOnMain", "field2ChangedOnMain");
		indexRevision(MAIN, data);
		// lookup by storageKey on child should return new revision
		assertEquals(data, getRevision(MAIN, RevisionData.class, STORAGE_KEY1));
		
		// child branch still has his own updated revision
		final RevisionData expectedRevisionOnChild = new RevisionData(STORAGE_KEY1, "field1Changed", "field2Changed");
		final RevisionData actualRevisionOnChild = getRevision("MAIN/a", RevisionData.class, STORAGE_KEY1);
		assertEquals(expectedRevisionOnChild, actualRevisionOnChild);
	}
	
}
