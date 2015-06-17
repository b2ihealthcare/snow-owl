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
import java.util.Map
import java.util.Date
import java.util.UUID

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.*

import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*
import com.b2international.snowowl.snomed.api.rest.components.*
import com.b2international.snowowl.snomed.api.rest.branches.*
import com.b2international.snowowl.snomed.api.domain.Acceptability
import com.b2international.snowowl.snomed.api.domain.DefinitionStatus
import com.b2international.snowowl.snomed.api.domain.CaseSignificance

/**
 * @since 2.0
 */
Feature: SnomedMergeApi

	Background:
		static String API = "/snomed-ct/v2"

		var public String branchName = UUID.randomUUID.toString
		var public String componentId
		
		var RequestSpecification req
		var Response res

		val Map<String, String> componentMap = newHashMap()
		val metadata = newHashMap()
		val preferredAcceptabilityMap = #{ REFSET_LANGUAGE_TYPE_UK -> Acceptability.PREFERRED.name }

	Scenario: Accept merge attempt of new concept in FORWARD state 
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
			val parent = args.first.renderWithFields(this)
			val renderedBranchName = args.second.renderWithFields(this)
			req = givenAuthenticatedRequest(API).withJson(#{
				"parent" -> parent,
  				"name" -> renderedBranchName,
  				"metadata" -> metadata
			})
			
			res = req.post("/branches")
			res.expectStatus(201)
		When creating a new concept "C1" with URL "MAIN/${branchName}/concepts"
			req = givenAuthenticatedRequest(API).withJson(#{
				"parentId" -> ROOT_CONCEPT,
				"moduleId" -> MODULE_SCT_CORE,
				"descriptions" -> #[
					#{
						"typeId" -> FULLY_SPECIFIED_NAME,
						"term" -> "New Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> preferredAcceptabilityMap 
					},
					#{
						"typeId" -> SYNONYM,
						"term" -> "New Preferred Term at " + new Date(),
						"languageCode" -> "en",
						"acceptability" -> preferredAcceptabilityMap 
					}
				],
				"commitComment" -> "New concept"
			})
			
			val url = args.second.renderWithFields(this)
			res = req.post(url)
			res.expectStatus(201)
			res.location should contain url
			componentMap.put(args.first, res.location.lastPathSegment)
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Merge commit"
			req = givenAuthenticatedRequest(API).withJson(#{
				"source" -> args.first.renderWithFields(this),
				"target" -> args.second.renderWithFields(this),
				"commitComment" -> args.third
			})
			
			res = req.post("/merges")
		Then return "204" status
		And component "C1" should exist on URL base "MAIN/concepts"
			API.get(args.second.renderWithFields(this), componentMap.get(args.first)).expectStatus(200)
		And component "C1" should exist on URL base "MAIN/${branchName}/concepts"

	Scenario: Accept merge attempt of new description in FORWARD state
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
		When creating a new description "D1" with URL "MAIN/${branchName}/descriptions"
			req = givenAuthenticatedRequest(API).withJson(#{
				"conceptId" -> ROOT_CONCEPT,
				"moduleId" -> MODULE_SCT_CORE,
				"typeId" -> SYNONYM,
				"term" -> "New PT at " + new Date(),
				"languageCode" -> "en",
				"acceptability" -> preferredAcceptabilityMap,
				"commitComment" -> "New description"
			})
			
			val url = args.second.renderWithFields(this)
			res = req.post(url)
			res.expectStatus(201)
			res.location should contain url
			componentMap.put(args.first, res.location.lastPathSegment)
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Merge commit"
		Then return "204" status
		And component "D1" should exist on URL base "MAIN/descriptions"

	Scenario: Accept merge attempt of new relationship in FORWARD state
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
		When creating a new relationship "R1" with URL "MAIN/${branchName}/relationships"
			req = givenAuthenticatedRequest(API).withJson(#{
				"sourceId" -> ROOT_CONCEPT,
				"moduleId" -> MODULE_SCT_CORE,
				"typeId" -> "116676008", // Associated morphology
				"destinationId" -> "49755003", // Morphologic abnormality
				"commitComment" -> "New relationship"
			})
			
			val url = args.second.renderWithFields(this)
			res = req.post(url)
			res.expectStatus(201)
			res.location should contain url
			componentMap.put(args.first, res.location.lastPathSegment)
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Merge commit"
		Then return "204" status
		And component "R1" should exist on URL base "MAIN/relationships"

	Scenario: Reject merge attempt of new concept in DIVERGED state
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
		When creating a new concept "C1" with URL "MAIN/concepts"
		And creating a new concept "C2" with URL "MAIN/${branchName}/concepts"
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Merge commit"
		Then return "409" status
		And component "C1" should exist on URL base "MAIN/concepts"
		And component "C2" should exist on URL base "MAIN/${branchName}/concepts"

	Scenario: Reject merge attempt of new description in DIVERGED state
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
		When creating a new description "D1" with URL "MAIN/descriptions"
		And creating a new description "D2" with URL "MAIN/${branchName}/descriptions"
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Merge commit"
		Then return "409" status
		And component "D1" should exist on URL base "MAIN/descriptions"
		And component "D2" should exist on URL base "MAIN/${branchName}/descriptions"

	Scenario: Reject merge attempt of new relationship in DIVERGED state
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
		When creating a new relationship "R1" with URL "MAIN/relationships"
		And creating a new relationship "R2" with URL "MAIN/${branchName}/relationships"
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Merge commit"
		Then return "409" status
		And component "R1" should exist on URL base "MAIN/relationships"
		And component "R2" should exist on URL base "MAIN/${branchName}/relationships"

	Scenario: Accept rebase attempt of new concept in DIVERGED state
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
		When creating a new concept "C1" with URL "MAIN/concepts"
		And creating a new concept "C2" with URL "MAIN/${branchName}/concepts"
		And rebasing branch "MAIN/${branchName}" onto "MAIN" with comment "Rebase commit"
			res = API.postJson(#{
				"source" -> args.second.renderWithFields(this),
				"target" -> args.first.renderWithFields(this),
				"commitComment" -> args.third
			}, "merges")
		Then return "204" status
		And component "C1" should exist on URL base "MAIN/concepts"
		And component "C1" should exist on URL base "MAIN/${branchName}/concepts"
		And component "C2" should not exist on URL base "MAIN/concepts"
			API.get(args.second.renderWithFields(this), componentMap.get(args.first)).expectStatus(404)
		And component "C2" should exist on URL base "MAIN/${branchName}/concepts"

	Scenario: Accept rebase attempt of new description in DIVERGED state
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
		When creating a new description "D1" with URL "MAIN/descriptions"
		And creating a new description "D2" with URL "MAIN/${branchName}/descriptions"
		And rebasing branch "MAIN/${branchName}" onto "MAIN" with comment "Rebase commit"
		Then return "409" status
		And component "D1" should exist on URL base "MAIN/descriptions"
		And component "D2" should not exist on URL base "MAIN/descriptions"
		And component "D2" should exist on URL base "MAIN/${branchName}/descriptions"

	Scenario: Accept rebase attempt of new relationship in DIVERGED state
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
		When creating a new relationship "R1" with URL "MAIN/relationships"
		And creating a new relationship "R2" with URL "MAIN/${branchName}/relationships"
		And rebasing branch "MAIN/${branchName}" onto "MAIN" with comment "Rebase commit"
		Then return "204" status
		And component "R1" should exist on URL base "MAIN/relationships"
		And component "R1" should exist on URL base "MAIN/${branchName}/relationships"
		And component "R2" should not exist on URL base "MAIN/descriptions"
		And component "R2" should exist on URL base "MAIN/${branchName}/relationships"

	Scenario: Reject rebase attempt of changed description on branch and parent
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
		And creating a new description "D1" with URL "MAIN/${branchName}/descriptions"
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Description creation commit"
		And updating description case significance "D1" to "CASE_INSENSITIVE" with URL "MAIN/${branchName}/descriptions/${componentId}/updates"
			req = givenAuthenticatedRequest(API).withJson(#{
				"caseSignificance" -> CaseSignificance.valueOf(args.second).name,
				"commitComment" -> "Changed definition status"
			})
			
			componentId = componentMap.get(args.first);
			res = req.post(args.third.renderWithFields(this))
			res.expectStatus(204)
		And updating description case significance "D1" to "ENTIRE_TERM_CASE_SENSITIVE" with URL "MAIN/descriptions/${componentId}/updates"
		And rebasing branch "MAIN/${branchName}" onto "MAIN" with comment "Rebase commit"
		Then return "409" status

	Scenario: Reject rebase attempt of changed concept on branch, deleted on parent
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
		When creating a new concept "C1" with URL "MAIN/${branchName}/concepts"
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Concept creation commit"
		And updating concept definition status "C1" to "FULLY_DEFINED" with URL "MAIN/${branchName}/concepts/${componentId}/updates"
			req = givenAuthenticatedRequest(API).withJson(#{
				"definitionStatus" -> DefinitionStatus.valueOf(args.second).name,
				"commitComment" -> "Changed definition status"
			})
			
			componentId = componentMap.get(args.first);
			res = req.post(args.third.renderWithFields(this))
			res.expectStatus(204)
		And deleting component "C1" with URL "MAIN/concepts/${componentId}"
			componentId = componentMap.get(args.first);
			res = req.delete(args.second.renderWithFields(this))
			res.expectStatus(204)
		And rebasing branch "MAIN/${branchName}" onto "MAIN" with comment "Rebase commit"
		Then return "409" status

	Scenario: Accept rebase attempt of changed concept on parent, deleted on branch, concept stays deleted
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
		When creating a new concept "C1" with URL "MAIN/${branchName}/concepts"
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Concept creation commit"
		And updating concept definition status "C1" to "FULLY_DEFINED" with URL "MAIN/concepts/${componentId}/updates"
		And deleting component "C1" with URL "MAIN/${branchName}/concepts/${componentId}"
		And rebasing branch "MAIN/${branchName}" onto "MAIN" with comment "Rebase commit"
		Then return "204" status
		And component "C1" should not exist on URL base "MAIN/${branchName}/concepts"
		And component "C1" should exist on URL base "MAIN/concepts"

	Scenario: Accept rebase and merge attempt of changed concept on parent, deleted on branch, concept stays deleted
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
		When creating a new concept "C1" with URL "MAIN/${branchName}/concepts"
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Concept creation commit"
		And updating concept definition status "C1" to "FULLY_DEFINED" with URL "MAIN/concepts/${componentId}/updates"
		And deleting component "C1" with URL "MAIN/${branchName}/concepts/${componentId}"
		And rebasing branch "MAIN/${branchName}" onto "MAIN" with comment "Rebase commit"
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Merge commit"
		Then return "204" status
		And component "C1" should not exist on URL base "MAIN/${branchName}/concepts"
		And component "C1" should not exist on URL base "MAIN/concepts"
		
	Scenario: Reject rebase attempt of changed description on branch and parent with multiple changes
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
		And creating a new description "D1" with URL "MAIN/${branchName}/descriptions"
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Description creation commit"
		And updating description case significance "D1" to "CASE_INSENSITIVE" with URL "MAIN/${branchName}/descriptions/${componentId}/updates"
		And updating description module identifier "D1" to "900000000000013009" with URL "MAIN/${branchName}/descriptions/${componentId}/updates"
			req = givenAuthenticatedRequest(API).withJson(#{
				"moduleId" -> args.second,
				"commitComment" -> "Changed module identifier"
			})
			
			componentId = componentMap.get(args.first);
			res = req.post(args.third.renderWithFields(this))
			res.expectStatus(204)
		And updating description case significance "D1" to "ENTIRE_TERM_CASE_SENSITIVE" with URL "MAIN/descriptions/${componentId}/updates"
		And updating description module identifier "D1" to "900000000000443000" with URL "MAIN/descriptions/${componentId}/updates"
		And rebasing branch "MAIN/${branchName}" onto "MAIN" with comment "Rebase commit"
		Then return "409" status

	Scenario: Accept rebase and merge attempt of changed description on branch with multiple changes
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
		And creating a new description "D1" with URL "MAIN/${branchName}/descriptions"
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Description creation commit"
		And creating a new description "D2" with URL "MAIN/descriptions"
		And updating description case significance "D1" to "CASE_INSENSITIVE" with URL "MAIN/${branchName}/descriptions/${componentId}/updates"
		And updating description module identifier "D1" to "900000000000013009" with URL "MAIN/${branchName}/descriptions/${componentId}/updates"
		And rebasing branch "MAIN/${branchName}" onto "MAIN" with comment "Rebase commit"
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Merge commit"
		Then return "204" status
		And component "D1" should exist on URL base "MAIN/descriptions"
		And component "D2" should exist on URL base "MAIN/descriptions"
		And component "D1" should be "CASE_INSENSITIVE" for JSON path "caseSignificance" with URL "MAIN/descriptions/${componentId}"
			componentId = componentMap.get(args.first)
			res = API.get(args.forth.renderWithFields(this))
			res.expectStatus(200)
			res.jsonPath.getString(args.third) should be args.second
		And component "D1" should be "900000000000013009" for JSON path "moduleId" with URL "MAIN/descriptions/${componentId}"

	Scenario: Accept rebase and merge attempt of deleted description on branch and parent
		Given a SNOMED CT branch under parent branch "MAIN" with name "${branchName}"
		And creating a new description "D1" with URL "MAIN/${branchName}/descriptions"
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Description creation commit"

		And deleting component "D1" with URL "MAIN/descriptions/${componentId}"

		And deleting component "D1" with URL "MAIN/${branchName}/descriptions/${componentId}"
		And creating a new description "D2" with URL "MAIN/${branchName}/descriptions"

		And rebasing branch "MAIN/${branchName}" onto "MAIN" with comment "Rebase commit" 
		And return "204" status
		And merging changes from branch "MAIN/${branchName}" to "MAIN" with comment "Merge commit"

		Then return "204" status

		And component "D1" should not exist on URL base "MAIN/descriptions"
		And component "D1" should not exist on URL base "MAIN/${branchName}/descriptions"
		And component "D2" should exist on URL base "MAIN/descriptions"
		And component "D2" should exist on URL base "MAIN/${branchName}/descriptions"
