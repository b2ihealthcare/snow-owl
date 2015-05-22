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
package com.b2international.snowowl.snomed.api.rest.io

import com.jayway.restassured.response.Response
import com.b2international.snowowl.snomed.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.api.rest.components.*

import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*

/**
 * @since 2.0
 */
Feature: SnomedImportApiExamples
	
	Background:
		static String API = "/snomed-ct/v2"
		var req = givenAuthenticatedRequest(API)
		var Response res
		var public String importId
		var public String branchPath
		
	Scenario: Import new concept via RF2 Delta archive
		
		Given branch "import-branch" under "MAIN"
		And SNOMED CT "DELTA" import configuration on branch "${branchPath}"
		And concept "63961392103" is not available on "${branchPath}"
			API.get(args.second.renderWithFields(this), "concepts", args.first).expectStatus(404)
		When sending POST to "/imports/${importId}/archive" with file "SnomedCT_Release_INT_20150131_new_concept.zip"
			res = req.withFile(args.second, getClass()).post(args.first.renderWithFields(this))
		And return "204" status
		Then wait until the import state is either "COMPLETED" or "FAILED"
			res.then.statusCode(204) // guard to make this part fail if Then part failed
			val states = #{args.first, args.second}
			val delay = 1000L;
			var totalWaitTime = 30 * delay; // 30 sec
			do {
				totalWaitTime = totalWaitTime - delay;
				Thread.sleep(delay);
				res = API.get("imports", importId)
			} while (!states.contains(res.getBody.path("status")));
		And it should succeed
			res.getBody.path("status") should be "COMPLETED"
		And concept "63961392103" should be available on "${branchPath}"
			val res = API.get(args.second.renderWithFields(this), "concepts", args.first)
			res.expectStatus(200)
			res.getBody.path("active") should be true

	Scenario: Import new description via RF2 Delta archive
		
		Given branch "import-branch" under "MAIN"
		And SNOMED CT "DELTA" import configuration on branch "${branchPath}"
		And description "11320138110" is not available on "${branchPath}"
			API.get(args.second.renderWithFields(this), "descriptions", args.first).expectStatus(404)
		When sending POST to "/imports/${importId}/archive" with file "SnomedCT_Release_INT_20150201_new_description.zip"
		And return "204" status
		Then wait until the import state is either "COMPLETED" or "FAILED"
		And it should succeed
		And description "11320138110" should be available on "${branchPath}"
			val res = API.get(args.second.renderWithFields(this), "descriptions", args.first)
			res.expectStatus(200)
			res.getBody.path("active") should be true
	
	Scenario: Import new relationship via RF2 Delta archive
		
		Given branch "import-branch" under "MAIN"
		And SNOMED CT "DELTA" import configuration on branch "${branchPath}"
		And relationship "24088071128" is not available on "${branchPath}"
			API.get(args.second.renderWithFields(this), "relationships", args.first).expectStatus(404)
		When sending POST to "/imports/${importId}/archive" with file "SnomedCT_Release_INT_20150202_new_relationship.zip"
		And return "204" status
		Then wait until the import state is either "COMPLETED" or "FAILED"
		And it should succeed
		And relationship "24088071128" should be available on "${branchPath}"
			val res = API.get(args.second.renderWithFields(this), "relationships", args.first)
			res.expectStatus(200)
			res.getBody.path("active") should be true
		
	Scenario: Import preferred term change via RF2 Delta archive
		
		Given branch "import-branch" under "MAIN"
		And SNOMED CT "DELTA" import configuration on branch "${branchPath}"
		And preferred term of concept "63961392103" is "13809498114" on "${branchPath}"
			val res = givenAuthenticatedRequest(API).header("Accept-Language", "en-GB").get(asPath(#[args.third.renderWithFields(this), "concepts", args.first, "pt"]))
			res.expectStatus(200)
			res.getBody.path("id") should be args.second
		When sending POST to "/imports/${importId}/archive" with file "SnomedCT_Release_INT_20150203_change_pt.zip"
		And return "204" status
		Then wait until the import state is either "COMPLETED" or "FAILED"
		And it should succeed
		And preferred term of concept "63961392103" is "11320138110" on "${branchPath}"
		
	Scenario: Import concept inactivation via RF2 Delta archive
		
		Given branch "import-branch" under "MAIN"
		And SNOMED CT "DELTA" import configuration on branch "${branchPath}"
		When sending POST to "/imports/${importId}/archive" with file "SnomedCT_Release_INT_20150204_inactivate_concept.zip"
		And return "204" status
		Then wait until the import state is either "COMPLETED" or "FAILED"
		And it should succeed
		And concept "63961392103" should be inactive on "${branchPath}"
			API.get(args.second.renderWithFields(this), "concepts", args.first).getBody.path("active") should be false
