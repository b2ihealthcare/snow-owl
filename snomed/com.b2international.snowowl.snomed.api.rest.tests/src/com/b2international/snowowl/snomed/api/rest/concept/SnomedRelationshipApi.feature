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
package com.b2international.snowowl.snomed.api.rest.concept

import com.jayway.restassured.response.Response
import java.util.UUID

import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.*
import java.util.Map

/**
 * @since 2.0
 */
Feature: SnomedRelationshipApi

	Background:
		static String API = "/snomed-ct/v2"
		var req = givenAuthenticatedRequest(API)
		var Response res
		var public String branchPath
		var public branchName = UUID.randomUUID.toString
		
		// concept IDs available in the MiniCT FULL RF2
		val disease = "64572001"
		val severity = "246112005"
		val associatedWith = "47429007"
		var Map<String, Object> json
		
	Scenario: New Relationship on non-existent SNOMED CT branch
		
		Given branchPath "MAIN/nonexistent"
		And new relationship
			req.withJson(#{
				"sourceId" -> disease,
				"typeId" -> associatedWith,
				"destinationId" -> severity
			})
		When sending POST to "/relationships"
		Then return "404" status
		And return body with status "404"
		
	Scenario: New Relationship on SNOMED CT Main branch
		
		Given branchPath "MAIN"
		And new relationship
		When sending POST to "/relationships"
		Then return "404" status
		And return body with status "404"
		
	Scenario: Delete relationship
		
	Scenario: Change relationship characteristictype
		
	Scenario: Inactivate relationship
	
	Scenario: Create relationship on branch
		
	Scenario: Create relationship on deleted branch
		