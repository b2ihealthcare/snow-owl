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
package com.b2international.snowowl.snomed.api.rest.branches

import com.jayway.restassured.response.Response
import java.util.Date
import java.util.UUID
import com.b2international.snowowl.snomed.api.rest.concept.*

import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*

/**
 * @since 2.0
 */
Feature: SnomedBranchingApi

	Background:
		static String API = "/v2/snomed-ct"
		var public String version;
		var public String branchName = UUID.randomUUID.toString
		var description = "Description at " + new Date
		var req = givenAuthenticatedRequest(API)
		var Response res;
		
	Scenario: New SNOMED CT Branch
	
		Given new SNOMED-CT branch request
			req.withJson(#{
  				"name" -> branchName
			})
		When sending POST to "/branches"
		Then return "201" status
		And return location header pointing to "/branches/MAIN/${branchName}"