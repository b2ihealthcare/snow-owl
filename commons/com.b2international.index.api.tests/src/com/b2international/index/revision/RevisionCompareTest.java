/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import com.b2international.index.revision.RevisionFixtures.Data;
import com.google.common.collect.ImmutableSet;

/**
 * @since 5.0
 */
public class RevisionCompareTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableSet.<Class<?>>of(Data.class);
	}
	
	@Test
	public void compareBranchWithSelfReturnsEmptyCompare() throws Exception {
		final RevisionCompare compare = index().compare(MAIN, MAIN);
		assertTrue(compare.getNewComponents().isEmpty());
		assertTrue(compare.getChangedComponents().isEmpty());
		assertTrue(compare.getDeletedComponents().isEmpty());
	}
	
	@Test
	public void compareBranchWithoutChangesReturnsEmptyCompare() throws Exception {
		final String branch = createBranch(MAIN, "a");
		final RevisionCompare compare = index().compare(MAIN, branch);
		assertTrue(compare.getNewComponents().isEmpty());
		assertTrue(compare.getChangedComponents().isEmpty());
		assertTrue(compare.getDeletedComponents().isEmpty());
	}
	
	@Test
	public void compareBranchWithNewComponent() throws Exception {
		final String branch = createBranch(MAIN, "a");
		indexRevision(branch, new Data(STORAGE_KEY1, "field1", "field2"));
		final RevisionCompare compare = index().compare(MAIN, branch);
		assertEquals(1, compare.getNewComponents().size());
		assertTrue(compare.getNewComponents().get(Data.class).contains(STORAGE_KEY1));
		assertTrue(compare.getChangedComponents().isEmpty());
		assertTrue(compare.getDeletedComponents().isEmpty());
	}
	
	@Test
	public void compareBranchWithNewComponent_BaseWithNewComponent() throws Exception {
		indexRevision(MAIN, new Data(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		indexRevision(branch, new Data(STORAGE_KEY2, "field1", "field2"));
		
		final RevisionCompare compare = index().compare(MAIN, branch);
		assertEquals(1, compare.getNewComponents().size());
		assertTrue(compare.getNewComponents(Data.class).contains(STORAGE_KEY2));
		assertTrue(compare.getChangedComponents().isEmpty());
		assertTrue(compare.getDeletedComponents().isEmpty());
	}
	
	@Test
	public void compareBranchWithNewComponent_BaseWithNewComponent_Reverse() throws Exception {
		indexRevision(MAIN, new Data(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		indexRevision(branch, new Data(STORAGE_KEY2, "field1", "field2"));
		
		final RevisionCompare compare = index().compare(branch, MAIN);
		assertTrue(compare.getNewComponents().isEmpty());
		assertTrue(compare.getChangedComponents().isEmpty());
		assertTrue(compare.getDeletedComponents().isEmpty());
	}
	
	@Test
	public void compareChangeOnMainSinceBranchBasePoint_Reverse() throws Exception {
		indexRevision(MAIN, new Data(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		indexRevision(branch, new Data(STORAGE_KEY2, "field1", "field2"));
		indexRevision(MAIN, new Data(STORAGE_KEY1, "field1Changed", "field2"));
		
		final RevisionCompare compare = index().compare(branch, MAIN);
		assertTrue(compare.getNewComponents().isEmpty());
		assertEquals(1, compare.getChangedComponents().size());
		assertTrue(compare.getChangedComponents(Data.class).contains(STORAGE_KEY1));
		assertTrue(compare.getDeletedComponents().isEmpty());
	}
	
	@Test
	public void compareBranchWithChangedComponent() throws Exception {
		indexRevision(MAIN, new Data(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		indexRevision(branch, new Data(STORAGE_KEY1, "field1Changed", "field2"));
		
		final RevisionCompare compare = index().compare(MAIN, branch);
		assertTrue(compare.getNewComponents().isEmpty());
		assertEquals(1, compare.getChangedComponents().size());
		assertTrue(compare.getChangedComponents(Data.class).contains(STORAGE_KEY1));
		assertTrue(compare.getDeletedComponents().isEmpty());
	}
	
	@Test
	public void compareBranchWithChangedComponent_Reverse() throws Exception {
		indexRevision(MAIN, new Data(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		indexRevision(branch, new Data(STORAGE_KEY1, "field1Changed", "field2"));
		
		final RevisionCompare compare = index().compare(branch, MAIN);
		assertTrue(compare.getNewComponents().isEmpty());
		assertTrue(compare.getChangedComponents().isEmpty());
		assertTrue(compare.getDeletedComponents().isEmpty());
	}
	
	@Test
	public void compareBranchWithDeletedComponent() throws Exception {
		indexRevision(MAIN, new Data(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		deleteRevision(branch, Data.class, STORAGE_KEY1);
		
		final RevisionCompare compare = index().compare(MAIN, branch);
		assertTrue(compare.getNewComponents().isEmpty());
		assertTrue(compare.getChangedComponents().isEmpty());
		assertEquals(1, compare.getDeletedComponents().size());
		assertTrue(compare.getDeletedComponents(Data.class).contains(STORAGE_KEY1));
	}
	
	@Test
	public void compareBranchWithDeletedComponent_Reverse() throws Exception {
		indexRevision(MAIN, new Data(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		deleteRevision(branch, Data.class, STORAGE_KEY1);
		
		final RevisionCompare compare = index().compare(branch, MAIN);
		assertTrue(compare.getNewComponents().isEmpty());
		assertTrue(compare.getChangedComponents().isEmpty());
		assertTrue(compare.getDeletedComponents().isEmpty());
	}
	
	@Test
	public void compareBranchWithRevertedChanges() throws Exception {
		indexRevision(MAIN, new Data(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		// change storageKey1 component then revert the change
		indexRevision(branch, new Data(STORAGE_KEY1, "field1", "field2Changed"));
		indexRevision(branch, new Data(STORAGE_KEY1, "field1", "field2"));

		final RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getNewComponents()).isEmpty();
		assertThat(compare.getChangedComponents()).isEmpty();
		assertThat(compare.getDeletedComponents()).isEmpty();
	}
}
