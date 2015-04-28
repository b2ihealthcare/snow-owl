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

import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*
import com.b2international.snowowl.snomed.api.rest.io.*
import com.b2international.snowowl.snomed.api.rest.concept.*
import com.jayway.restassured.response.Response

/**
 * @since 2.0
 */
Feature: SnomedImportApiExamples
	
	Background:
		static String UK_LANG_REFSET = "900000000000508004"
		static String API = "/snomed-ct/v2"
		static String MEDIA_TYPE = ""
		var req = givenAuthenticatedRequest(API)
		var Response res
		var public String importId
	
	Scenario: Import new concept via RF2 Delta archive
		
		Given SNOMED CT "DELTA" import configuration on branch "MAIN"
		When sending POST to "/imports/${importId}/archive" with file "SnomedCT_Release_INT_20150131_new_concept.zip"
			res = req.withFile(args.second, getClass()).post(args.first.renderWithFields(this))
		Then return "204" status
		And wait until the import state is either "COMPLETED" or "FAILED"
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

	Scenario: Import new description via RF2 Delta archive
		
		Given SNOMED CT "DELTA" import configuration on branch "MAIN"
		When sending POST to "/imports/${importId}/archive" with file "SnomedCT_Release_INT_20150201_new_description.zip"
		Then return "204" status
		And wait until the import state is either "COMPLETED" or "FAILED"
		And it should succeed
	
	Scenario: Import new relationship via RF2 Delta archive
		
		Given SNOMED CT "DELTA" import configuration on branch "MAIN"
		When sending POST to "/imports/${importId}/archive" with file "SnomedCT_Release_INT_20150202_new_relationship.zip"
		Then return "204" status
		And wait until the import state is either "COMPLETED" or "FAILED"
		And it should succeed
		
	Scenario: Import preferred term change via RF2 Delta archive
		
		Given SNOMED CT "DELTA" import configuration on branch "MAIN"
		When sending POST to "/imports/${importId}/archive" with file "SnomedCT_Release_INT_20150203_change_pt.zip"
		Then return "204" status
		And wait until the import state is either "COMPLETED" or "FAILED"
		And it should succeed
		
	Scenario: Import concept inactivation via RF2 Delta archive
		
		Given SNOMED CT "DELTA" import configuration on branch "MAIN"
		When sending POST to "/imports/${importId}/archive" with file "SnomedCT_Release_INT_20150204_inactivate_concept.zip"
		Then return "204" status
		And wait until the import state is either "COMPLETED" or "FAILED"
		And it should succeed