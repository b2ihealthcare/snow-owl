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
package com.b2international.snowowl.datastore.branch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.NotFoundException;

/**
 * @since 4.1
 */
public class BranchManagerTest {

	private BranchManager manager;
	private Branch main;

	@Before
	public void givenBranchManager() {
		manager = new BranchManager(new AtomicLongTimestampAuthority());
		main = manager.getMainBranch();
	}
	
	@Test
	public void whenGettingMainBranch_ThenItShouldBeReturned() throws Exception {
		assertNotNull(main);
	}
	
	@Test(expected = NotFoundException.class)
	public void whenGettingNonExistingBranch_ThenThrowNotFoundException() throws Exception {
		manager.getBranch("MAIN/a");
	}
	
	@Test
	public void whenCreatingBranch_ThenItShouldBeReturnedViaGet() throws Exception {
		final Branch created = main.createChild("a");
		assertEquals(created, manager.getBranch("MAIN/a"));
	}
	
	@Test(expected = AlreadyExistsException.class)
	public void whenCreatingAlreadyExistingBranch_ThenThrowException() throws Exception {
		main.createChild("a");
		main.createChild("a");
	}
	
	@Test
	public void whenCreatingDeepBranchHierarchy_ThenEachSegmentShouldBeCreatedAndStoredInBranchManager() throws Exception {
		final Branch abcd = main.createChild("a").createChild("b").createChild("c").createChild("d");
		assertEquals("MAIN/a/b/c/d", abcd.path());
		final Branch abc = abcd.parent();
		final Branch ab = abc.parent();
		final Branch a = ab.parent();
		final Branch main = a.parent();
		assertEquals(manager.getBranch("MAIN/a/b/c"), abc);
		assertEquals(manager.getBranch("MAIN/a/b"), ab);
		assertEquals(manager.getBranch("MAIN/a"), a);
		assertEquals(this.main, main);
	}
	
	@Test
	public void whenCreatingThreeBranches_ThenManagerShouldReturnAllOfThemInGetAll() throws Exception {
		final Branch a = main.createChild("a");
		final Branch b = main.createChild("b");
		final Branch c = main.createChild("c");
		final Collection<Branch> branches = manager.getBranches();
		assertThat(branches).containsExactly(main, a, b, c);
	}
	
	@Test
	public void whenDeletingBranch_ThenManagerShouldStillReturnIt() throws Exception {
		final Branch a = main.createChild("a");
		a.delete();
		assertEquals(manager.getBranch("MAIN/a"), a);
	}
	
}
