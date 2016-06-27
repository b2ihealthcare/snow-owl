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
package com.b2international.snowowl.snomed.api.rest;

import static com.b2international.snowowl.snomed.api.rest.SnomedBranchingApiAssert.givenBranchWithPath;

import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;

/**
 * @since 2.0
 */
public abstract class AbstractSnomedApiTest {

	protected static final String DISEASE = "64572001";
	protected static final String BLEEDING = "50960005";
	protected IBranchPath testBranchPath;

	@Rule
	public TestWatcher watcher = new TestWatcher() {
		
		@Override
		protected void starting(Description description) {
			System.out.println("===== Start of " + description + " =====");
		}
		
		@Override
		protected void finished(Description description) {
			System.out.println("===== End of " + description + " =====");
		}
		
	};
	
	@Before
	public void setup() {
		testBranchPath = createRandomBranchPath();
	}

	protected IBranchPath createRandomBranchPath() {
		return BranchPathUtils.createPath(BranchPathUtils.createMainPath(), UUID.randomUUID().toString());
	}

	protected IBranchPath createNestedBranch(IBranchPath parent, final String... segments) {
		IBranchPath currentBranchPath = parent;
		
		for (final String segment : segments) {
			currentBranchPath = BranchPathUtils.createPath(currentBranchPath, segment);
			givenBranchWithPath(currentBranchPath);
		}

		return currentBranchPath;
	}
	
}
