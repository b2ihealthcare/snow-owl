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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.core.exceptions.NotFoundException;

/**
 * @since 4.1
 */
public class BranchManagerTest {

	private BranchManager manager;

	@Before
	public void givenBranchManager() {
		manager = new BranchManager(new AtomicLongTimestampAuthority());
	}
	
	@Test
	public void whenGettingMainBranch_ThenItShouldBeReturned() throws Exception {
		assertNotNull(manager.getMainBranch());
	}
	
	@Test(expected = NotFoundException.class)
	public void whenGettingNonExistingBranch_ThenThrowNotFoundException() throws Exception {
		manager.getBranch("MAIN/a");
	}
	
	@Test
	public void whenCreatingBranch_ThenItShouldBeReturnedViaGet() throws Exception {
		final Branch created = manager.getMainBranch().createChild("a");
		assertEquals(created, manager.getBranch("MAIN/a"));
	}
	
	@Test(expected = AlreadyExistsException.class)
	public void whenCreatingAlreadyExistingBranch_ThenThrowException() throws Exception {
		manager.getMainBranch().createChild("a");
		manager.getMainBranch().createChild("a");
	}
	
}
