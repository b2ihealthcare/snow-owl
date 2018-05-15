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
import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

import com.b2international.index.revision.RevisionBranch.BranchState;

/**
 * @since 6.5
 */
public class RevisionBranchingTest extends BaseRevisionIndexTest {

	@Test
	public void afterInit() throws Exception {
		RevisionBranch main = branching().getMainBranch();
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
		assertThat(branching().getMainBranch().getHeadTimestamp()).isEqualTo(timestamp);
		assertThat(branching().getBranchState(MAIN)).isEqualTo(BranchState.UP_TO_DATE);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void deleteMainIsNotPossible() throws Exception {
		branching().delete(MAIN);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createBranchWithEmptyName() throws Exception {
		createBranch(MAIN, "");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createBranchWithPath() throws Exception {
		createBranch(MAIN, "a/b");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void createBranchWithInvalidChars() throws Exception {
		createBranch(MAIN, "?a");
	}
	
	@Test(expected = IllegalArgumentException.class)
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
		assertThat(branching().getBranchState(path)).isEqualTo(BranchState.BEHIND);
	}
	
	@Test
	public void divergedStateAfterParentAndBranchCommit() throws Exception {
		final String path = createBranch(MAIN, "a");
		commit(MAIN, Collections.emptyMap());
		commit(path, Collections.emptyMap());
		assertThat(branching().getBranchState(path)).isEqualTo(BranchState.DIVERGED);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void rebaseMain() throws Exception {
		branching().rebase(MAIN);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void mergeMain() throws Exception {
		branching().merge(MAIN, MAIN);
	}
	
}
