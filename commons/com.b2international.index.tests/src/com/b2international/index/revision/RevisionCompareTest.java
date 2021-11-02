/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.List;

import org.junit.Test;

import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.revision.RevisionFixtures.ComponentRevisionData;
import com.b2international.index.revision.RevisionFixtures.ContainerRevisionData;
import com.b2international.index.revision.RevisionFixtures.ObjectListPropertyData;
import com.b2international.index.revision.RevisionFixtures.RevisionData;
import com.google.common.collect.ImmutableSet;

/**
 * @since 5.0
 */
public class RevisionCompareTest extends BaseRevisionIndexTest {

	private static final String DOC_TYPE = DocumentMapping.getDocType(RevisionData.class);
	private static final ObjectId ROOT = ObjectId.rootOf(DOC_TYPE);
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableSet.<Class<?>>of(RevisionData.class, ContainerRevisionData.class, ComponentRevisionData.class, ObjectListPropertyData.class);
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
		indexRevision(branch, new RevisionData(STORAGE_KEY1, "field1", "field2"));
		final RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getDetails()).hasSize(1);
		final RevisionCompareDetail detail = compare.getDetails().iterator().next();
		assertThat(detail.getOp()).isEqualTo(Operation.ADD);
		assertThat(detail.getObject()).isEqualTo(ROOT);
		assertThat(detail.getComponent()).isEqualTo(ObjectId.of(DOC_TYPE, STORAGE_KEY1));
	}
	
	@Test
	public void compareBranchWithNewComponent_BaseWithNewComponent() throws Exception {
		indexRevision(MAIN, new RevisionData(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		indexRevision(branch, new RevisionData(STORAGE_KEY2, "field1", "field2"));
		
		final RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getDetails()).hasSize(1);
		final RevisionCompareDetail detail = compare.getDetails().iterator().next();
		assertThat(detail.getOp()).isEqualTo(Operation.ADD);
		assertThat(detail.getObject()).isEqualTo(ROOT);
		assertThat(detail.getComponent()).isEqualTo(ObjectId.of(DOC_TYPE, STORAGE_KEY2));
	}
	
	@Test
	public void compareBranchWithNewComponent_BaseWithNewComponent_Reverse() throws Exception {
		indexRevision(MAIN, new RevisionData(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		indexRevision(branch, new RevisionData(STORAGE_KEY2, "field1", "field2"));
		
		final RevisionCompare compare = index().compare(branch, MAIN);
		assertThat(compare.getDetails()).isEmpty();
	}
	
	@Test
	public void compareChangeOnMainSinceBranchBasePoint_Reverse() throws Exception {
		final RevisionData rev1 = new RevisionData(STORAGE_KEY1, "field1", "field2");
		indexRevision(MAIN, rev1);
		final String branch = createBranch(MAIN, "a");
		indexRevision(branch, new RevisionData(STORAGE_KEY2, "field1", "field2"));
		final RevisionData rev2 = new RevisionData(STORAGE_KEY1, "field1Changed", "field2");
		indexChange(MAIN, rev1, rev2);
		
		final RevisionCompare compare = index().compare(branch, MAIN);
		assertThat(compare.getDetails()).containsOnly(
			RevisionCompareDetail.componentChange(Operation.CHANGE, rev2.getContainerId(), rev2.getObjectId()),
			RevisionCompareDetail.propertyChange(Operation.CHANGE, rev2.getObjectId(), "field1", "field1", "field1Changed")
		);
	}
	
	@Test
	public void compareBranchWithChangedComponent() throws Exception {
		RevisionData rev1 = new RevisionData(STORAGE_KEY1, "field1", "field2");
		indexRevision(MAIN, rev1);
		final String branch = createBranch(MAIN, "a");
		RevisionData rev2 = new RevisionData(STORAGE_KEY1, "field1Changed", "field2");
		indexChange(branch, rev1, rev2);
		
		final RevisionCompare compare = index().compare(MAIN, branch);
		
		assertThat(compare.getDetails()).containsOnly(
			RevisionCompareDetail.componentChange(Operation.CHANGE, rev2.getContainerId(), rev2.getObjectId()),
			RevisionCompareDetail.propertyChange(Operation.CHANGE, rev2.getObjectId(), "field1", "field1", "field1Changed")
		);
	}
	
	@Test
	public void compareBranchWithChangedComponent_Reverse() throws Exception {
		RevisionData rev1 = new RevisionData(STORAGE_KEY1, "field1", "field2");
		indexRevision(MAIN, rev1);
		final String branch = createBranch(MAIN, "a");
		RevisionData rev2 = new RevisionData(STORAGE_KEY1, "field1Changed", "field2");
		indexChange(branch, rev1, rev2);
		
		final RevisionCompare compare = index().compare(branch, MAIN);
		assertThat(compare.getDetails()).isEmpty();
	}
	
	@Test
	public void compareBranchWithDeletedComponent() throws Exception {
		indexRevision(MAIN, new RevisionData(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		deleteRevision(branch, RevisionData.class, STORAGE_KEY1);
		
		final RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getDetails()).hasSize(1);
		final RevisionCompareDetail detail = compare.getDetails().iterator().next();
		assertThat(detail.getOp()).isEqualTo(Operation.REMOVE);
		assertThat(detail.getObject()).isEqualTo(ROOT);
		assertThat(detail.getComponent()).isEqualTo(ObjectId.of(DOC_TYPE, STORAGE_KEY1));
	}
	
	@Test
	public void compareBranchWithDeletedComponent_Reverse() throws Exception {
		indexRevision(MAIN, new RevisionData(STORAGE_KEY1, "field1", "field2"));
		final String branch = createBranch(MAIN, "a");
		deleteRevision(branch, RevisionData.class, STORAGE_KEY1);
		
		final RevisionCompare compare = index().compare(branch, MAIN);
		assertThat(compare.getDetails()).isEmpty();
	}
	
	@Test
	public void compareBranchWithNewAndChanged() throws Exception {
		final String branch = createBranch(MAIN, "a");
		// new revision
		RevisionData rev1 = new RevisionData(STORAGE_KEY1, "field1", "field2");
		indexRevision(branch, rev1);
		// change storageKey1 component
		RevisionData changed = new RevisionData(STORAGE_KEY1, "field1", "field2Changed");
		indexChange(branch, rev1, changed);

		final RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getDetails()).hasSize(1);
		assertThat(compare.getTotalAdded()).isEqualTo(1);
		assertThat(compare.getTotalChanged()).isEqualTo(0);
		assertThat(compare.getTotalRemoved()).isEqualTo(0);
	}
	
	@Test
	public void compareBranchWithRevertedChanges() throws Exception {
		RevisionData rev1 = new RevisionData(STORAGE_KEY1, "field1", "field2");
		indexRevision(MAIN, rev1);
		final String branch = createBranch(MAIN, "a");
		// change storageKey1 component then revert the change
		RevisionData changed = new RevisionData(STORAGE_KEY1, "field1", "field2Changed");
		indexChange(branch, rev1, changed);
		indexChange(branch, changed, rev1); // this actually reverts the prev. change, via a new revision

		final RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getDetails()).containsOnly(
			RevisionCompareDetail.componentChange(Operation.CHANGE, rev1.getContainerId(), rev1.getObjectId())
		);
	}
	
	@Test
	public void compareBranchWithNewThenDeleted() throws Exception {
		final String branch = createBranch(MAIN, "a");
		indexRevision(branch, new RevisionData(STORAGE_KEY1, "field1", "field2"));
		deleteRevision(branch, RevisionData.class, STORAGE_KEY1);
		
		final RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getDetails()).isEmpty();
	}
	
	@Test
	public void compareBranchWithChangedThenDeleted() throws Exception {
		final RevisionData rev = new RevisionData(STORAGE_KEY1, "field1", "field2");
		indexRevision(MAIN, rev);
		
		final String branch = createBranch(MAIN, "a");
		RevisionData changed = new RevisionData(STORAGE_KEY1, "field1", "field2Changed");
		indexChange(branch, rev, changed);
		
		deleteRevision(branch, RevisionData.class, STORAGE_KEY1);
		
		final RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getDetails()).hasSize(1);
		assertThat(compare.getTotalAdded()).isEqualTo(0);
		assertThat(compare.getTotalChanged()).isEqualTo(0);
		assertThat(compare.getTotalRemoved()).isEqualTo(1);
	}
	
	@Test
	public void compareBranchWithChangedRootAndChildThenDeletedRootObject() throws Exception {
		final ContainerRevisionData container = new ContainerRevisionData(STORAGE_KEY1);
		final ComponentRevisionData component = new ComponentRevisionData(STORAGE_KEY2, STORAGE_KEY1, "value");
		indexRevision(MAIN, container, component);
		
		final String branch = createBranch(MAIN, "a");
		final ComponentRevisionData componentChanged = new ComponentRevisionData(STORAGE_KEY2, STORAGE_KEY1, "valueChanged");
		indexChange(branch, component, componentChanged);
		
		deleteRevision(branch, ComponentRevisionData.class, STORAGE_KEY2);
		deleteRevision(branch, ContainerRevisionData.class, STORAGE_KEY1);
		
		final RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getDetails()).hasSize(2);
		assertThat(compare.getTotalAdded()).isEqualTo(0);
		assertThat(compare.getTotalChanged()).isEqualTo(0);
		assertThat(compare.getTotalRemoved()).isEqualTo(2);
	}
	
	@Test
	public void compareBranchWithStringArrayPropertyChange() throws Exception {
		RevisionData data = new RevisionData(STORAGE_KEY1, null, null, List.of(), null);
		indexRevision(MAIN, data);
		
		String branch = createBranch(MAIN, "a");
		
		RevisionData updatedData = new RevisionData(STORAGE_KEY1, null, null, List.of("1", "2"), null);
		indexChange(branch, data, updatedData);
		
		RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getDetails()).containsOnly(
			RevisionCompareDetail.componentChange(Operation.CHANGE, data.getContainerId(), data.getObjectId()),
			RevisionCompareDetail.propertyChange(Operation.CHANGE, data.getObjectId(), "terms", "[]", "[\"1\",\"2\"]")
		);
	}
	
	@Test
	public void compareBranchWithObjectArrayPropertyChange() throws Exception {
		ObjectListPropertyData data = new ObjectListPropertyData(STORAGE_KEY1, List.of());
		indexRevision(MAIN, data);
		
		String branch = createBranch(MAIN, "a");
		
		ObjectListPropertyData updatedData = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2"),
			new RevisionFixtures.ObjectItem("field3", "field4")
		));
		indexChange(branch, data, updatedData);
		
		RevisionCompare compare = index().compare(MAIN, branch);
		assertThat(compare.getDetails()).containsOnly(
			RevisionCompareDetail.componentChange(Operation.CHANGE, data.getContainerId(), data.getObjectId()),
			RevisionCompareDetail.propertyChange(Operation.CHANGE, data.getObjectId(), "items", "[]", "[{\"field1\":\"field1\",\"field2\":\"field2\"},{\"field1\":\"field3\",\"field2\":\"field4\"}]")
		);
	}
	
}
