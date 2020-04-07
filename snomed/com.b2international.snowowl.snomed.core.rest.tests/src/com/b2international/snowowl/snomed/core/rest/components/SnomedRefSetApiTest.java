/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.components;

import static com.b2international.snowowl.snomed.core.rest.SnomedComponentRestRequests.getComponent;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createConcreteDomainParentConcept;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createConcreteDomainRefSet;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewRefSet;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.createNewRefSetMember;
import static com.b2international.snowowl.snomed.core.rest.SnomedRestFixtures.inactivateConcept;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.Test;

import com.b2international.snowowl.core.branch.BranchPathUtils;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.refset.DataType;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.core.rest.AbstractSnomedApiTest;
import com.b2international.snowowl.snomed.core.rest.SnomedComponentType;

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

	@Test
	public void createConcreteDomainReferenceSets() {
		createConcreteDomainParentConcept(branchPath);

		for (DataType dataType : DataType.values()) {
			System.out.println("Datatype: " + dataType);
			createConcreteDomainRefSet(branchPath, dataType);
		}
	}
	
	@Test
	public void inactivateIdentifierConceptInactivatesMembers() throws Exception {
		final String refSetId = createNewRefSet(branchPath, SnomedRefSetType.SIMPLE);
		final String memberId1 = createNewRefSetMember(branchPath, Concepts.ROOT_CONCEPT, refSetId);
		final String memberId2 = createNewRefSetMember(branchPath, Concepts.FINDING_SITE, refSetId);
		
		inactivateConcept(branchPath, refSetId);
		
		getComponent(branchPath, SnomedComponentType.REFSET, refSetId)
			.body("active", equalTo(false));
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId1)
			.body("active", equalTo(false));
		getComponent(branchPath, SnomedComponentType.MEMBER, memberId2)
			.body("active", equalTo(false));
	}

}
