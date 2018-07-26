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

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Test;

import com.b2international.index.revision.RevisionFixtures.RevisionData;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.0
 */
public class RevisionBranchMergeTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(RevisionData.class);
	}
	
	@Test
	public void mergeBranchWithNewRevisionToParent() throws Exception {
		String child = createBranch(MAIN, "a");
		// create a revision on child branch
		indexRevision(child, new RevisionData(STORAGE_KEY1, "field1", "field2"));
		branching().merge(child, MAIN, "Merge");
		// after merge revision should be visible from MAIN branch
		assertNotNull(getRevision(MAIN, RevisionData.class, STORAGE_KEY1));
	}
	
	@Test
	public void rebaseBranchOnParentWithNewRevision() throws Exception {
		String child = createBranch(MAIN, "a");
		// create a revision on MAIN branch
		indexRevision(MAIN, new RevisionData(STORAGE_KEY1, "field1", "field2"));
		
		branching().rebase(child, MAIN, "Rebase", () -> {});
		// after rebase revision should be visible from child branch
		assertNotNull(getRevision(child, RevisionData.class, STORAGE_KEY1));
	}
	
	@Test
	public void mergeBranchWithChangedRevisionToParent() throws Exception {
		RevisionData first = new RevisionData(STORAGE_KEY1, "field1", "field2");
		indexRevision(MAIN, first);
		String child = createBranch(MAIN, "a");
		// create a revision on child branch
		RevisionData updated = new RevisionData(STORAGE_KEY1, "field1Changed", "field2");
		indexChange(child, first, updated);
		branching().merge(child, MAIN, "Merge");
		// after merge revision should be visible from MAIN branch
		RevisionData afterMerge = getRevision(MAIN, RevisionData.class, STORAGE_KEY1);
		assertDocEquals(updated, afterMerge);
	}
	
	@Test
	public void rebaseBranchOnParentWithChangedRevision() throws Exception {
		RevisionData first = new RevisionData(STORAGE_KEY1, "field1", "field2");
		indexRevision(MAIN, first);
		String child = createBranch(MAIN, "a");
		// create a revision on child branch
		RevisionData updated = new RevisionData(STORAGE_KEY1, "field1Changed", "field2");
		indexChange(MAIN, first, updated);
		branching().rebase(child, MAIN, "Rebase", () -> {});
		// after merge revision should be visible from MAIN branch
		RevisionData afterRebase = getRevision(child, RevisionData.class, STORAGE_KEY1);
		assertDocEquals(updated, afterRebase);
	}
	
	@Test
	public void mergeBranchWithRemoveToParent() throws Exception {
		RevisionData first = new RevisionData(STORAGE_KEY1, "field1", "field2");
		indexRevision(MAIN, first);
		
		String child = createBranch(MAIN, "a");
		indexRemove(child, first);
		
		branching().merge(child, MAIN, "Merge");
		
		assertNull(getRevision(MAIN, RevisionData.class, STORAGE_KEY1));
	}
	
	@Test
	public void rebaseBranchOnParentWithRemove() throws Exception {
		RevisionData first = new RevisionData(STORAGE_KEY1, "field1", "field2");
		indexRevision(MAIN, first);
		
		String child = createBranch(MAIN, "a");
		indexRemove(MAIN, first);
		
		branching().rebase(child, MAIN, "Rebase", () -> {});
		
		assertNull(getRevision(child, RevisionData.class, STORAGE_KEY1));
	}
	
}
