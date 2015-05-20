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
import java.util.UUID

import com.b2international.snowowl.snomed.api.rest.components.*
import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*
import com.b2international.snowowl.snomed.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.api.rest.branches.*

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.*
import com.b2international.snowowl.snomed.api.domain.Acceptability
import com.b2international.snowowl.snomed.api.domain.CaseSignificance

/**
 * @since 2.0
 */
Feature: SnomedDescriptionApi

	Background:
		static String API = "/snomed-ct/v2"
		var req = givenAuthenticatedRequest(API)
		var Response res
		var public String branchPath
		var public String descriptionId
		var public branchName = UUID.randomUUID.toString
		
		// concept IDs available in the MiniCT FULL RF2
		val public DISEASE = "64572001"
		val public TEMPORAL_CONTEXT = "410510008"
		val public FINDING_CONTEXT = "408729009"
		
		val public MODULE_SCT_CORE_ID = MODULE_SCT_CORE
		val public SYNONYM_ID = SYNONYM
		
		val acceptableAcceptabilityMap = #{ REFSET_LANGUAGE_TYPE_UK -> Acceptability.ACCEPTABLE.name }
		val preferredAcceptabilityMap = #{ REFSET_LANGUAGE_TYPE_UK -> Acceptability.PREFERRED.name }
		
	Scenario: New description on non-existent SNOMED CT branch
		
		Given branchPath "MAIN/nonexistent"
		And new description "${DISEASE}" - "Rare disease" with module "${MODULE_SCT_CORE_ID}" and type "${SYNONYM_ID}" and comment "Created new description"
			req.withJson(#{
				"conceptId" -> args.first.renderWithFields(this),
				"moduleId" -> args.third.renderWithFields(this),
				"typeId" -> args.forth.renderWithFields(this),
				"term" -> args.second.renderWithFields(this),
				"languageCode" -> "en",
				"acceptability" -> acceptableAcceptabilityMap,
				"commitComment" -> args.fifth
			})
		When sending POST to "/${branchPath}/descriptions"
		Then return "404" status
		And return body with status "404"
		
	Scenario: New description with non-existent source Concept
		
		Given branchPath "MAIN"
		And new description "NON_EXISTENT" - "Rare disease" with module "${MODULE_SCT_CORE_ID}" and type "${SYNONYM_ID}" and comment "Created new description"
		When sending POST to "/${branchPath}/descriptions"
		Then return "400" status
		And return body with status "400"
	
	Scenario: New description with non-existent type Concept
		
		Given branchPath "MAIN"
		And new description "${DISEASE}" - "Rare disease" with module "${MODULE_SCT_CORE_ID}" and type "NON_EXISTENT" and comment "Created new description"
		When sending POST to "/${branchPath}/descriptions"
		Then return "400" status
		And return body with status "400"
		
	Scenario: New description with non-existent module Concept
		
		Given branchPath "MAIN"
		And new description "${DISEASE}" - "Rare disease" with module "NON_EXISTENT" and type "${SYNONYM_ID}" and comment "Created new description"
		When sending POST to "/${branchPath}/descriptions"
		Then return "400" status
		And return body with status "400"
		
	Scenario: New description on SNOMED CT Main branch
		
		Given branchPath "MAIN"
		And new description "${DISEASE}" - "Rare disease" with module "${MODULE_SCT_CORE_ID}" and type "${SYNONYM_ID}" and comment "Created new description"
		When sending POST to "/${branchPath}/descriptions"
		Then return "201" status
		And return location header pointing to "/${branchPath}/descriptions"
		And extracted description id
			descriptionId = res.location.lastPathSegment
		And "caseSignificance" should be "INITIAL_CHARACTER_CASE_INSENSITIVE"
			res = API.get(branchPath, "descriptions", descriptionId)
			res.expectStatus(200)
			res.getBody.path(args.first).toString should be args.second
			
	Scenario: New case sensitive description on SNOMED CT Main branch
		
		Given branchPath "MAIN"
		And new case insensitive description "${DISEASE}" - "Rare disease" with module "${MODULE_SCT_CORE_ID}" and type "${SYNONYM_ID}" and comment "Created new description"
			req.withJson(#{
				"conceptId" -> args.first.renderWithFields(this),
				"moduleId" -> args.third.renderWithFields(this),
				"typeId" -> args.forth.renderWithFields(this),
				"term" -> args.second.renderWithFields(this),
				"languageCode" -> "en",
				"acceptability" -> acceptableAcceptabilityMap,
				"caseSignificance" -> CaseSignificance.CASE_INSENSITIVE.toString,
				"commitComment" -> args.fifth
			})
		When sending POST to "/${branchPath}/descriptions"
		Then return "201" status
		And return location header pointing to "/${branchPath}/descriptions"
		And extracted description id
		And "caseSignificance" should be "CASE_INSENSITIVE"
		
	Scenario: Delete description
		
		Given branchPath "MAIN"
		And new description "${DISEASE}" - "Rare disease" with module "${MODULE_SCT_CORE_ID}" and type "${SYNONYM_ID}" and comment "Created new description"
		And sending POST to "/${branchPath}/descriptions"
		And extracted description id
		When sending DELETE to "/${branchPath}/descriptions/${descriptionId}"
			res = API.delete(args.first.renderWithFields(this))
		Then return "204" status
		And description should be deleted
			API.get(branchPath, "descriptions", descriptionId).expectStatus(404)
		
	Scenario: Inactivate description
		
		Given branchPath "MAIN"
		And new description "${DISEASE}" - "Rare disease" with module "${MODULE_SCT_CORE_ID}" and type "${SYNONYM_ID}" and comment "Created new description"
		And sending POST to "/${branchPath}/descriptions"
		And extracted description id
		And inactivation request
		When sending POST to "/${branchPath}/descriptions/${descriptionId}/updates"
		Then return "204" status
		And "active" should be "false"
	
	Scenario: Change description case significance
		Given branchPath "MAIN"
		And new description "${DISEASE}" - "Rare disease" with module "${MODULE_SCT_CORE_ID}" and type "${SYNONYM_ID}" and comment "Created new description"
		And sending POST to "/${branchPath}/descriptions"
		And extracted description id
		And changing case significance to "CASE_INSENSITIVE"
			req.withJson(#{
				"caseSignificance" -> args.first,
				"commitComment" -> "Updating case significance"
			})
		When sending POST to "/${branchPath}/descriptions/${descriptionId}/updates"
		Then return "204" status
		And "caseSignificance" should be "CASE_INSENSITIVE"

	Scenario: Change description acceptability
		Given branchPath "MAIN"
		And new description "${DISEASE}" - "Rare disease" with module "${MODULE_SCT_CORE_ID}" and type "${SYNONYM_ID}" and comment "Created new description"
		And sending POST to "/${branchPath}/descriptions"
		And extracted description id
		And changing acceptability to preferred
			req.withJson(#{
				"acceptability" -> preferredAcceptabilityMap,
				"commitComment" -> "Updating acceptability"
			})
		When sending POST to "/${branchPath}/descriptions/${descriptionId}/updates"
		Then return "204" status
		And the preferred term of "${DISEASE}" should be "Rare disease"
			res = givenAuthenticatedRequest(API)
				.headers(#{ "Accept-Language" -> "en-GB" })
				.get(asPath(#[ branchPath, "concepts", args.first.renderWithFields(this), "pt" ]))
				
			res.expectStatus(200)
			res.getBody.path("term").toString should be args.second

	Scenario: Create description on deep branch
		
		Given branchPath "MAIN/${branchName}/b/c"
		And newly created branch "${branchName}" under "MAIN"
			val name = args.first.renderWithFields(this)
			val parent = args.second.renderWithFields(this)
			API.postJson(#{"parent" -> parent, "name" -> name}, "branches").expectStatus(201)
		And newly created branch "b" under "MAIN/${branchName}"
		And newly created branch "c" under "MAIN/${branchName}/b"
		And new description "${DISEASE}" - "Rare disease" with module "${MODULE_SCT_CORE_ID}" and type "${SYNONYM_ID}" and comment "Created new description"
		When sending POST to "/${branchPath}/descriptions"
		Then return "201" status
		And return location header pointing to "/${branchPath}/descriptions"
		And extracted description id
		And "caseSignificance" should be "INITIAL_CHARACTER_CASE_INSENSITIVE"
		
	Scenario: Create description on deleted deep branch
		
		Given branchPath "MAIN/${branchName}/b/c"
		And newly created branch "${branchName}" under "MAIN"
		And newly created branch "b" under "MAIN/${branchName}"
		And newly created branch "c" under "MAIN/${branchName}/b"
		And new description "${DISEASE}" - "Rare disease" with module "${MODULE_SCT_CORE_ID}" and type "${SYNONYM_ID}" and comment "Created new description"
		And sending POST to "/${branchPath}/descriptions"
		And return location header pointing to "/${branchPath}/descriptions"
		And extracted description id
		When deleting description at "/${branchPath}/descriptions/${descriptionId}"
			res = API.delete(args.first.renderWithFields(this))
		Then return "204" status 
		And it should be deleted
			API.get(branchPath, "descriptions", descriptionId).expectStatus(404)
