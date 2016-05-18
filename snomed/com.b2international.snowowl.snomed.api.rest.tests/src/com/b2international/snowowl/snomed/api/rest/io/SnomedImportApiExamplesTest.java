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
package com.b2international.snowowl.snomed.api.rest.io;

import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertComponentActive;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertConceptExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertConceptNotExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertDescriptionExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertDescriptionNotExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertPreferredTermEquals;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertRelationshipExists;
import static com.b2international.snowowl.snomed.api.rest.SnomedComponentApiAssert.assertRelationshipNotExists;
import static com.b2international.snowowl.test.commons.rest.RestExtensions.givenAuthenticatedRequest;

import org.hamcrest.CoreMatchers;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.snomed.api.rest.SnomedComponentType;

/**
 * @since 2.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SnomedImportApiExamplesTest extends AbstractSnomedImportApiTest {

	@Override
	protected IBranchPath createRandomBranchPath() {
		return BranchPathUtils.createMainPath();
	}
	
	@Test
	public void import01NewConcept() {
		assertConceptNotExists(testBranchPath, "63961392103");
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150131_new_concept.zip");
		assertConceptExists(testBranchPath, "63961392103");
		assertPreferredTermEquals(testBranchPath, "63961392103", "13809498114");
		givenAuthenticatedRequest("/admin").when().get("/codesystems/{shortName}/versions/{version}", "SNOMEDCT", "2015-01-31").then().statusCode(200);
	}

	@Test
	public void import02NewDescription() {
		assertDescriptionNotExists(testBranchPath, "11320138110");
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150201_new_description.zip");
		assertDescriptionExists(testBranchPath, "11320138110");
		givenAuthenticatedRequest("/admin").when().get("/codesystems/{shortName}/versions/{version}", "SNOMEDCT", "2015-02-01").then().statusCode(200);
	}

	@Test
	public void import03NewRelationship() {
		assertRelationshipNotExists(testBranchPath, "24088071128");
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150202_new_relationship.zip");
		assertRelationshipExists(testBranchPath, "24088071128");
		givenAuthenticatedRequest("/admin").when().get("/codesystems/{shortName}/versions/{version}", "SNOMEDCT", "2015-02-02").then().statusCode(200);
	}

	@Test
	public void import04NewPreferredTerm() {
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150203_change_pt.zip");
		assertPreferredTermEquals(testBranchPath, "63961392103", "11320138110");
		givenAuthenticatedRequest("/admin").when().get("/codesystems/{shortName}/versions/{version}", "SNOMEDCT", "2015-02-03").then().statusCode(200);
	}

	@Test
	public void import05ConceptInactivation() {
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150204_inactivate_concept.zip");
		assertComponentActive(testBranchPath, SnomedComponentType.CONCEPT, "63961392103", false);
	}
	
	@Test
	public void import06IndexInitBug_ImportSameNewConceptWithAdditionalDescriptionShouldNotFail() throws Exception {
		assertConceptExists(testBranchPath, "63961392103").body("active", CoreMatchers.equalTo(false));
		assertPreferredTermEquals(testBranchPath, "63961392103", "11320138110");
		assertImportFileCanBeImported("SnomedCT_Release_INT_20150131_index_init_bug.zip");
		assertConceptExists(testBranchPath, "63961392103").body("active", CoreMatchers.equalTo(false));
		assertPreferredTermEquals(testBranchPath, "63961392103", "11320138110");
	}
	
}
