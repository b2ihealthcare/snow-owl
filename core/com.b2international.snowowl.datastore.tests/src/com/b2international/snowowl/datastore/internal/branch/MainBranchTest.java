/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.internal.branch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.b2international.snowowl.datastore.branch.Branch;
import com.b2international.snowowl.datastore.internal.branch.MainBranch;
import com.b2international.snowowl.datastore.internal.branch.BranchImpl.BranchState;

public class MainBranchTest {

	private Branch main = new MainBranch();
	private Branch mainWithTimestamp = new MainBranch(5L);

	@Test
	public void pathShouldBeMain() {
		assertEquals("Main branch path should be 'MAIN'.", "MAIN", main.path());
	}

	@Test
	public void nameShouldBeMain() {
		assertEquals("Main branch name should be 'MAIN'.", "MAIN", main.name());
	}

	@Test
	public void shouldBeEqualToMain() {
		Branch main2 = new MainBranch();
		assertTrue("Separately created main branches should be equal.", main.equals(main2));
	}

	@Test
	public void parentShouldBeMain() throws Exception {
		assertEquals("Parent of main branch should be the main branch.", main, main.parent());
	}

	@Test(expected=IllegalArgumentException.class)
	public void mainBaseTimestampShouldBeNonNegative() throws Exception {
		new MainBranch(-1L);
	}

	@Test
	public void mainBaseTimestamp() throws Exception {
		assertEquals("Branch 'MAIN' should have base timestamp 5.", 5L, new MainBranch(5L).baseTimestamp());
	}

	@Test
	public void mainBaseAndHeadTimestampsShouldBeEqual() throws Exception {
		Branch main2 = new MainBranch(5L);
		assertTrue("Branch 'MAIN' should have equal base and head timestamps.", main2.headTimestamp() == main2.baseTimestamp());
	}

	@Test(expected=IllegalArgumentException.class)
	public void handleCommitOnMainFromPast() {
		mainWithTimestamp.handleCommit(3L);
	}

	@Test(expected=IllegalArgumentException.class)
	public void handleCommitOnMainTwice() {
		mainWithTimestamp.handleCommit(5L);
	}

	@Test
	public void handleCommitOnMainAdvancesHeadTimestamp() {
		main.handleCommit(8L);
		main.handleCommit(9L);
		main.handleCommit(10L);
		assertEquals("Branch 'MAIN' should have head timestamp 10 after committing at timestamps 8, 9 and 10.", 10L, main.headTimestamp());
	}

	@Test
	public void testAlwaysUpToDate() throws Exception {
		assertEquals("Branch 'MAIN' should be in UP_TO_DATE state initially.", BranchState.UP_TO_DATE, main.state());
		main.handleCommit(5L);
		assertEquals("Branch 'MAIN' should be in UP_TO_DATE state, even after committing.", BranchState.UP_TO_DATE, main.state());
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void rebaseMainBranch() throws Exception {
		main.rebase();
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void deleteMainBranch() throws Exception {
		main.delete();
	}
}
