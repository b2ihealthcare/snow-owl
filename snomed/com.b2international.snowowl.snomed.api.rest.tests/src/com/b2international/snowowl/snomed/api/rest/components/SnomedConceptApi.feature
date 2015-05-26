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
package com.b2international.snowowl.snomed.api.rest.components

import com.jayway.restassured.response.Response
import java.util.Date
import static org.hamcrest.CoreMatchers.*;

import com.b2international.snowowl.snomed.api.rest.branches.*
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.*
import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*
import java.util.UUID
import java.util.Collection

/**
 * @since 1.0
 */
Feature: SnomedConceptApi

	Background:
		static String API = "/snomed-ct/v2"
		var req = givenAuthenticatedRequest(API)
		var Response res
		var public String branchPath
		var public branchName = UUID.randomUUID.toString
		
		val correctAcceptabilityMap = #{ REFSET_LANGUAGE_TYPE_UK -> "PREFERRED" }
		val incorrectAcceptabilityMap = #{ "1" -> "PREFERRED" }
		
	Scenario: New Concept on non-existent SNOMED CT branch
		
		Given branchPath "MAIN/1998-01-31"
			branchPath = args.first.renderWithFields(this)
		And new concept
			req.withJson(#{
				"parentId" -> ROOT_CONCEPT,
				"moduleId" -> MODULE_SCT_CORE,
				"descriptions" -> #[
					#{
						"typeId" -> FULLY_SPECIFIED_NAME,
						"term" -> "New Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap 
					},
					#{
						"typeId" -> SYNONYM,
						"term" -> "New Preferred Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap 
					}
				],
				"commitComment" -> "New concept"
			})
		When sending POST to "/${branchPath}/concepts"
			res = req.post(args.first.renderWithFields(this))
		Then return "404" status
			// if the status code is not the expected one print the method body to track failures easily
			res.expectStatus(args.first.toInt)
		And return body with status "404"
			res.then.body("status", equalTo(args.first.toInt))
			
	Scenario: New Concept under unspecified parent Concept
		
		Given branchPath "MAIN"
		And new child concept of parent ""
			req.withJson(#{
				"parentId" -> args.first,
				"moduleId" -> MODULE_SCT_CORE,
				"descriptions" -> #[
					#{
						"typeId" -> FULLY_SPECIFIED_NAME,
						"term" -> "New Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap
					},
					#{
						"typeId" -> SYNONYM,
						"term" -> "New Preferred Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap 
					}
				],
				"commitComment" -> "New concept"
			})
		When sending POST to "/${branchPath}/concepts"
		Then return "400" status
		And return body with status "400"
		And return body with message "1 validation error"
			res.path("message") should be args.first.renderWithFields(this)
			println(res.getBody.asString)
		And return body with violation message "'parentId' may not be empty (was '')"
			res.path("violations") should be [
				it instanceof Collection && (it as Collection).contains(args.first.renderWithFields(this))
			]
		
	Scenario: New Concept under unknown parent Concept
		
		Given branchPath "MAIN"
		And new child concept of parent "1000"
		When sending POST to "/${branchPath}/concepts"
		Then return "400" status
		And return body with status "400"

	Scenario: New Concept with unknown FSN language refset
	
		Given branchPath "MAIN"
		And new concept with unknown refset
			req.withJson(#{
				"parentId" -> ROOT_CONCEPT,
				"moduleId" -> MODULE_SCT_CORE,
				"descriptions" -> #[
					#{
						"typeId" -> FULLY_SPECIFIED_NAME,
						"term" -> "New Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> incorrectAcceptabilityMap
					},
					#{
						"typeId" -> SYNONYM,
						"term" -> "New Preferred Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap 
					}
				],
				"commitComment" -> "New concept"
			})
		When sending POST to "/${branchPath}/concepts"
		Then return "400" status
		And return body with status "400"

	Scenario: New Concept in an unkown module
	
		Given branchPath "MAIN"
		And new concept with module "1"
			req.withJson(#{
				"parentId" -> ROOT_CONCEPT,
				"moduleId" -> args.first.renderWithFields(this),
				"descriptions" -> #[
					#{
						"typeId" -> FULLY_SPECIFIED_NAME,
						"term" -> "New Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap
					},
					#{
						"typeId" -> SYNONYM,
						"term" -> "New Preferred Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap 
					}
				],
				"commitComment" -> "New concept"
			})
		When sending POST to "/${branchPath}/concepts"
		Then return "400" status
		And return body with status "400"
		
	Scenario: New Concept request without commit comment
	
		Given branchPath "MAIN"
		And new concept request with commit comment ""
			req.withJson(#{
				"parentId" -> ROOT_CONCEPT,
				"moduleId" -> MODULE_SCT_CORE,
				"descriptions" -> #[
					#{
						"typeId" -> FULLY_SPECIFIED_NAME,
						"term" -> "New Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap
					},
					#{
						"typeId" -> SYNONYM,
						"term" -> "New Preferred Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap 
					}
				],
				"commitComment" -> args.first.renderWithFields(this)
			})
		When sending POST to "/${branchPath}/concepts"
		Then return "400" status
		And return body with status "400"

	Scenario: New Concept on SNOMED CT Main Branch
	
		Given branchPath "MAIN"
		And new concept
		When sending POST to "/${branchPath}/concepts"
		Then return "201" status
		And return location header pointing to "/${branchPath}/concepts"
			res.location should contain args.first.renderWithFields(this) 
		And return empty body
			res.getBody.asString => ""
	
	Scenario: New Concept with existing identifier
		
		var public conceptId = SnomedIdentifiers.generateConceptId()
		
		Given branchPath "MAIN"
		And new concept with id
			req.withJson(#{
				"id" -> conceptId,
				"parentId" -> ROOT_CONCEPT,
				"moduleId" -> MODULE_SCT_CORE,
				"descriptions" -> #[
					#{
						"typeId" -> FULLY_SPECIFIED_NAME,
						"term" -> "New Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap
					},
					#{
						"typeId" -> SYNONYM,
						"term" -> "New Preferred Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> correctAcceptabilityMap 
					}
				],
				"commitComment" -> "New concept"
			})
		When sending POST to "/${branchPath}/concepts"
		Then return "201" status
		And return location header pointing to "/${branchPath}/concepts/${conceptId}"
		And return empty body
		
	Scenario: New Concept on a newly created branch
	
		Given branchPath "MAIN/${branchName}"
		And new concept
		And a newly created branch at "/branches"
			API.postJson(#{"name" -> branchName}, args.first.renderWithFields(this)).expectStatus(201)
		When sending POST to "/${branchPath}/concepts"
		Then return "201" status
		And return location header pointing to "/${branchPath}/concepts"
		And return empty body
	
	Scenario: New Concept on a newly created branch with existing identifier
		
		var public conceptId = SnomedIdentifiers.generateConceptId()
		
		Given branchPath "MAIN/${branchName}"
		And new concept with id
		And a newly created branch at "/branches"
		When sending POST to "/${branchPath}/concepts"
		Then return "201" status
		And return location header pointing to "/${branchPath}/concepts/${conceptId}"
		And return empty body
			
	Scenario: New Concept on a deleted branch
		
		Given branchPath "MAIN/${branchName}"
		And a newly created branch at "/branches"
		And new concept
		And deleting branch at "/branches/${branchPath}"
			API.delete(args.first.renderWithFields(this)).expectStatus(204)	
		When sending POST to "/${branchPath}/concepts"
		Then return "400" status
		And return body with status "400"
		