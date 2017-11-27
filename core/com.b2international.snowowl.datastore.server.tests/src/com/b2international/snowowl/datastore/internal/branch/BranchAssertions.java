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

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.branch.Branch.BranchState;

/**
 * @since 4.1
 */
public class BranchAssertions {

	// Use only in tests where a working BranchManager is present
	public static void assertState(Branch branch, BranchState expectedState) {
		assertEquals(String.format("Incorrect branch state of branch '%s' ", branch.path()), expectedState, branch.state());
	}

	public static void assertState(final Branch branch, final Branch other, final BranchState expectedState) {
		assertEquals(String.format("Incorrect branch state of branch '%s' compared to branch '%s'", branch.path(), other.path()), expectedState, branch.state(other));
	}

	public static void assertPath(Branch branch, String expectedPath) {
		assertEquals("Branch path should be '%s'.", expectedPath, branch.path());
	}

	public static void assertLaterBase(Branch branch, Branch other) {
		assertTrue(String.format("Basetimestamp of branch '%s' should be later than headTimestamp of '%s'.", branch.path(), other.path()), branch.baseTimestamp() > other.headTimestamp());
	}

	public static void assertParent(Branch branch, Branch parent) {
		assertEquals(String.format("Parent of branch '%s' should be '%s'.", branch.path(), parent.path()), branch.parent(), parent);		
	}
}
