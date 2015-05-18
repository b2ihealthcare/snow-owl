package com.b2international.snowowl.snomed.api.rest.components

import com.jayway.restassured.response.Response
import java.util.UUID

import com.b2international.snowowl.snomed.api.rest.components.*
import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*
import com.b2international.snowowl.snomed.SnomedConstants.Concepts
import com.b2international.snowowl.snomed.api.rest.branches.*
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
		var public String relationshipId
		var public branchName = UUID.randomUUID.toString
		
		// concept IDs available in the MiniCT FULL RF2
		val public DISEASE = "64572001"
		val public TEMPORAL_CONTEXT = "410510008"
		val public FINDING_CONTEXT = "408729009"
		val public SCT_CORE_MODULE = Concepts.MODULE_SCT_CORE
		var public Map<String, ?> json
		
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
			json = #{
				"active" -> false,
				"commitComment" -> "Inactivated"
			}
		When sending PUT to "/${branchPath}/relationships/${relationshipId}/updates"
			res = API.postJson(json, args.first.renderWithFields(this))
		Then return "204" status
		And "active" should be "false"
	
	Scenario: Create relationship on deep branch
		
	Scenario: Create relationship on deleted deep branch
