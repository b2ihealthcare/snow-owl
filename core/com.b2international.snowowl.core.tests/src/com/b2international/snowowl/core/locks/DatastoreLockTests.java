/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.locks;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.mapping.Mappings;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContext;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.internal.locks.DatastoreLockTarget;
import com.b2international.snowowl.core.internal.locks.Slf4jOperationLockTargetListener;
import com.b2international.snowowl.core.locks.DatastoreLockIndexEntry;
import com.b2international.snowowl.core.locks.DatastoreOperationLockManager;
import com.b2international.snowowl.core.locks.OperationLock;
import com.b2international.snowowl.core.locks.OperationLockException;
import com.b2international.snowowl.core.locks.OperationLockInfo;
import com.b2international.snowowl.core.repository.JsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 7.1.0
 */
public class DatastoreLockTests {

	private static final long TIMEOUT = 10_000L;

	private static final String USER = "info@b2international.com";
	
	private DatastoreOperationLockManager manager;

	@Before
	public void setup() {
		final ObjectMapper mapper = JsonSupport.getDefaultObjectMapper();
		final Index index = Indexes.createIndex("locks", mapper, new Mappings(DatastoreLockIndexEntry.class));
		manager = new DatastoreOperationLockManager(index);
		manager.addLockTargetListener(new Slf4jOperationLockTargetListener());
		manager.unlockAll();
	}
	
	@Test
	public void testLockAll() {
		final DatastoreLockContext context = createContext(USER, DatastoreLockContextDescriptions.MAINTENANCE);
		final DatastoreLockTarget allLockTarget = DatastoreLockTarget.ALL;
		
		try {
			manager.lock(context, TIMEOUT, allLockTarget);
			checkIfLockExists(context, true, allLockTarget);
			manager.unlockAll();
		} catch (OperationLockException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testUnlock() {
		final DatastoreLockContext context = createContext(USER, DatastoreLockContextDescriptions.MAINTENANCE);
		final DatastoreLockTarget target = new DatastoreLockTarget("snomedStore", "MAIN");
		
		try {
			manager.lock(context, TIMEOUT, target);
			checkIfLockExists(context, true, target);

			manager.unlock(context, target);
			checkIfLockExists(context, false, target);
		} catch (OperationLockException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testUnlockAll() {
		final DatastoreLockContext context = createContext(USER, DatastoreLockContextDescriptions.MAINTENANCE);
		final DatastoreLockTarget target1 = new DatastoreLockTarget("snomedStore", "MAIN");
		final DatastoreLockTarget target2 = new DatastoreLockTarget("loincStore", "MAIN");
		
		try {
			manager.lock(context, TIMEOUT, target1, target2);
			checkIfLockExists(context, true, target1, target2);
			
			manager.unlockAll();
			checkIfLockExists(context, false, target1, target2);
		} catch (OperationLockException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test(expected = OperationLockException.class)
	public void testLockAllNotAbleToLockAnother() throws OperationLockException {
		final DatastoreLockContext context = createContext(USER, DatastoreLockContextDescriptions.MAINTENANCE);
		final DatastoreLockTarget allLockTarget = DatastoreLockTarget.ALL;

		try {
			manager.lock(context, 1_000L, allLockTarget);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			manager.lock(context, 1_000L, allLockTarget);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLockBranchAndRepository() {
		final DatastoreLockContext context = createContext(USER, DatastoreLockContextDescriptions.CREATE_VERSION);
		final DatastoreLockTarget target = new DatastoreLockTarget("snomedStore", "MAIN");
		try {
			manager.lock(context, 10_000L, target);
			checkIfLockExists(context, true, target);
		} catch (OperationLockException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private DatastoreLockContext createContext(final String user, final String description) {
		return new DatastoreLockContext(user, description);
	}
	
	private void checkIfLockExists(DatastoreLockContext context,  boolean expected, DatastoreLockTarget...targets) {
		for (int i = 0; i < targets.length; i++) {
			final DatastoreLockTarget target = targets[i];
			final OperationLock operationLock = new OperationLock(i, target);
			operationLock.acquire(context);
			final OperationLockInfo info = new OperationLockInfo(i, operationLock.getLevel(), operationLock.getCreationDate(), target, context);
			assertTrue(expected == manager.getLocks().contains(info));
		}
	}
	
}
