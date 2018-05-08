/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.b2international.commons.options.MetadataImpl;
import com.b2international.snowowl.core.branch.Branch.BranchState;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.datastore.internal.branch.BranchManagerImpl;
import com.b2international.snowowl.datastore.internal.branch.InternalBranch;
import com.b2international.snowowl.datastore.internal.branch.MainBranchImpl;
import com.b2international.snowowl.datastore.server.internal.JsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.1
 */
public class MainBranchTest {

	private BranchManagerImpl manager;
	private MainBranchImpl main;
	private MainBranchImpl mainWithTimestamp;
	private ObjectMapper serializer = JsonSupport.getDefaultObjectMapper();

	@Before
	public void before() {
		manager = mock(BranchManagerImpl.class);
		main = new MainBranchImpl(0L);
		main.setBranchManager(manager);
		mainWithTimestamp = new MainBranchImpl(5L);
		mainWithTimestamp.setBranchManager(manager);
	}

	@Test
	public void updateMetadataShouldReturnNewInstanceWithProperType() throws Exception {
		final InternalBranch newMain = main.withMetadata(new MetadataImpl());
		assertTrue(newMain instanceof MainBranchImpl);
	}
	
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
		MainBranchImpl main2 = new MainBranchImpl(0L);
		main2.setBranchManager(manager);
		assertTrue("Separately created main branches should be equal.", main.equals(main2));
	}

	@Test
	public void parentShouldBeMain() throws Exception {
		assertEquals("Parent of main branch should be the main branch.", main, main.parent());
	}

	@Test(expected=IllegalArgumentException.class)
	public void mainBaseTimestampShouldBeNonNegative() throws Exception {
		new MainBranchImpl(-1L);
	}

	@Test
	public void mainBaseTimestamp() throws Exception {
		assertEquals("Branch 'MAIN' should have base timestamp 5.", 5L, mainWithTimestamp.baseTimestamp());
	}

	@Test
	public void mainBaseAndHeadTimestampsShouldBeEqual() throws Exception {
		assertTrue("Branch 'MAIN' should have equal base and head timestamps.", mainWithTimestamp.headTimestamp() == mainWithTimestamp.baseTimestamp());
	}

	@Test
	public void handleCommitOnMainAdvancesHeadTimestamp() {
		assertEquals("Branch 'MAIN' should have head timestamp 10 after committing at timestamps 8, 9 and 10.", 10L, commit(main, 8L, 9L, 10L).headTimestamp());
	}

	@Test
	public void testAlwaysUpToDate() throws Exception {
		assertEquals("Branch 'MAIN' should be in UP_TO_DATE state, even after committing.", BranchState.UP_TO_DATE, commit(main, 5L).state());
	}
	
	private InternalBranch commit(InternalBranch branch, long... timestamps) {
		for (long timestamp : timestamps) {
			branch = branch.withHeadTimestamp(timestamp);
		}
		return branch;
	}
	
	@Test(expected = BadRequestException.class)
	public void deleteMainBranch() throws Exception {
		main.delete();
	}

	@Test(expected = BadRequestException.class)
	public void rebaseMainBranch() throws Exception {
		main.rebase(main, "Rebase");
	}
	
}
