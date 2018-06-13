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

import java.util.Collection;

import org.junit.Test;

import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.revision.RevisionFixtures.Data;
import com.google.common.collect.ImmutableSet;

/**
 * @since 5.0
 */
public class RevisionCompareTest extends BaseRevisionIndexTest {

	private static final String DOC_TYPE = DocumentMapping.getType(Data.class);
	private static final ObjectId ROOT = ObjectId.rootOf(DOC_TYPE);
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableSet.<Class<?>>of(Data.class);
	}
	
	@Test
	public void compareBranchWithSelfReturnsEmptyCompare() throws Exception {
		final RevisionCompare compare = index().compare(MAIN, MAIN);
		assertThat(compare.getDetails()).isEmpty();
	}
	
	@Test
	public void compareBranchWithoutChangesReturnsEmptyCompare() throws Exception {
		final String branch = createBranch(MAIN, "a");
		final RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getDetails()).isEmpty();
	}
	
	@Test
	public void compareBranchWithNewComponent() throws Exception {
		final String branch = createBranch(MAIN, "a");
		indexRevision(branch, new Data(STORAGE_KEY1, "field1", "field2"));
		final RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getDetails()).hasSize(1);
		final RevisionCompareDetail detail = compare.getDetails().iterator().next();
		assertThat(detail.getOp()).isEqualTo(Operation.ADD);
		assertThat(detail.getObject()).isEqualTo(ROOT);
		assertThat(detail.getComponent()).isEqualTo(ObjectId.of(DOC_TYPE, STORAGE_KEY1));
	}
	
	@Test
	public void compareBranchWithNewComponent_BaseWithNewComponent() throws Exception {
		indexRevision(MAIN, new Data(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		indexRevision(branch, new Data(STORAGE_KEY2, "field1", "field2"));
		
		final RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getDetails()).hasSize(1);
		final RevisionCompareDetail detail = compare.getDetails().iterator().next();
		assertThat(detail.getOp()).isEqualTo(Operation.ADD);
		assertThat(detail.getObject()).isEqualTo(ROOT);
		assertThat(detail.getComponent()).isEqualTo(ObjectId.of(DOC_TYPE, STORAGE_KEY2));
	}
	
	@Test
	public void compareBranchWithNewComponent_BaseWithNewComponent_Reverse() throws Exception {
		indexRevision(MAIN, new Data(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		indexRevision(branch, new Data(STORAGE_KEY2, "field1", "field2"));
		
		final RevisionCompare compare = index().compare(branch, MAIN);
		assertThat(compare.getDetails()).isEmpty();
	}
	
	@Test
	public void compareChangeOnMainSinceBranchBasePoint_Reverse() throws Exception {
		final Data rev1 = new Data(STORAGE_KEY1, "field1", "field2");
		indexRevision(MAIN, rev1);
		final String branch = createBranch(MAIN, "a");
		indexRevision(branch, new Data(STORAGE_KEY2, "field1", "field2"));
		final Data rev2 = new Data(STORAGE_KEY1, "field1Changed", "field2");
		indexChange(MAIN, rev1, rev2);
		
		final RevisionCompare compare = index().compare(branch, MAIN);
		assertThat(compare.getDetails()).hasSize(1);
		final RevisionCompareDetail detail = compare.getDetails().iterator().next();
		assertThat(detail.getOp()).isEqualTo(Operation.CHANGE);
		assertThat(detail.getObject()).isEqualTo(ObjectId.of(DOC_TYPE, STORAGE_KEY1));
		assertThat(detail.getProperty()).isEqualTo("field1");
		assertThat(detail.getFromValue()).isEqualTo("field1");
		assertThat(detail.getValue()).isEqualTo("field1Changed");
	}
	
	@Test
	public void compareBranchWithChangedComponent() throws Exception {
		Data rev1 = new Data(STORAGE_KEY1, "field1", "field2");
		indexRevision(MAIN, rev1);
		final String branch = createBranch(MAIN, "a");
		Data rev2 = new Data(STORAGE_KEY1, "field1Changed", "field2");
		indexChange(branch, rev1, rev2);
		
		final RevisionCompare compare = index().compare(MAIN, branch);
		
		assertThat(compare.getDetails()).hasSize(1);
		final RevisionCompareDetail detail = compare.getDetails().iterator().next();
		assertThat(detail.getOp()).isEqualTo(Operation.CHANGE);
		assertThat(detail.getObject()).isEqualTo(ObjectId.of(DOC_TYPE, STORAGE_KEY1));
		assertThat(detail.getProperty()).isEqualTo("field1");
		assertThat(detail.getFromValue()).isEqualTo("field1");
		assertThat(detail.getValue()).isEqualTo("field1Changed");
	}
	
	@Test
	public void compareBranchWithChangedComponent_Reverse() throws Exception {
		Data rev1 = new Data(STORAGE_KEY1, "field1", "field2");
		indexRevision(MAIN, rev1);
		final String branch = createBranch(MAIN, "a");
		Data rev2 = new Data(STORAGE_KEY1, "field1Changed", "field2");
		indexChange(branch, rev1, rev2);
		
		final RevisionCompare compare = index().compare(branch, MAIN);
		assertThat(compare.getDetails()).isEmpty();
	}
	
	@Test
	public void compareBranchWithDeletedComponent() throws Exception {
		indexRevision(MAIN, new Data(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		deleteRevision(branch, Data.class, STORAGE_KEY1);
		
		final RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getDetails()).hasSize(1);
		final RevisionCompareDetail detail = compare.getDetails().iterator().next();
		assertThat(detail.getOp()).isEqualTo(Operation.REMOVE);
		assertThat(detail.getObject()).isEqualTo(ROOT);
		assertThat(detail.getComponent()).isEqualTo(ObjectId.of(DOC_TYPE, STORAGE_KEY1));
	}
	
	@Test
	public void compareBranchWithDeletedComponent_Reverse() throws Exception {
		indexRevision(MAIN, new Data(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		deleteRevision(branch, Data.class, STORAGE_KEY1);
		
		final RevisionCompare compare = index().compare(branch, MAIN);
		assertThat(compare.getDetails()).isEmpty();
	}
	
	@Test
	public void compareBranchWithRevertedChanges() throws Exception {
		Data rev1 = new Data(STORAGE_KEY1, "field1", "field2");
		indexRevision(MAIN, rev1);
		final String branch = createBranch(MAIN, "a");
		// change storageKey1 component then revert the change
		Data changed = new Data(STORAGE_KEY1, "field1", "field2Changed");
		indexChange(branch, rev1, changed);
		indexChange(branch, changed, rev1); // this actually reverts the prev. change, via a new revision

		final RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getDetails()).isEmpty();
	}
}
