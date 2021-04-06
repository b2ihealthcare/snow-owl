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
import com.b2international.index.revision.RevisionFixtures.*;
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
		return ImmutableList.<Class<?>>of(RevisionData.class, NestedRevisionData.class, ObjectPropertyData.class, ObjectListPropertyData.class, ObjectSetPropertyData.class);
	}
	
	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.setSerializationInclusion(Include.NON_NULL);
	}
	
	@Test(expected = BranchMergeConflictException.class)
	public void rebaseObjectChangeDifferentValue() throws Exception {
		indexRevision(MAIN, NEW_DATA);
		final String branchA = createBranch(MAIN, "a");
		
		indexChange(MAIN, NEW_DATA, NEW_DATA.toBuilder().field1("changedOnMain").build());
		indexChange(branchA, NEW_DATA, NEW_DATA.toBuilder().field1("changedOnBranch").build());
		
		branching().prepareMerge(MAIN, branchA).merge();
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
	
	/**
	 * @deprecated - keeping test case for the 7.x stream, but it will be removed in the 8.x stream
	 */
	@Test
	public void rebaseObjectNestedChangeWithDifferentPropertyChangeToValue_OLD() throws Exception {
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
	public void rebaseObjectNestedChangeWithRootVsNestedChange() throws Exception {
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
	public void rebaseObjectChangeSameTrackedProperty() throws Exception {
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
	public void rebaseObjectChangeSameNonTrackedProperty() throws Exception {
		indexRevision(MAIN, NEW_DATA);
		final String branchA = createBranch(MAIN, "a");
		
		// change untracked field to the same value
		RevisionData newDataWithUpdatedDerivedField = NEW_DATA.toBuilder().derivedField("derived").build();
		indexChange(MAIN, NEW_DATA, newDataWithUpdatedDerivedField);
		indexChange(branchA, NEW_DATA, newDataWithUpdatedDerivedField);
		
		// rebase should make the revision on merge source revised from the perspective of the child branch
		branching().prepareMerge(MAIN, branchA).merge();
		
		// this would fail with multiple entries exception if the rebase could not resolve the two existing revisions
		assertDocEquals(newDataWithUpdatedDerivedField, getRevision(branchA, RevisionData.class, NEW_DATA.getId()));
	}
	
	@Test
	public void rebaseObjectChangeSameTrackedPropertyMultipleRebases() throws Exception {
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
	public void rebaseObjectArrayChangeSameNonObjects() throws Exception {
		indexRevision(MAIN, NEW_DATA);
		final String branchA = createBranch(MAIN, "a");
		
		final List<String> terms = List.of("term1", "term2");
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
	public void rebaseObjectArrayChangeSameObjects() throws Exception {
		ObjectListPropertyData data = new ObjectListPropertyData(STORAGE_KEY1, List.of());
		indexRevision(MAIN, data);
		String branchA = createBranch(MAIN, "a");
		
		ObjectListPropertyData updatedData = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2"),
			new RevisionFixtures.ObjectItem("field3", "field4")
		));
		indexChange(MAIN, data, updatedData);
		indexChange(branchA, data, updatedData);
		
		branching().prepareMerge(MAIN, branchA).merge(); // should not throw BranchMergeConflictException
		
		// how ever as the Collection type List has been used in the ObjectArrayPropertyData the resulting list will have duplicates
		ObjectListPropertyData actual = getRevision(branchA, ObjectListPropertyData.class, STORAGE_KEY1);
		assertDocEquals(
			new ObjectListPropertyData(STORAGE_KEY1, List.of(
				new RevisionFixtures.ObjectItem("field1", "field2"),
				new RevisionFixtures.ObjectItem("field3", "field4"),
				new RevisionFixtures.ObjectItem("field1", "field2"),
				new RevisionFixtures.ObjectItem("field3", "field4")
			)), 
			actual
		);
	}
	
	@Test
	public void rebaseObjectArrayChangeExtraObject() throws Exception {
		ObjectListPropertyData data = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2"),
			new RevisionFixtures.ObjectItem("field3", "field4")
		));
		indexRevision(MAIN, data);
		String branchA = createBranch(MAIN, "a");
		
		ObjectListPropertyData updateOnMain = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2"),
			new RevisionFixtures.ObjectItem("field3", "field4"),
			new RevisionFixtures.ObjectItem("field5", "field6")
		));
		ObjectListPropertyData updateOnBranch = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2"),
			new RevisionFixtures.ObjectItem("field3", "field4"),
			new RevisionFixtures.ObjectItem("field7", "field8")
		));
		indexChange(MAIN, data, updateOnMain);
		indexChange(branchA, data, updateOnBranch);
		
		branching().prepareMerge(MAIN, branchA).merge(); // should not throw BranchMergeConflictException
		
		ObjectListPropertyData actual = getRevision(branchA, ObjectListPropertyData.class, STORAGE_KEY1);
		assertDocEquals(
			new ObjectListPropertyData(STORAGE_KEY1, List.of(
				new RevisionFixtures.ObjectItem("field1", "field2"),
				new RevisionFixtures.ObjectItem("field3", "field4"),
				new RevisionFixtures.ObjectItem("field5", "field6"),
				new RevisionFixtures.ObjectItem("field7", "field8")
			)), 
			actual
		);
	}
	
	@Test
	public void rebaseObjectArrayChangeRemoveSameOldItem() throws Exception {
		ObjectListPropertyData data = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2"),
			new RevisionFixtures.ObjectItem("field3", "field4")
		));
		indexRevision(MAIN, data);
		String branchA = createBranch(MAIN, "a");
		
		ObjectListPropertyData updateOnMain = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2")
		));
		ObjectListPropertyData updateOnBranch = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2")
		));
		indexChange(MAIN, data, updateOnMain);
		indexChange(branchA, data, updateOnBranch);
		
		branching().prepareMerge(MAIN, branchA).merge(); // should not throw BranchMergeConflictException
		
		ObjectListPropertyData actual = getRevision(branchA, ObjectListPropertyData.class, STORAGE_KEY1);
		assertDocEquals(
			new ObjectListPropertyData(STORAGE_KEY1, List.of(
				new RevisionFixtures.ObjectItem("field1", "field2")
			)), 
			actual
		);
	}
	
	@Test
	public void rebaseObjectArrayChangeRemoveDifferentOldItem() throws Exception {
		ObjectListPropertyData data = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2"),
			new RevisionFixtures.ObjectItem("field3", "field4")
		));
		indexRevision(MAIN, data);
		String branchA = createBranch(MAIN, "a");
		
		ObjectListPropertyData updateOnMain = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field3", "field4")
		));
		ObjectListPropertyData updateOnBranch = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2")
		));
		indexChange(MAIN, data, updateOnMain);
		indexChange(branchA, data, updateOnBranch);
		
		branching().prepareMerge(MAIN, branchA).merge(); // should not throw BranchMergeConflictException
		
		ObjectListPropertyData actual = getRevision(branchA, ObjectListPropertyData.class, STORAGE_KEY1);
		assertDocEquals(
			new ObjectListPropertyData(STORAGE_KEY1, List.of()), 
			actual
		);
	}
	
	@Test
	public void rebaseObjectArrayChangeReplaceSameItemWithSameItem() throws Exception {
		ObjectListPropertyData data = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2"),
			new RevisionFixtures.ObjectItem("field3", "field4")
		));
		indexRevision(MAIN, data);
		String branchA = createBranch(MAIN, "a");
		
		ObjectListPropertyData updateOnMain = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2"),
			new RevisionFixtures.ObjectItem("field5", "field6")
		));
		ObjectListPropertyData updateOnBranch = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2"),
			new RevisionFixtures.ObjectItem("field5", "field6")
		));
		indexChange(MAIN, data, updateOnMain);
		indexChange(branchA, data, updateOnBranch);
		
		branching().prepareMerge(MAIN, branchA).merge(); // should not throw BranchMergeConflictException
		
		ObjectListPropertyData actual = getRevision(branchA, ObjectListPropertyData.class, STORAGE_KEY1);
		assertDocEquals(
			new ObjectListPropertyData(STORAGE_KEY1, List.of(
				new RevisionFixtures.ObjectItem("field1", "field2"),
				new RevisionFixtures.ObjectItem("field5", "field6"),
				new RevisionFixtures.ObjectItem("field5", "field6")
			)), 
			actual
		);
	}
	
	@Test
	public void rebaseObjectArrayChangeReplaceSameItemWithDifferentItem() throws Exception {
		ObjectListPropertyData data = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2"),
			new RevisionFixtures.ObjectItem("field3", "field4")
		));
		indexRevision(MAIN, data);
		String branchA = createBranch(MAIN, "a");
		
		ObjectListPropertyData updateOnMain = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2"),
			new RevisionFixtures.ObjectItem("field5", "field6")
		));
		ObjectListPropertyData updateOnBranch = new ObjectListPropertyData(STORAGE_KEY1, List.of(
			new RevisionFixtures.ObjectItem("field1", "field2"),
			new RevisionFixtures.ObjectItem("field7", "field8")
		));
		indexChange(MAIN, data, updateOnMain);
		indexChange(branchA, data, updateOnBranch);
		
		branching().prepareMerge(MAIN, branchA).merge(); // should not throw BranchMergeConflictException
		
		ObjectListPropertyData actual = getRevision(branchA, ObjectListPropertyData.class, STORAGE_KEY1);
		assertDocEquals(
			new ObjectListPropertyData(STORAGE_KEY1, List.of(
				new RevisionFixtures.ObjectItem("field1", "field2"),
				new RevisionFixtures.ObjectItem("field5", "field6"),
				new RevisionFixtures.ObjectItem("field7", "field8")
			)), 
			actual
		);
	}
	
	@Test
	public void rebaseObjectArrayChangeNonObjectsEmptied() throws Exception {
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
	public void rebaseObjectSetSameChanges() throws Exception {
		ObjectSetPropertyData data = new ObjectSetPropertyData(STORAGE_KEY1, Set.of());
		indexRevision(MAIN, data);
		String branchA = createBranch(MAIN, "a");
		
		ObjectSetPropertyData updatedData = new ObjectSetPropertyData(STORAGE_KEY1, Set.of(
			new RevisionFixtures.ObjectItem("field1", "field2"),
			new RevisionFixtures.ObjectItem("field3", "field4")
		));
		indexChange(MAIN, data, updatedData);
		indexChange(branchA, data, updatedData);
		
		branching().prepareMerge(MAIN, branchA).merge(); // should not throw BranchMergeConflictException
		
		// how ever as the Collection type List has been used in the ObjectArrayPropertyData the resulting list will have duplicates
		ObjectSetPropertyData actual = getRevision(branchA, ObjectSetPropertyData.class, STORAGE_KEY1);
		assertDocEquals(
			new ObjectSetPropertyData(STORAGE_KEY1, Set.of(
				new RevisionFixtures.ObjectItem("field1", "field2"),
				new RevisionFixtures.ObjectItem("field3", "field4")
			)), 
			actual
		);
	}
	
	@Test
	public void rebaseObjectNestedChangeToSameValueOnBothSides() throws Exception {
		ObjectPropertyData data = new ObjectPropertyData(STORAGE_KEY1, null);
		indexRevision(MAIN, data);
		String branchA = createBranch(MAIN, "a");
		
		ObjectPropertyData update = new ObjectPropertyData(STORAGE_KEY1, new RevisionFixtures.ObjectItem("field1", "field2"));
		
		indexChange(MAIN, data, update);
		indexChange(branchA, data, update);
		
		branching().prepareMerge(MAIN, branchA).merge(); // should not throw BranchMergeConflictException
		
		ObjectPropertyData actual = getRevision(branchA, ObjectPropertyData.class, STORAGE_KEY1);
		assertDocEquals(
			new ObjectPropertyData(STORAGE_KEY1, new RevisionFixtures.ObjectItem("field1", "field2")), 
			actual
		);
	}
	
	@Test
	public void rebaseObjectNestedChangeToNullOnBothSides() throws Exception {
		ObjectPropertyData data = new ObjectPropertyData(STORAGE_KEY1, new RevisionFixtures.ObjectItem("field1", "field2"));
		indexRevision(MAIN, data);
		String branchA = createBranch(MAIN, "a");
		
		ObjectPropertyData update = new ObjectPropertyData(STORAGE_KEY1, null);
		
		indexChange(MAIN, data, update);
		indexChange(branchA, data, update);
		
		branching().prepareMerge(MAIN, branchA).merge(); // should not throw BranchMergeConflictException
		
		ObjectPropertyData actual = getRevision(branchA, ObjectPropertyData.class, STORAGE_KEY1);
		assertDocEquals(
			new ObjectPropertyData(STORAGE_KEY1, null), 
			actual
		);
	}
	
	@Test(expected = BranchMergeConflictException.class)
	public void rebaseObjectNestedChangeToDifferentValues() throws Exception {
		ObjectPropertyData data = new ObjectPropertyData(STORAGE_KEY1, null);
		indexRevision(MAIN, data);
		String branchA = createBranch(MAIN, "a");
		
		ObjectPropertyData updateOnMain = new ObjectPropertyData(STORAGE_KEY1, new RevisionFixtures.ObjectItem("field1", "field2"));
		indexChange(MAIN, data, updateOnMain);
		
		ObjectPropertyData updateOnBranch = new ObjectPropertyData(STORAGE_KEY1, new RevisionFixtures.ObjectItem("field2", "field1"));
		indexChange(branchA, data, updateOnBranch);
		
		branching().prepareMerge(MAIN, branchA).merge();
	}
	
	@Test
	public void rebaseObjectNestedChangeWithDifferentPropertyChangeToValue() throws Exception {
		ObjectPropertyData data = new ObjectPropertyData(STORAGE_KEY1, null);
		indexRevision(MAIN, data);
		String branchA = createBranch(MAIN, "a");
		
		ObjectPropertyData updateOnMain = new ObjectPropertyData(STORAGE_KEY1, new RevisionFixtures.ObjectItem("field1", null));
		ObjectPropertyData updateOnBranch = new ObjectPropertyData(STORAGE_KEY1, new RevisionFixtures.ObjectItem(null, "field2"));
		
		indexChange(MAIN, data, updateOnMain);
		indexChange(branchA, data, updateOnBranch);
		
		branching().prepareMerge(MAIN, branchA).merge(); // should not throw BranchMergeConflictException
		
		ObjectPropertyData actual = getRevision(branchA, ObjectPropertyData.class, STORAGE_KEY1);
		assertDocEquals(
			new ObjectPropertyData(STORAGE_KEY1, new RevisionFixtures.ObjectItem("field1", "field2")), 
			actual
		);
	}
	
	@Test
	public void rebaseObjectNestedChangeWithDifferentPropertyChangeToNull() throws Exception {
		ObjectPropertyData data = new ObjectPropertyData(STORAGE_KEY1, new RevisionFixtures.ObjectItem("field1", "field2"));
		indexRevision(MAIN, data);
		String branchA = createBranch(MAIN, "a");
		
		ObjectPropertyData updateOnMain = new ObjectPropertyData(STORAGE_KEY1, new RevisionFixtures.ObjectItem("field1", null ));
		ObjectPropertyData updateOnBranch = new ObjectPropertyData(STORAGE_KEY1, new RevisionFixtures.ObjectItem(null, "field2"));
		
		indexChange(MAIN, data, updateOnMain);
		indexChange(branchA, data, updateOnBranch);
		
		branching().prepareMerge(MAIN, branchA).merge(); // should not throw BranchMergeConflictException
		
		ObjectPropertyData actual = getRevision(branchA, ObjectPropertyData.class, STORAGE_KEY1);
		assertDocEquals(
			new ObjectPropertyData(STORAGE_KEY1, new RevisionFixtures.ObjectItem(null, null)), // TODO should we expect null here, or just a merged fully emptied object
			actual
		);
	}
	
}
