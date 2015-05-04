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
import com.jayway.restassured.specification.RequestSpecification
import java.util.Date
import java.util.UUID

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.*

import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*
import com.b2international.snowowl.snomed.api.rest.concept.*
import com.b2international.snowowl.snomed.api.rest.branches.*

/**
 * @since 2.0
 */
Feature: SnomedMergeApi

	Background:
		static String API = "/snomed-ct/v2"

		var public String parent = "MAIN"
		var public String branchName = UUID.randomUUID.toString
		var String conceptId
		
		var RequestSpecification req
		var Response res

		val metadata = newHashMap()
		val correctAcceptabilityMap = #{ REFSET_LANGUAGE_TYPE_UK -> "PREFERRED" }

	Scenario: Clean merge of new concept on branch 
	
		Given a SNOMED CT branch under parent branch "${parent}" with name "${branchName}"
			parent = args.first.renderWithFields(this)
			branchName = args.second.renderWithFields(this)
			req = givenAuthenticatedRequest(API).withJson(#{
				"parent" -> parent,
  				"name" -> branchName,
  				"metadata" -> metadata
			})
			
			res = req.post("/branches")
			res.expectStatus(201)
		When creating a new concept on "${parent}/${branchName}"
			req = givenAuthenticatedRequest(API).withJson(#{
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
			
			res = req.post(args.first.renderWithFields(this) + "/concepts")
			res.expectStatus(201)
			res.location should contain args.first.renderWithFields(this) + "/concepts"
			conceptId = res.location.lastPathSegment
		And merging changes on branch "${parent}/${branchName}" to "${parent}" with comment "Merge commit"
			req = givenAuthenticatedRequest(API).withJson(#{
				"source" -> args.first.renderWithFields(this),
				"target" -> args.second.renderWithFields(this),
				"commitComment" -> args.third
			})
			
			res = req.post("/merges")
		Then return "204" status
		And the concept should exist on branch "${parent}"
			API.get(args.first.renderWithFields(this), "concepts", conceptId).expectStatus(200)
		And the concept should exist on branch "${parent}/${branchName}"
