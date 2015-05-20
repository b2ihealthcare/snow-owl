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

/**
 * @since 2.0
 */
Feature: SnomedRelationshipApi

	Background:
		static String API = "/snomed-ct/v2"
		var req = givenAuthenticatedRequest(API)
		var Response res
		var public String branchPath
		var public String relationshipId
		var public branchName = UUID.randomUUID.toString
		
		// concept IDs available in the MiniCT FULL RF2
		val public DISEASE = "64572001"
		val public TEMPORAL_CONTEXT = "410510008"
		val public FINDING_CONTEXT = "408729009"
		val public SCT_CORE_MODULE = Concepts.MODULE_SCT_CORE
		
	Scenario: New Relationship on non-existent SNOMED CT branch
		
		Given branchPath "MAIN/nonexistent"
		And new relationship "${DISEASE}" - "${FINDING_CONTEXT}" - "${TEMPORAL_CONTEXT}" with module "${SCT_CORE_MODULE}" and comment "Created new relationship"
			req.withJson(#{
				"sourceId" -> args.first.renderWithFields(this),
				"typeId" -> args.second.renderWithFields(this),
				"destinationId" -> args.third.renderWithFields(this),
				"moduleId" -> args.forth.renderWithFields(this),
				"commitComment" -> args.fifth
			})
		When sending POST to "/${branchPath}/relationships"
		Then return "404" status
		And return body with status "404"
		
	Scenario: New Relationship with non-existent source Concept
		
		Given branchPath "MAIN"
		And new relationship "NON_EXISTENT" - "${FINDING_CONTEXT}" - "${TEMPORAL_CONTEXT}" with module "${SCT_CORE_MODULE}" and comment "Created new relationship"
		When sending POST to "/${branchPath}/relationships"
		Then return "400" status
		And return body with status "400"
	
	Scenario: New Relationship with non-existent type Concept
		
		Given branchPath "MAIN"
		And new relationship "${DISEASE}" - "NON_EXISTENT" - "${TEMPORAL_CONTEXT}" with module "${SCT_CORE_MODULE}" and comment "Created new relationship"
		When sending POST to "/${branchPath}/relationships"
		Then return "400" status
		And return body with status "400"
	
	Scenario: New Relationship with non-existent destination Concept
		
		Given branchPath "MAIN"
		And new relationship "${DISEASE}" - "${FINDING_CONTEXT}" - "NON_EXISTENT" with module "${SCT_CORE_MODULE}" and comment "Created new relationship"
		When sending POST to "/${branchPath}/relationships"
		Then return "400" status
		And return body with status "400"
		
	Scenario: New Relationship with non-existent module Concept
		
		Given branchPath "MAIN"
		And new relationship "${DISEASE}" - "${FINDING_CONTEXT}" - "${TEMPORAL_CONTEXT}" with module "NON_EXISTENT" and comment "Created new relationship"
		When sending POST to "/${branchPath}/relationships"
		Then return "400" status
		And return body with status "400"
		
	Scenario: New Relationship on SNOMED CT Main branch
		
		Given branchPath "MAIN"
		And new relationship "${DISEASE}" - "${FINDING_CONTEXT}" - "${TEMPORAL_CONTEXT}" with module "${SCT_CORE_MODULE}" and comment "Created new relationship"
		When sending POST to "/${branchPath}/relationships"
		Then return "201" status
		And return location header pointing to "/${branchPath}/relationships"
		And extracted relationship id
			relationshipId = res.location.lastPathSegment
		And "characteristicType" should be "STATED_RELATIONSHIP"
			res = API.get(branchPath, "relationships", relationshipId)
			res.expectStatus(200)
			res.getBody.path(args.first).toString should be args.second
			
	Scenario: New Defining Relationship on SNOMED CT Main branch
		
		Given branchPath "MAIN"
		And new defining relationship "${DISEASE}" - "${FINDING_CONTEXT}" - "${TEMPORAL_CONTEXT}" with module "${SCT_CORE_MODULE}" and comment "Created new relationship"
			req.withJson(#{
				"sourceId" -> args.first.renderWithFields(this),
				"typeId" -> args.second.renderWithFields(this),
				"destinationId" -> args.third.renderWithFields(this),
				"moduleId" -> args.forth.renderWithFields(this),
				"characteristicType" -> "DEFINING_RELATIONSHIP",
				"commitComment" -> args.fifth
			})
		When sending POST to "/${branchPath}/relationships"
		Then return "201" status
		And return location header pointing to "/${branchPath}/relationships"
		And extracted relationship id
		And "characteristicType" should be "DEFINING_RELATIONSHIP"
		
	Scenario: Delete relationship
		
		Given branchPath "MAIN"
		And new relationship "${DISEASE}" - "${FINDING_CONTEXT}" - "${TEMPORAL_CONTEXT}" with module "${SCT_CORE_MODULE}" and comment "Created new relationship"
		And sending POST to "/${branchPath}/relationships"
		And extracted relationship id
		When sending DELETE to "/${branchPath}/relationships/${relationshipId}"
			res = API.delete(args.first.renderWithFields(this))
		Then return "204" status
		And relationship should be deleted
			API.get(branchPath, "relationships", relationshipId).expectStatus(404)
		
	Scenario: Inactivate relationship
		
		Given branchPath "MAIN"
		And new relationship "${DISEASE}" - "${FINDING_CONTEXT}" - "${TEMPORAL_CONTEXT}" with module "${SCT_CORE_MODULE}" and comment "Created new relationship"
		And sending POST to "/${branchPath}/relationships"
		And extracted relationship id
		And inactivation request
			req.withJson(#{
				"active" -> false,
				"commitComment" -> "Inactivated"
			})
		When sending POST to "/${branchPath}/relationships/${relationshipId}/updates"
		Then return "204" status
		And "active" should be "false"
	
	Scenario: Create relationship on deep branch
		
		Given branchPath "MAIN/${branchName}/b/c"
		And newly created branch "${branchName}" under "MAIN"
			val name = args.first.renderWithFields(this)
			val parent = args.second.renderWithFields(this)
			API.postJson(#{"parent" -> parent, "name" -> name}, "branches").expectStatus(201)
		And newly created branch "b" under "MAIN/${branchName}"
		And newly created branch "c" under "MAIN/${branchName}/b"
		And new relationship "${DISEASE}" - "${FINDING_CONTEXT}" - "${TEMPORAL_CONTEXT}" with module "${SCT_CORE_MODULE}" and comment "Created new relationship"
		When sending POST to "/${branchPath}/relationships"
		Then return "201" status
		And return location header pointing to "/${branchPath}/relationships"
		And extracted relationship id
		And "characteristicType" should be "STATED_RELATIONSHIP"
		
	Scenario: Create relationship on deleted deep branch
		
		Given branchPath "MAIN/${branchName}/b/c"
		And newly created branch "${branchName}" under "MAIN"
		And newly created branch "b" under "MAIN/${branchName}"
		And newly created branch "c" under "MAIN/${branchName}/b"
		And new relationship "${DISEASE}" - "${FINDING_CONTEXT}" - "${TEMPORAL_CONTEXT}" with module "${SCT_CORE_MODULE}" and comment "Created new relationship"
		And sending POST to "/${branchPath}/relationships"
		And return location header pointing to "/${branchPath}/relationships"
		And extracted relationship id
		When deleting relationship at "/${branchPath}/relationships/${relationshipId}"
			res = API.delete(args.first.renderWithFields(this))
		Then return "204" status 
		And it should be deleted
			API.get(branchPath, "relationships", relationshipId).expectStatus(404)