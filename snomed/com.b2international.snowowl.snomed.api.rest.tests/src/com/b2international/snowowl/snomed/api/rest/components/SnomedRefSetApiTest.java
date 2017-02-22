/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.components;

import static com.b2international.snowowl.snomed.api.rest.SnomedComponentRestRequests.getComponent;

import org.junit.Test;

import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.api.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;

/**
 * @since 4.5
 */
public class SnomedRefSetApiTest extends AbstractSnomedApiTest {

	@Test
	public void getRefSetNonExistingBranch() throws Exception {
		getComponent(BranchPathUtils.createPath("MAIN/x/y/z"), SnomedComponentType.REFSET, Concepts.REFSET_LANGUAGE_TYPE_UK).statusCode(404);
	}

	@Test
	public void getRefSetNonExistingIdentifier() throws Exception {
		getComponent(branchPath, SnomedComponentType.REFSET, "11110000").statusCode(404);
	}

}
