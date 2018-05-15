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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.commons.options.Metadata;
import com.b2international.commons.options.MetadataImpl;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionBranch.BranchState;

/**
 * @since 6.5
 */
public class RevisionBranchingTest extends BaseRevisionIndexTest {

	@Test
	public void afterInit() throws Exception {
		RevisionBranch main = getMainBranch();
		assertNotNull(main);
		assertThat(main.getPath()).isEqualTo(MAIN);
		assertThat(main.getName()).isEqualTo(MAIN);
		assertThat(main.getParentPath()).isEqualTo("");
		assertThat(main.getBaseTimestamp()).isEqualTo(main.getHeadTimestamp());
		assertThat(branching().getBranchState(MAIN)).isEqualTo(BranchState.UP_TO_DATE);
	}

	@Test
	public void commitUpdatesHeadTimestamp() throws Exception {
		long timestamp = commit(MAIN, Collections.emptyMap());
		assertThat(getMainBranch().getHeadTimestamp()).isEqualTo(timestamp);
		assertThat(branching().getBranchState(MAIN)).isEqualTo(BranchState.UP_TO_DATE);
	}
	
	@Test(expected = BadRequestException.class)
	public void deleteMainIsNotPossible() throws Exception {
		branching().delete(MAIN);
	}
	
	@Test(expected = BadRequestException.class)
	public void createBranchWithEmptyName() throws Exception {
		createBranch(MAIN, "");
	}
	
	@Test(expected = BadRequestException.class)
	public void createBranchWithPath() throws Exception {
		createBranch(MAIN, "a/b");
	}
	
	@Test(expected = BadRequestException.class)
	public void createBranchWithInvalidChars() throws Exception {
		createBranch(MAIN, "?a");
	}
	
	@Test(expected = BadRequestException.class)
	public void createBranchWithTooLongName() throws Exception {
		createBranch(MAIN, "123456789012345678901234567890123456789012345678901");
	}
	
	@Test
	public void createBranchWith50CharName() throws Exception {
		final String name = "12345678901234567890123456789012345678901234567890";
		final String path = createBranch(MAIN, name);
		assertNotNull(branching().getBranch(path));
		assertThat(branching().getBranchState(path)).isEqualTo(BranchState.UP_TO_DATE);
	}
	
	@Test
	public void forwardStateAfterCommit() throws Exception {
		final String path = createBranch(MAIN, "a");
		commit(path, Collections.emptyMap());
		assertThat(branching().getBranchState(path)).isEqualTo(BranchState.FORWARD);
	}
	
	@Test
	public void behindStateAfterParentCommit() throws Exception {
		final String path = createBranch(MAIN, "a");
		commit(MAIN, Collections.emptyMap());
		assertState(path, BranchState.BEHIND);
	}
	
	@Test
	public void divergedStateAfterParentAndBranchCommit() throws Exception {
		final String path = createBranch(MAIN, "a");
		commit(MAIN, Collections.emptyMap());
		commit(path, Collections.emptyMap());
		assertThat(branching().getBranchState(path)).isEqualTo(BranchState.DIVERGED);
	}
	
	@Test(expected = BadRequestException.class)
	public void rebaseMain() throws Exception {
		branching().rebase(MAIN, MAIN, "Message", () -> {});
	}
	
	@Test(expected = BadRequestException.class)
	public void mergeMain() throws Exception {
		branching().merge(MAIN, MAIN, "Message");
	}
	
	@Test(expected = NotFoundException.class)
	public void whenGettingNonExistingBranch_ThenThrowNotFoundException() throws Exception {
		branching().getBranch("MAIN/nonexistent");
	}
	
