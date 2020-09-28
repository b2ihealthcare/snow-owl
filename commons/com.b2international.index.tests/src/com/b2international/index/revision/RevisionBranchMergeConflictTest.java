/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.revision.RevisionFixtures.NestedRevisionData;
import com.b2international.index.revision.RevisionFixtures.RevisionData;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.10
 */
public class RevisionBranchMergeConflictTest extends BaseRevisionIndexTest {

	private static final RevisionData NEW_DATA = new RevisionData(STORAGE_KEY1, "field1", "field2");
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(RevisionData.class, NestedRevisionData.class);
	}
	
	@Test
	public void rebaseThenMergeResolvableSingleValuedPropertyChanges() throws Exception {
		indexRevision(MAIN, NEW_DATA);
		final String branchA = createBranch(MAIN, "a");
		
		indexChange(MAIN, NEW_DATA, NEW_DATA.toBuilder().field1("field1Changed").build());
		indexChange(branchA, NEW_DATA, NEW_DATA.toBuilder().field2("field2Changed").build());
		
		branching().prepareMerge(MAIN, branchA).merge();
		RevisionData mainRevision = getRevision(MAIN, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1Changed", "field2"), mainRevision);
		RevisionData branchARevision = getRevision(branchA, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1Changed", "field2Changed"), branchARevision);
		
		branching().prepareMerge(branchA, MAIN).squash(true).merge();
		mainRevision = getRevision(MAIN, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1Changed", "field2Changed"), mainRevision);
		branchARevision = getRevision(branchA, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1Changed", "field2Changed"), branchARevision);
	}
	
	@Test
	public void rebaseThenMergeResolvableMultiValuedPropertyChanges() throws Exception {
		indexRevision(MAIN, NEW_DATA);
		final String branchA = createBranch(MAIN, "a");
		
		final List<String> terms = List.of("term1", "term2");
		indexChange(MAIN, NEW_DATA, NEW_DATA.toBuilder().terms(terms).build());
		indexChange(branchA, NEW_DATA, NEW_DATA.toBuilder().field1("field1Changed").build());
		
		branching().prepareMerge(MAIN, branchA).merge();
		RevisionData mainRevision = getRevision(MAIN, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1", "field2", terms), mainRevision);
		RevisionData branchARevision = getRevision(branchA, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1Changed", "field2", terms), branchARevision);
		
		branching().prepareMerge(branchA, MAIN).squash(true).merge();
		mainRevision = getRevision(MAIN, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1Changed", "field2", terms), mainRevision);
		branchARevision = getRevision(branchA, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1Changed", "field2", terms), branchARevision);
	}
	
	@Test
	public void rebaseResolvableConflictSingleValuedProperty() throws Exception {
		indexRevision(MAIN, NEW_DATA);
		final String branchA = createBranch(MAIN, "a");
		
		indexChange(MAIN, NEW_DATA, NEW_DATA.toBuilder().field1("field1Changed").build());
		indexChange(branchA, NEW_DATA, NEW_DATA.toBuilder().field1("field1Changed").build());
		
		branching().prepareMerge(MAIN, branchA).merge();
		RevisionData mainRevision = getRevision(MAIN, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1Changed", "field2"), mainRevision);
		RevisionData branchARevision = getRevision(branchA, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1Changed", "field2"), branchARevision);
		
		branching().prepareMerge(branchA, MAIN).squash(true).merge();
		mainRevision = getRevision(MAIN, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1Changed", "field2"), mainRevision);
		branchARevision = getRevision(branchA, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1Changed", "field2"), branchARevision);
	}
	
	@Test
	public void rebaseResolvableConflictMultiValuedProperty() throws Exception {
		final List<String> terms = List.of("term1", "term2");
		
		indexRevision(MAIN, NEW_DATA);
		final String branchA = createBranch(MAIN, "a");
		
		indexChange(MAIN, NEW_DATA, NEW_DATA.toBuilder().terms(terms).build());
		indexChange(branchA, NEW_DATA, NEW_DATA.toBuilder().terms(terms).build());
		
		branching().prepareMerge(MAIN, branchA).merge();
		RevisionData mainRevision = getRevision(MAIN, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1", "field2", List.of("term1", "term2")), mainRevision);
		RevisionData branchARevision = getRevision(branchA, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1", "field2", List.of("term1", "term2")), branchARevision);
		
		branching().prepareMerge(branchA, MAIN).squash(true).merge();
		mainRevision = getRevision(MAIN, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1", "field2", List.of("term1", "term2")), mainRevision);
		branchARevision = getRevision(branchA, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(new RevisionData(NEW_DATA.getId(), "field1", "field2", List.of("term1", "term2")), branchARevision);
	}
	
	@Test
	public void rebaseResolvableNestedRevisionDataChanges_NestedObjectChange() throws Exception {
		final Data nestedData = new Data();
		nestedData.setField1("field1_1");
		nestedData.setField2("field2_1");
		final NestedRevisionData doc = new NestedRevisionData(STORAGE_KEY1, "parent1", nestedData);
		
		indexRevision(MAIN, doc);
		
		String a = createBranch(MAIN, "a");
		
		final Data updatedNestedDataOnChild = new Data();
		updatedNestedDataOnChild.setField1("field1_2");
		updatedNestedDataOnChild.setField2("field2_1");
		final NestedRevisionData updatedOnChild = new NestedRevisionData(STORAGE_KEY1, "parent1", updatedNestedDataOnChild);
		indexChange(a, doc, updatedOnChild);
		
		final Data updatedNestedDataOnParent = new Data();
		updatedNestedDataOnParent.setField1("field1_1");
		updatedNestedDataOnParent.setField2("field2_2");
		final NestedRevisionData updatedOnParent = new NestedRevisionData(STORAGE_KEY1, "parent1", updatedNestedDataOnParent);
		indexChange(MAIN, doc, updatedOnParent);
		
		// rebase should be able to merge the two non-conflicting changes
		branching().prepareMerge(MAIN, a).merge();
		
		NestedRevisionData latestOnChild = getRevision(a, NestedRevisionData.class, STORAGE_KEY1);
		
		final Data expectedNestedDataOnChildAfterRebase = new Data();
		expectedNestedDataOnChildAfterRebase.setField1("field1_2");
		expectedNestedDataOnChildAfterRebase.setField2("field2_2");
		final NestedRevisionData expectedOnChildAfterRebase = new NestedRevisionData(STORAGE_KEY1, "parent1", expectedNestedDataOnChildAfterRebase);
		
		assertDocEquals(expectedOnChildAfterRebase, latestOnChild);
		
	}
	
	@Test
	public void rebaseResolvableNestedRevisionDataChanges_SetObject() throws Exception {
		final NestedRevisionData doc = new NestedRevisionData(STORAGE_KEY1, "parent1", null);
		indexRevision(MAIN, doc);
		
		String a = createBranch(MAIN, "a");
		final NestedRevisionData updatedOnChild = new NestedRevisionData(STORAGE_KEY1, "parent2", null);
		indexChange(a, doc, updatedOnChild);
		
		final Data nestedData = new Data();
		nestedData.setField1("field1_1");
		nestedData.setField2("field2_1");
		final NestedRevisionData updatedOnParent = new NestedRevisionData(STORAGE_KEY1, "parent1", nestedData);
		indexChange(MAIN, doc, updatedOnParent);
		
		// rebase should be able to merge the two non-conflicting changes
		branching().prepareMerge(MAIN, a).merge();
		
		NestedRevisionData latestOnChild = getRevision(a, NestedRevisionData.class, STORAGE_KEY1);
		
		final Data expectedNestedDataOnChildAfterRebase = new Data();
		expectedNestedDataOnChildAfterRebase.setField1("field1_1");
		expectedNestedDataOnChildAfterRebase.setField2("field2_1");
		final NestedRevisionData expectedOnChildAfterRebase = new NestedRevisionData(STORAGE_KEY1, "parent2", expectedNestedDataOnChildAfterRebase);
		
		assertDocEquals(expectedOnChildAfterRebase, latestOnChild);
	}
	
}
