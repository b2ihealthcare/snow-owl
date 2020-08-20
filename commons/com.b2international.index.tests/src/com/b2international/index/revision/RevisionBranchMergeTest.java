/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.revision.RevisionBranch.BranchState;
import com.b2international.index.revision.RevisionFixtures.RevisionData;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RevisionBranchMergeTest extends BaseRevisionIndexTest {

	private static final RevisionData NEW_DATA = new RevisionData(STORAGE_KEY1, "field1", "field2");
	private static final RevisionData NEW_DATA2 = new RevisionData(STORAGE_KEY2, "field1", "field2");
	private static final RevisionData NEW_DATA3 = new RevisionData("3", "field1", "field2");
	private static final RevisionData CHANGED_DATA = new RevisionData(STORAGE_KEY1, "field1Changed", "field2");

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(RevisionData.class);
	}
	
	@Test(expected = BadRequestException.class)
	public void mergeMain() throws Exception {
		branching().prepareMerge(MAIN, MAIN).merge();
	}
	
	@Test
	public void behindStateAfterParentCommit() throws Exception {
		final String a = createBranch(MAIN, "a");
		commit(MAIN, List.of(NEW_DATA));
		assertState(a, MAIN, BranchState.BEHIND);
	}
	
	@Test
	public void mergeEmptyUpToDateBranch() throws Exception {
		String child = createBranch(MAIN, "a");
		assertState(child, MAIN, BranchState.UP_TO_DATE);
		branching().prepareMerge(child, MAIN).merge();
		assertState(child, MAIN, BranchState.UP_TO_DATE);
	}
	
	@Test
	public void rebaseUpToDateEmptyBranch() throws Exception {
		final String a = createBranch(MAIN, "a");
		assertState(a, MAIN, BranchState.UP_TO_DATE);
		branching().prepareMerge(MAIN, a).merge();
		assertState(a, MAIN, BranchState.UP_TO_DATE);
	}
	
	@Test
	public void mergeBehindBranch() throws Exception {
		String a = createBranch(MAIN, "a");
		assertState(a, MAIN, BranchState.UP_TO_DATE);
		indexRevision(MAIN, NEW_DATA);
		assertState(a, MAIN, BranchState.BEHIND);
		branching().prepareMerge(a, MAIN).merge();
		assertState(a, MAIN, BranchState.BEHIND);
	}
	
	@Test
	public void mergeExcludeNew() throws Exception {
		String child = createBranch(MAIN, "a");
		// create a revisions on child branch
		indexRevision(child, NEW_DATA);
		indexRevision(child, NEW_DATA2);
		// after commit child branch becomes FORWARD
		assertState(child, MAIN, BranchState.FORWARD);
		// do the merge with an exclusion
		branching()
			.prepareMerge(child, MAIN)
			.exclude(STORAGE_KEY1)
			.squash(true)
			.merge();
		
		// after fast-forward merge
		// 1. MAIN falls behind compared to the child
		assertState(MAIN, child, BranchState.FORWARD);
		
		// 2. Child should be UP_TO_DATE state compared to the MAIN
		assertState(child, MAIN, BranchState.BEHIND);
		
		// 3. one revision should be visible from MAIN branch, excluded one should not
		assertNotNull(getRevision(MAIN, RevisionData.class, STORAGE_KEY2));
		assertNull(getRevision(MAIN, RevisionData.class, STORAGE_KEY1));
	}
	
	@Test
	public void mergeExcludeChange() throws Exception {
		indexRevision(MAIN, NEW_DATA);
		indexRevision(MAIN, NEW_DATA2);
		String child = createBranch(MAIN, "a");
		// change a revision on the child branch
		indexChange(child, NEW_DATA, CHANGED_DATA);
		// after commit child branch becomes FORWARD
		assertState(child, MAIN, BranchState.FORWARD);
		// do the merge with an exclusion
		branching()
			.prepareMerge(child, MAIN)
			.exclude(STORAGE_KEY1)
			.squash(true)
			.merge();
		
		// after fast-forward merge
		// 1. MAIN falls behind compared to the child
		assertState(MAIN, child, BranchState.FORWARD);
		
		// 2. Child should be UP_TO_DATE state compared to the MAIN
		assertState(child, MAIN, BranchState.BEHIND);
		
		// 3. one revision should be visible from MAIN branch, excluded one should not
		assertNotNull(getRevision(MAIN, RevisionData.class, STORAGE_KEY2));
		assertEquals(getRevision(MAIN, RevisionData.class, STORAGE_KEY1), NEW_DATA);
	}
	
	@Test
	public void mergeExcludeDelete() throws Exception {
		indexRevision(MAIN, NEW_DATA);
		indexRevision(MAIN, NEW_DATA2);
		String child = createBranch(MAIN, "a");
		// change a revision on the child branch
		indexRemove(child, NEW_DATA, NEW_DATA2);
		// after commit child branch becomes FORWARD
		assertState(child, MAIN, BranchState.FORWARD);
		// do the merge with an exclusion
		branching()
			.prepareMerge(child, MAIN)
			.exclude(STORAGE_KEY1)
			.squash(true)
			.merge();
		
		// after fast-forward merge
		// 1. MAIN falls behind compared to the child
		assertState(MAIN, child, BranchState.FORWARD);
		
		// 2. Child should be UP_TO_DATE state compared to the MAIN
		assertState(child, MAIN, BranchState.BEHIND);
		
		// 3. one revision should be visible from MAIN branch, excluded one should not
		assertNotNull(getRevision(MAIN, RevisionData.class, STORAGE_KEY1));
		assertNull(getRevision(MAIN, RevisionData.class, STORAGE_KEY2));
	}
	
	@Test
	public void fastForwardBranchInForwardState() throws Exception {
		String a = createBranch(MAIN, "a");
		// create a revision on child branch
		indexRevision(a, NEW_DATA);
		// after commit child branch becomes FORWARD
		assertState(a, MAIN, BranchState.FORWARD);
		branching().prepareMerge(MAIN, a).merge();
		assertState(a, MAIN, BranchState.FORWARD);
	}
	
	@Test
	public void fastForwardMergeBranchWithNewRevisionToParent() throws Exception {
		String child = createBranch(MAIN, "a");
		// create a revision on child branch
		indexRevision(child, NEW_DATA);
		// after commit child branch becomes FORWARD
		assertState(child, MAIN, BranchState.FORWARD);
		// do the merge
		branching().prepareMerge(child, MAIN).merge();
		// after fast-forward merge
		// 1. MAIN should be in UP_TO_DATE state compared to the child
		assertState(MAIN, child, BranchState.UP_TO_DATE);
		// 2. Child should be UP_TO_DATE state compared to the MAIN
		assertState(child, MAIN, BranchState.UP_TO_DATE);
		// 3. revision should be visible from MAIN branch
		assertNotNull(getRevision(MAIN, RevisionData.class, STORAGE_KEY1));
	}
	
	@Test
	public void fastForwardMergeBranchWithChangedRevisionToParent() throws Exception {
		indexRevision(MAIN, NEW_DATA);
		String child = createBranch(MAIN, "a");
		// create a revision on child branch
		indexChange(child, NEW_DATA, CHANGED_DATA);
		branching().prepareMerge(child, MAIN).merge();
		// after merge revision should be visible from MAIN branch
		RevisionData afterMerge = getRevision(MAIN, RevisionData.class, STORAGE_KEY1);
		assertDocEquals(CHANGED_DATA, afterMerge);
	}
	
	@Test
	public void squashMergeBranchWithNewToParentWithNewNoConflict() throws Exception {
		String a = createBranch(MAIN, "a");
		indexRevision(MAIN, NEW_DATA);
		assertNotNull(getRevision(MAIN, RevisionData.class, STORAGE_KEY1));
		indexRevision(a, NEW_DATA2);
		assertNotNull(getRevision(a, RevisionData.class, STORAGE_KEY2));
		assertState(a, MAIN, BranchState.DIVERGED);
		branching().prepareMerge(a, MAIN).squash(true).merge();
		// after merge both revisions are visible from MAIN
		assertNotNull(getRevision(MAIN, RevisionData.class, STORAGE_KEY1));
		assertNotNull(getRevision(MAIN, RevisionData.class, STORAGE_KEY2));
		// Child state should be BEHIND since it lacks one commit from MAIN
		assertState(a, MAIN, BranchState.BEHIND);
		// MAIN state should be FORWARD, since it has one extra revision and commit
		assertState(MAIN, a, BranchState.FORWARD);
	}
	
	@Test
	public void rebaseBranchOnParentWithNewRevision() throws Exception {
		String a = createBranch(MAIN, "a");
		// create a revision on MAIN branch
		indexRevision(MAIN, NEW_DATA);
		// after commit on parent child state becomes BEHIND
		assertState(a, MAIN, BranchState.BEHIND);
		// do merge
		branching().prepareMerge(MAIN, a).merge();
		// after rebase revision should be visible from child branch
		assertNotNull(getRevision(a, RevisionData.class, STORAGE_KEY1));
		// and state should be UP_TO_DATE
		assertState(a, MAIN, BranchState.UP_TO_DATE);
	}
	
	@Test
	public void rebaseBranchOnParentWithChangedRevision() throws Exception {
		indexRevision(MAIN, NEW_DATA);
		String child = createBranch(MAIN, "a");
		// create a revision on child branch
		indexChange(MAIN, NEW_DATA, CHANGED_DATA);
		branching().prepareMerge(MAIN, child).merge();
		// after merge revision should be visible from MAIN branch
		RevisionData afterRebase = getRevision(child, RevisionData.class, STORAGE_KEY1);
		assertDocEquals(CHANGED_DATA, afterRebase);
	}
	
	@Test
	public void mergeBranchWithRemoveToParent() throws Exception {
		indexRevision(MAIN, NEW_DATA);
		
		String child = createBranch(MAIN, "a");
		indexRemove(child, NEW_DATA);
		
		branching().prepareMerge(child, MAIN).merge();
		
		assertNull(getRevision(MAIN, RevisionData.class, STORAGE_KEY1));
	}
	
	@Test
	public void rebaseBranchOnParentWithRemove() throws Exception {
		indexRevision(MAIN, NEW_DATA);
		
		String child = createBranch(MAIN, "a");
		indexRemove(MAIN, NEW_DATA);
		
		branching().prepareMerge(MAIN, child).merge();
		
		assertNull(getRevision(child, RevisionData.class, STORAGE_KEY1));
	}
	
	@Test
	public void rebaseDivergedBranch() throws Exception {
		final String a = createBranch(MAIN, "a");
		indexRevision(MAIN, NEW_DATA);
		indexRevision(a, NEW_DATA2);
		assertState(a, MAIN, BranchState.DIVERGED);
		assertState(MAIN, a, BranchState.DIVERGED);
		// do the rebase
		branching().prepareMerge(MAIN, a).merge();
		// both revisions are visible
		assertNotNull(getRevision(a, RevisionData.class, STORAGE_KEY1));
		assertNotNull(getRevision(a, RevisionData.class, STORAGE_KEY2));
		// task becomes forward (up to date with all changes compared to the MAIN)
		assertState(a, MAIN, BranchState.FORWARD);
		// MAIN becomes behind compared to the A branch
		assertState(MAIN, a, BranchState.BEHIND);
	}
	
	@Test
	public void rebaseDivergedWithBehindChild() throws Exception {
		final String a = createBranch(MAIN, "a");
		final String b = createBranch(a, "b");
		
		indexRevision(MAIN, NEW_DATA);
		indexRevision(a, NEW_DATA2);
		
		assertState(a, MAIN, BranchState.DIVERGED);
		assertState(b, a, BranchState.BEHIND);
		
		branching().prepareMerge(MAIN, a).merge();
		
		assertState(a, MAIN, BranchState.FORWARD);
		assertState(b, a, BranchState.BEHIND);
	}
	
	@Test
	public void rebaseBehindWithForwardChild() throws Exception {
		final String a = createBranch(MAIN, "a");
		final String b = createBranch(a, "b");
		
		indexRevision(MAIN, NEW_DATA);
		indexRevision(b, NEW_DATA2);
		
		assertState(a, MAIN, BranchState.BEHIND);
		assertState(b, a, BranchState.FORWARD);
		
		branching().prepareMerge(MAIN, a).merge();
		
		assertState(a, MAIN, BranchState.UP_TO_DATE);
		assertState(b, a, BranchState.DIVERGED);
	}
	
	@Test
	public void rebaseDivergedWithTwoChildren() throws Exception {
		final String a = createBranch(MAIN, "a");
		indexRevision(MAIN, NEW_DATA);
		
		final String b = createBranch(a, "b");
		indexRevision(a, NEW_DATA2);
		
		final String c = createBranch(a, "c");
		
		branching().prepareMerge(MAIN, a).merge();
		
		assertState(a, MAIN, BranchState.FORWARD);
		assertState(b, a, BranchState.BEHIND);
		assertState(c, a, BranchState.BEHIND);
	}
	
	@Test
	public void rebaseBehindChildOnRebasedForwardParent() throws Exception {
		rebaseDivergedWithBehindChild();
		branching().prepareMerge("MAIN/a", "MAIN/a/b").merge();
		assertState("MAIN/a/b", "MAIN/a", BranchState.UP_TO_DATE);
	}
	
	@Test
	public void mergeChildBranchThenDeleteShouldNotAffectSearches() throws Exception {
		final String branchA = createBranch(MAIN, "a");
		indexRevision(branchA, NEW_DATA);
		branching().prepareMerge(branchA, MAIN).merge();
		branching().delete(branchA);
		RevisionData rev = getRevision(MAIN, RevisionData.class, NEW_DATA.getId());
		assertNotNull(rev);
	}
	
	@Test
	public void rebaseDivergedThenMerge() throws Exception {
		final String branchA = createBranch(MAIN, "a");
		indexRevision(branchA, NEW_DATA);
		indexRevision(MAIN, NEW_DATA2);
		// rebase, revision should be visible from task
		branching().prepareMerge(MAIN, branchA).merge();
		RevisionData branchARev = getRevision(branchA, RevisionData.class, NEW_DATA.getId());
		assertNotNull(branchARev);
		assertState(branchA, MAIN, BranchState.FORWARD);
		assertState(MAIN, branchA, BranchState.BEHIND);
		
		// merge, revision should be visible from MAIN
		branching().prepareMerge(branchA, MAIN).merge();
		RevisionData mainRev = getRevision(MAIN, RevisionData.class, NEW_DATA.getId());
		assertNotNull(mainRev);
		assertState(branchA, MAIN, BranchState.BEHIND);
		assertState(MAIN, branchA, BranchState.FORWARD);
	}
	
	@Test
	public void squashMergeThenRebase() throws Exception {
		final String branchA = createBranch(MAIN, "a");
		final String branchB = createBranch(MAIN, "b");
		
		indexRevision(branchA, NEW_DATA);
		indexRevision(branchB, NEW_DATA2);

		// merge then rebase Branch B
		branching().prepareMerge(branchA, MAIN).squash(true).merge();
		branching().prepareMerge(MAIN, branchB).merge();
		
		assertNotNull(getRevision(MAIN, RevisionData.class, NEW_DATA.getId()));
		assertNotNull(getRevision(branchB, RevisionData.class, NEW_DATA.getId()));
		assertNull(getRevision(MAIN, RevisionData.class, NEW_DATA2.getId()));
		assertNotNull(getRevision(branchB, RevisionData.class, NEW_DATA2.getId()));
	}
	
	@Test
	public void rebaseDivergedThenMergeTwoBranches() throws Exception {
		final String branchA = createBranch(MAIN, "a");
		final String branchB = createBranch(MAIN, "b");
		
		
		indexRevision(branchA, NEW_DATA);
		indexRevision(branchB, NEW_DATA2);
		indexRevision(MAIN, NEW_DATA3);
		
		Hooks.PreCommitHook hook = new Hooks.PreCommitHook() {
			@Override
			public void run(StagingArea staging) {
				if (branchA.equals(staging.getBranchPath())) {
					staging.getNewObjects(RevisionData.class).forEach(newRevision -> {
						if (NEW_DATA3.getId().equals(newRevision.getId())) {
							staging.stageNew(newRevision);
						}
					});
				}
			}
		};
		index().hooks().addHook(hook);
		
		try {
			assertNull(getRevision(branchA, RevisionData.class, NEW_DATA3.getId()));
			branching().prepareMerge(MAIN, branchA).merge();
			assertNotNull(getRevision(branchA, RevisionData.class, NEW_DATA3.getId()));
			
			branching().prepareMerge(MAIN, branchB).merge();
			branching().prepareMerge(branchB, MAIN).merge();
			
			branching().prepareMerge(MAIN, branchA).merge();
			
			getRevision(branchA, RevisionData.class, NEW_DATA.getId());
			getRevision(branchA, RevisionData.class, NEW_DATA2.getId());
			getRevision(branchA, RevisionData.class, NEW_DATA3.getId());
		} finally {
			index().hooks().removeHook(hook);
		}
	}
	
	@Test
	public void rebaseThenMergeDifferentPropertyChanges() throws Exception {
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
	
	
	private void assertState(String branchPath, String compareWith, BranchState expectedState) {
		assertEquals(expectedState, branching().getBranchState(branchPath, compareWith));
	}
	
}
