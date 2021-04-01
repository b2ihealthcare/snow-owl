/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Set;

import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.revision.RevisionFixtures.NestedRevisionData;
import com.b2international.index.revision.RevisionFixtures.ObjectArrayPropertyData;
import com.b2international.index.revision.RevisionFixtures.ObjectSetPropertyData;
import com.b2international.index.revision.RevisionFixtures.RevisionData;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.10
 */
public class RevisionBranchMergeConflictTest extends BaseRevisionIndexTest {

	private static final RevisionData NEW_DATA = new RevisionData(STORAGE_KEY1, "field1", "field2");
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(RevisionData.class, NestedRevisionData.class, ObjectArrayPropertyData.class, ObjectSetPropertyData.class);
	}
	
	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.setSerializationInclusion(Include.NON_NULL);
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
		assertDocEquals(NEW_DATA.toBuilder().terms(terms).build(), mainRevision);
		RevisionData branchARevision = getRevision(branchA, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(NEW_DATA.toBuilder().field1("field1Changed").terms(terms).build(), branchARevision);
		
		branching().prepareMerge(branchA, MAIN).squash(true).merge();
		mainRevision = getRevision(MAIN, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(NEW_DATA.toBuilder().field1("field1Changed").terms(terms).build(), mainRevision);
		branchARevision = getRevision(branchA, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(NEW_DATA.toBuilder().field1("field1Changed").terms(terms).build(), branchARevision);
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
		
		RevisionData newDataWithUpdatedTerms = NEW_DATA.toBuilder().terms(terms).build();
		indexChange(MAIN, NEW_DATA, newDataWithUpdatedTerms);
		indexChange(branchA, NEW_DATA, newDataWithUpdatedTerms);
		
		branching().prepareMerge(MAIN, branchA).merge();
		RevisionData mainRevision = getRevision(MAIN, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(newDataWithUpdatedTerms, mainRevision);
		RevisionData branchARevision = getRevision(branchA, RevisionData.class, NEW_DATA.getId());
		RevisionData mergedData = NEW_DATA.toBuilder().terms(List.of("term1", "term2", "term1", "term2")).build();
		assertDocEquals(mergedData, branchARevision);
		
		branching().prepareMerge(branchA, MAIN).squash(true).merge();
		mainRevision = getRevision(MAIN, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(mergedData, mainRevision);
		branchARevision = getRevision(branchA, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(mergedData, branchARevision);
	}
	
	@Test
	public void rebaseResolvableConflictMultiValuedPropertyNonNullFromValue() throws Exception {
		final List<String> terms = List.of("term1", "term2");
		
		indexRevision(MAIN, NEW_DATA.toBuilder().terms(List.of()).build());
		final String branchA = createBranch(MAIN, "a");
		
		RevisionData newDataWithUpdatedTerms = NEW_DATA.toBuilder().terms(terms).build();
		indexChange(MAIN, NEW_DATA, newDataWithUpdatedTerms);
		indexChange(branchA, NEW_DATA, newDataWithUpdatedTerms);
		
		branching().prepareMerge(MAIN, branchA).merge();
		RevisionData mainRevision = getRevision(MAIN, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(newDataWithUpdatedTerms, mainRevision);
		RevisionData branchARevision = getRevision(branchA, RevisionData.class, NEW_DATA.getId());
		RevisionData mergedData = NEW_DATA.toBuilder().terms(List.of("term1", "term2", "term1", "term2")).build();
		assertDocEquals(mergedData, branchARevision);
		
		branching().prepareMerge(branchA, MAIN).squash(true).merge();
		mainRevision = getRevision(MAIN, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(mergedData, mainRevision);
		branchARevision = getRevision(branchA, RevisionData.class, NEW_DATA.getId());
		assertDocEquals(mergedData, branchARevision);
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
	
	@Test
	public void rebaseSameChanges() throws Exception {
		indexRevision(MAIN, NEW_DATA);
		final String branchA = createBranch(MAIN, "a");
		
		RevisionData newDataWithUpdatedDerivedField = NEW_DATA.toBuilder().derivedField("derived").build();
		indexChange(MAIN, NEW_DATA, newDataWithUpdatedDerivedField);
		indexChange(branchA, NEW_DATA, newDataWithUpdatedDerivedField);
		
		// rebase should make the revision on merge source revised from the perspective of the child branch
		branching().prepareMerge(MAIN, branchA).merge();
		
		// this would fail with multiple entries exception if the rebase could not resolve the two existing revisions
		assertDocEquals(newDataWithUpdatedDerivedField, getRevision(branchA, RevisionData.class, NEW_DATA.getId()));
	}
	
	@Test
	public void rebaseResolvableChangesShouldNotCauseConflictOnSecondRebase() throws Exception {
		indexRevision(MAIN, NEW_DATA);
		final String branchA = createBranch(MAIN, "a");
		
		indexChange(MAIN, NEW_DATA, NEW_DATA.toBuilder().field1("field1Changed").build());
		indexChange(branchA, NEW_DATA, NEW_DATA.toBuilder().field2("field2Changed").build());
		
		// rebase branch the first time should make it through
		branching().prepareMerge(MAIN, branchA).merge();
		
		// trigger another change on the same property on the parent branch 
		indexChange(MAIN, NEW_DATA, NEW_DATA.toBuilder().field1("field1Changed_2").build());
		
		// trigger second rebase causes issues in 7.11.0, but with patch it's not
		branching().prepareMerge(MAIN, branchA).merge(); // throws BranchMergeConflictException in 7.11.0 and earlier versions
	}
	
	@Test
	public void rebaseSameObjectArrayChanges() throws Exception {
		ObjectArrayPropertyData data = new ObjectArrayPropertyData(STORAGE_KEY1, List.of());
		indexRevision(MAIN, data);
		String branchA = createBranch(MAIN, "a");
		
		ObjectArrayPropertyData updatedData = new ObjectArrayPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectArrayPropertyItem("field1", "field2"),
			new RevisionFixtures.ObjectArrayPropertyItem("field3", "field4")
		));
		indexChange(MAIN, data, updatedData);
		indexChange(branchA, data, updatedData);
		
		branching().prepareMerge(MAIN, branchA).merge(); // should not throw BranchMergeConflictException
		
		// how ever as the Collection type List has been used in the ObjectArrayPropertyData the resulting list will have duplicates
		ObjectArrayPropertyData actual = getRevision(branchA, ObjectArrayPropertyData.class, STORAGE_KEY1);
		assertDocEquals(
			new ObjectArrayPropertyData(STORAGE_KEY1, List.of(
				new RevisionFixtures.ObjectArrayPropertyItem("field1", "field2"),
				new RevisionFixtures.ObjectArrayPropertyItem("field3", "field4"),
				new RevisionFixtures.ObjectArrayPropertyItem("field1", "field2"),
				new RevisionFixtures.ObjectArrayPropertyItem("field3", "field4")
			)), 
			actual
		);
	}
	
	@Test
	public void rebaseSameObjectSetChanges() throws Exception {
		ObjectSetPropertyData data = new ObjectSetPropertyData(STORAGE_KEY1, Set.of());
		indexRevision(MAIN, data);
		String branchA = createBranch(MAIN, "a");
		
		ObjectSetPropertyData updatedData = new ObjectSetPropertyData(STORAGE_KEY1, Set.of(
			new RevisionFixtures.ObjectArrayPropertyItem("field1", "field2"),
			new RevisionFixtures.ObjectArrayPropertyItem("field3", "field4")
		));
		indexChange(MAIN, data, updatedData);
		indexChange(branchA, data, updatedData);
		
		branching().prepareMerge(MAIN, branchA).merge(); // should not throw BranchMergeConflictException
		
		// how ever as the Collection type List has been used in the ObjectArrayPropertyData the resulting list will have duplicates
		ObjectSetPropertyData actual = getRevision(branchA, ObjectSetPropertyData.class, STORAGE_KEY1);
		assertDocEquals(
			new ObjectSetPropertyData(STORAGE_KEY1, Set.of(
				new RevisionFixtures.ObjectArrayPropertyItem("field1", "field2"),
				new RevisionFixtures.ObjectArrayPropertyItem("field3", "field4")
			)), 
			actual
		);
	}
	
	@Test
	public void rebaseObjectItemArrayChangeWithOneExtraElementOnEachSide() throws Exception {
		ObjectArrayPropertyData data = new ObjectArrayPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectArrayPropertyItem("field1", "field2"),
			new RevisionFixtures.ObjectArrayPropertyItem("field3", "field4")
		));
		indexRevision(MAIN, data);
		String branchA = createBranch(MAIN, "a");
		
		ObjectArrayPropertyData updateOnMain = new ObjectArrayPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectArrayPropertyItem("field1", "field2"),
			new RevisionFixtures.ObjectArrayPropertyItem("field3", "field4"),
			new RevisionFixtures.ObjectArrayPropertyItem("field5", "field6")
		));
		ObjectArrayPropertyData updateOnBranch = new ObjectArrayPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectArrayPropertyItem("field1", "field2"),
			new RevisionFixtures.ObjectArrayPropertyItem("field3", "field4"),
			new RevisionFixtures.ObjectArrayPropertyItem("field7", "field8")
		));
		indexChange(MAIN, data, updateOnMain);
		indexChange(branchA, data, updateOnBranch);
		
		branching().prepareMerge(MAIN, branchA).merge(); // should not throw BranchMergeConflictException
		
		ObjectArrayPropertyData actual = getRevision(branchA, ObjectArrayPropertyData.class, STORAGE_KEY1);
		assertDocEquals(
			new ObjectArrayPropertyData(STORAGE_KEY1, List.of(
				new RevisionFixtures.ObjectArrayPropertyItem("field1", "field2"),
				new RevisionFixtures.ObjectArrayPropertyItem("field3", "field4"),
				new RevisionFixtures.ObjectArrayPropertyItem("field5", "field6"),
				new RevisionFixtures.ObjectArrayPropertyItem("field7", "field8")
			)), 
			actual
		);
	}
	
}
