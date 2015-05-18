package com.b2international.snowowl.snomed.api.rest.components

import com.jayway.restassured.response.Response
import java.util.UUID

import com.b2international.snowowl.snomed.api.rest.components.*
import static extension com.b2international.snowowl.test.commons.rest.RestExtensions.*
import com.b2international.snowowl.snomed.SnomedConstants.Concepts

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
		val DISEASE = "64572001"
		val TEMPORAL_CONTEXT = "410510008"
		val FINDING_CONTEXT = "408729009"
		
	Scenario: New Relationship on non-existent SNOMED CT branch
		
		Given branchPath "MAIN/nonexistent"
		And new relationship
			req.withJson(#{
				"sourceId" -> DISEASE,
				"typeId" -> FINDING_CONTEXT,
				"destinationId" -> TEMPORAL_CONTEXT,
				"moduleId" -> Concepts.MODULE_SCT_CORE,
				"commitComment" -> "Created new relationship"
			})
		When sending POST to "/${branchPath}/relationships"
		Then return "404" status
		And return body with status "404"
		
	Scenario: New Relationship on SNOMED CT Main branch
		
		Given branchPath "MAIN"
		And new relationship
		When sending POST to "/${branchPath}/relationships"
		Then return "201" status
		
	Scenario: Delete relationship
		
	Scenario: Change relationship characteristictype
		
	Scenario: Inactivate relationship
	
	Scenario: Create relationship on branch
		
	Scenario: Create relationship on deleted branch