	@Test
	public void whenCreatingDeepBranchHierarchy_ThenEachSegmentShouldBeCreatedAndStoredInBranchManager() throws Exception {
		final String abcdPath = createBranch(createBranch(createBranch(createBranch(MAIN, "a"), "b"), "c"), "d");
		assertEquals("MAIN/a/b/c/d", abcdPath);
		final RevisionBranch abcd = getBranch(abcdPath);
		final RevisionBranch abc = getBranch(abcd.getParentPath());
		final RevisionBranch ab = getBranch(abc.getParentPath());
		final RevisionBranch a = getBranch(ab.getParentPath());
		assertEquals(MAIN, getBranch(a.getParentPath()).getPath());
		assertThat(branching().getChildren(MAIN).stream().map(RevisionBranch::getPath).collect(Collectors.toSet())).containsOnly(a.getPath(), ab.getPath(), abc.getPath(), abcd.getPath());
	}
	
	@Test
	public void whenCreatingThreeBranches_ThenManagerShouldReturnAllOfThemInGetAll() throws Exception {
		final String b = createBranch(MAIN, "b");
		final String c = createBranch(MAIN, "c");
		final Collection<String> branches = branching().search(Query.select(RevisionBranch.class).where(Expressions.matchAll()).build()).stream().map(RevisionBranch::getPath).collect(Collectors.toSet());
		assertThat(branches).containsOnly(MAIN, b, c);
	}
	
	@Test
	public void whenCreatingBranchWithMetadata_ThenItShouldBeStored() throws Exception {
		final Metadata metadata = new MetadataImpl();
		metadata.put("key", "value");
		final String b = branching().createBranch(MAIN, "b", metadata);
		assertEquals("value", getBranch(b).metadata().get("key"));
	}
	
	@Test
	public void whenDeletingBranch_ThenManagerShouldStillReturnIt() throws Exception {
		branching().delete(createBranch(MAIN, "a"));
		assertTrue(getBranch("MAIN/a").isDeleted());
	}
	
	@Test(expected = BadRequestException.class)
	public void whenCreatingChildUnderDeletedBranch_ThenThrowBadRequestException() throws Exception {
		branching().delete(createBranch(MAIN, "a"));
		createBranch("MAIN/a", "childOfDeletedA");
	}
	
	@Test
	public void whenDeletingBranch_ChildBranchesShouldBeDeletedAsWell() throws Exception {
		String a = createBranch(MAIN, "a");
		for (int i = 0; i < 10; i++) {
			createBranch(a, ""+i);
		}
		
		branching().delete(a);
		
		for (int i = 0; i < 10; i++) {
			assertTrue(getBranch("MAIN/a/"+i).isDeleted());
		}
	}
	
	@Test
	public void whenDeletingBranchWithChildTree_ThenChildTreeShouldBeDeleted() throws Exception {
		String a = createBranch(MAIN, "a");
		String a1 = createBranch(a, "1");
		createBranch(a1, "2");
		branching().delete(a);
		assertTrue(getBranch("MAIN/a/1").isDeleted());
		assertTrue(getBranch("MAIN/a/1/2").isDeleted());
	}
	
	@Test(expected = BranchMergeException.class)
	public void mergeUpToDate() throws Exception {
		String a = createBranch(MAIN, "a");
		branching().merge(a, MAIN, "Merge A to MAIN");
	}
	
	@Test
	public void mergeForward() throws Exception {
		RevisionBranch originalMain = getMainBranch();
		String a = createBranch(MAIN, "a");
		branching().handleCommit(a, currentTime());
		branching().merge(a, MAIN, "Merge A to MAIN");
		RevisionBranch mainA = getMainBranch();
		assertTrue(mainA.getHeadTimestamp() > originalMain.getHeadTimestamp());
		assertState(a, BranchState.UP_TO_DATE);
	}
	
	@Test(expected = BranchMergeException.class)
	public void mergeBehind() throws Exception {
		String a = createBranch(MAIN, "a");
		branching().handleCommit(MAIN, currentTime());
		branching().merge(a, MAIN, "Merge A to MAIN");
	}
	
	@Test(expected = BranchMergeException.class)
	public void mergeDiverged() throws Exception {
		String a = createBranch(MAIN, "a");
		branching().handleCommit(MAIN, currentTime());
		branching().handleCommit(a, currentTime());
		branching().merge(a, MAIN, "Merge A to MAIN");
	}
	
	@Test
	public void rebaseUpToDateState() throws Exception {
		final String a = createBranch(MAIN, "a");
		final long expected = getBranch(a).getBaseTimestamp();
		branching().rebase(a, MAIN, "Rebase A", () -> {});
		final long actual = getBranch(a).getBaseTimestamp();
		assertEquals("Rebasing UP_TO_DATE branch should do nothing", expected, actual);
	}
	
	@Test
	public void rebaseForwardState() throws Exception {
		final String a = createBranch(MAIN, "a");
		final long expected = getBranch(a).getBaseTimestamp();
		branching().handleCommit(a, currentTime());
		branching().rebase(a, MAIN, "Rebase A", () -> {});
		final long actual = getBranch(a).getBaseTimestamp();
		assertEquals("Rebasing FORWARD branch should do nothing", expected, actual);
	}
	
	@Test
	public void rebaseBehindState() throws Exception {
		final String a = createBranch(MAIN, "a");
		branching().handleCommit(MAIN, currentTime());
		assertState(a, BranchState.BEHIND);
		branching().rebase(a, MAIN, "Rebase A", () -> {});
		assertState(a, BranchState.UP_TO_DATE);
		assertLaterBase(a, MAIN);
	}

	@Test
	public void rebaseDivergedState() throws Exception {
		final String a = createBranch(MAIN, "a");
		branching().handleCommit(MAIN, currentTime());
		branching().handleCommit(a, currentTime());
		assertState(a, BranchState.DIVERGED);
		branching().rebase(a, MAIN, "Rebase A", () -> {});
		assertState(a, BranchState.FORWARD);
		assertLaterBase(a, MAIN);
	}

	@Test
	public void rebaseDivergedWithBehindChild() throws Exception {
		final String a = createBranch(MAIN, "a");
		final String b = createBranch(a, "b");
		
		branching().handleCommit(MAIN, currentTime());
		branching().handleCommit(a, currentTime());
		
		assertState(a, BranchState.DIVERGED);
		assertState(b, BranchState.BEHIND);
		
		branching().rebase(a, MAIN, "Rebase A", () -> {});
		
		assertState(a, BranchState.FORWARD);
		assertLaterBase(a, MAIN);
		assertState(b, BranchState.STALE);
	}
	
	@Test
	public void rebaseBehindWithForwardChild() throws Exception {
		final String a = createBranch(MAIN, "a");
		final String b = createBranch(a, "b");
		
		branching().handleCommit(MAIN, currentTime());
		branching().handleCommit(b, currentTime());
		
		assertState(a, BranchState.BEHIND);
		assertState(b, BranchState.FORWARD);
		
		branching().rebase(a, MAIN, "Rebase A", () -> {});
		
		assertState(a, BranchState.UP_TO_DATE);
		assertLaterBase(a, MAIN);
		assertState(b, BranchState.STALE);
	}
	
	@Test
	public void rebaseDivergedWithTwoChildren() throws Exception {
		final String a = createBranch(MAIN, "a");
		branching().handleCommit(MAIN, currentTime());
		
		final String b = createBranch(a, "b");
		branching().handleCommit(a, currentTime());
		
		final String c = createBranch(a, "c");
		
		branching().rebase(a, MAIN, "Rebase A", () -> {});
		
		assertState(a, BranchState.FORWARD);
		assertState(b, BranchState.STALE);
		assertState(c, BranchState.STALE);
	}
	
	@Test
	public void rebaseBehindChildOnRebasedForwardParent() throws Exception {
		rebaseDivergedWithBehindChild();
		branching().rebase("MAIN/a/b", "MAIN/a", "Rebase A", () -> {});
		assertState("MAIN/a/b", BranchState.UP_TO_DATE);
	}
	
	private void assertLaterBase(String branch, String other) {
		assertTrue(String.format("Basetimestamp of branch '%s' should be later than headTimestamp of '%s'.", branch, other), getBranch(branch).getBaseTimestamp() > getBranch(other).getHeadTimestamp());
	}
	
	private void assertState(String branchPath, BranchState expectedState) {
		assertEquals(expectedState, branching().getBranchState(branchPath));
	}
	
}
